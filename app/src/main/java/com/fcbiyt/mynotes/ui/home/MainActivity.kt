package com.fcbiyt.mynotes.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcbiyt.mynotes.R
import com.fcbiyt.mynotes.data.database.entities.NoteEntity
import com.fcbiyt.mynotes.databinding.ActivityMainBinding
import com.fcbiyt.mynotes.ui.NoteViewModel
import com.fcbiyt.mynotes.ui.addEditNote.AddEditNoteActivity
import com.fcbiyt.mynotes.ui.addEditNote.EXTRA_DESCRIPTION
import com.fcbiyt.mynotes.ui.addEditNote.EXTRA_ID
import com.fcbiyt.mynotes.ui.addEditNote.EXTRA_PRIORITY
import com.fcbiyt.mynotes.ui.addEditNote.EXTRA_TITLE
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
//Definir los estados de solicitud
const val ADD_NOTE_REQUEST = 1
const val EDIT_NOTE_REQUEST = 2
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    //Instanciamos nuestro viewModel
    private lateinit var vm: NoteViewModel

    //Crespues de crearlo se instancia el adapter
    private lateinit var adapter: NoteAdapter

    private val miCoroutineScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()

        setUpListeners()

        /**
         Crear una instancia del ViewModel y asociarla a nuestro componente MainActivity
         y especificar el tipo de viewModel que queremos obtener o crear, en este caso NoteVieModel
        */
        vm = ViewModelProvider(this)[NoteViewModel::class.java]

        /**
         Accedemos al metodo getAllNotes de nuestro vm que nos retorna un LiveData de las notas locales

         Recordemos que su camino para acceder a la informacion es:
         NoteViewModel(Interaccion con la UI) -> NoteRepository(Interaccion con fuente de datos) -> NoteDao(CONSULTAS SQL)

         Finalmente usamos submitList para mostrar la lista obtenida en la vista UI
        */
        vm.getAllNotes().observe(this) {localNotes ->
            adapter.submitList(localNotes)
        }
    }

    private fun setUpListeners() {
        binding.buttonAddNote.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        // swipe listener
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = adapter.getNoteAt(viewHolder.adapterPosition)
                miCoroutineScope.launch {
                    vm.delete(note)
                }
            }

        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun setUpRecyclerView() {
        //Definir el layoutManager para indicar la disposicion de los elementos
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        //Indicar que el recyclerview tendra una tamaño fijo no dinamico y optimizar el renderizado
        binding.recyclerView.setHasFixedSize(true)

        //Pasar la funcion lambda que recibe como parametro nuestro adapter para un evento clic de
        // cada elemento de la lista y pasamos los extras a las constantes definidas en nuestro
        //activity destino
        adapter = NoteAdapter { clickedNote ->
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra(EXTRA_ID, clickedNote.id)
            intent.putExtra(EXTRA_TITLE, clickedNote.title)
            intent.putExtra(EXTRA_DESCRIPTION, clickedNote.description)
            intent.putExtra(EXTRA_PRIORITY, clickedNote.priority)
            startActivityForResult(intent, EDIT_NOTE_REQUEST)
        }
        //Finalmente asignamos nuestro adapter personalizado al adapter del recyclerview
        binding.recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data != null && requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val title: String = data.getStringExtra(EXTRA_TITLE)!!
            val description: String =
                data.getStringExtra(EXTRA_DESCRIPTION)!!
            val priority: Int = data.getIntExtra(EXTRA_PRIORITY, -1)
            miCoroutineScope.launch {
                vm.insertAndGetId(NoteEntity(title, description, priority))
            }
        }
        else if(data != null && requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = data.getIntExtra(EXTRA_ID, -1)
            if(id == -1) {
                Snackbar.make(binding.root, "La nota no se puede actualizar", Snackbar.LENGTH_SHORT).show()
                return
            }
            val title: String = data.getStringExtra(EXTRA_TITLE)!!
            val description: String =
                data.getStringExtra(EXTRA_DESCRIPTION)!!
            val priority: Int = data.getIntExtra(EXTRA_PRIORITY, -1)
            miCoroutineScope.launch {
                vm.update(NoteEntity(title, description, priority, id))
            }

            Snackbar.make(binding.root, "Nota actualizada", Snackbar.LENGTH_SHORT).show()

        } else {
            Snackbar.make(binding.root, "Nota no guardada", Snackbar.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                miCoroutineScope.launch {
                    vm.deleteAllNotes()
                }
                Toast.makeText(this, "Se han eliminado todas las notas", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Importante!! Al utilizar corrutinas entendemos que los procesos que se llevan a cabo
     * dentro de ellas se ejecutan en un hilo secundario, por lo que si nosotros por alguna
     * razon iniciamos una corrutina en un activity y en seguida pasamos a otro activity y aun
     * no ha finalizado nuestra corrutina iniciada en el activity anterior, si hay algun dato
     * que deba renderizarse dentro de la corrutina, causará un error que cerrará la aplicacion
     * por que el elemento de UI sobre el que la informacion retornada por la corrutina necesita
     * renderizarse ya no existe en la vista actual.
     *
     * esto aplica para componentes visuales o contexto.
     */
    override fun onDestroy() {
        miCoroutineScope.cancel()
        super.onDestroy()
    }
}