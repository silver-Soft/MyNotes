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
import com.fcbiyt.mynotes.core.NetworkUtils
import com.fcbiyt.mynotes.data.database.entities.NoteEntity
import com.fcbiyt.mynotes.data.dto.NewNote
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
import kotlinx.coroutines.withContext
import org.json.JSONObject

const val ADD_NOTE_REQUEST = 1
const val EDIT_NOTE_REQUEST = 2
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var vm: NoteViewModel
    private lateinit var adapter: NoteAdapter

    private val miCoroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Importar el NetworkUtils para validar conexion
     */
    private val networkUtils = NetworkUtils(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()

        setUpListeners()

        vm = ViewModelProvider(this)[NoteViewModel::class.java]

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
                vm.delete(note)
                miCoroutineScope.launch {
                    if(networkUtils.isNetworkAvailable()){
                        deleteNoteOnCloud(note.id)
                    }
                }
            }

        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        adapter = NoteAdapter { clickedNote ->
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra(EXTRA_ID, clickedNote.id)
            intent.putExtra(EXTRA_TITLE, clickedNote.title)
            intent.putExtra(EXTRA_DESCRIPTION, clickedNote.description)
            intent.putExtra(EXTRA_PRIORITY, clickedNote.priority)
            startActivityForResult(intent, EDIT_NOTE_REQUEST)
        }
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
                /**
                 * Obtener el id que retorna insertAndGetId para enviarlo al API de guardar nueva nota
                 * ya con el id que se le asigno localmente
                 */
                val insertedId = vm.insertAndGetId(NoteEntity(title, description, priority))

                if (insertedId > 0) {
                    // Aquí puedes usar el ID que se ha insertado en la base de datos.
                    val body = NewNote(
                        data.getStringExtra(EXTRA_TITLE)!!,
                        data.getStringExtra(EXTRA_DESCRIPTION)!!,
                        data.getIntExtra(EXTRA_PRIORITY, -1)
                    )
                    if(networkUtils.isNetworkAvailable()){
                        saveNoteOnCloud(body,insertedId)
                    }else{
                        /**
                         * Si no hay conexion a internet se pueden guardar las notas no sincronizadas
                         * en una nueva tabla por ejemplo not_sync_notes_table
                         * para posteriormente en un nuevo ingreso a la app verificar si existen notas
                         * en esta tabla, de ser asi subir cada una de ellas recorriendo la lista de notas
                         * de esta tabla con el metodo

                                    saveNoteOnCloud(body,insertedId)

                         *Asegurate de vacíar esta tabla una vez sincronizadas las notas que habia en ella.
                         */
                    }
                    Snackbar.make(binding.root, "Nota insertada", Snackbar.LENGTH_SHORT).show()
                }
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
            if(networkUtils.isNetworkAvailable()){
                val body = NewNote(
                    data.getStringExtra(EXTRA_TITLE)!!,
                    data.getStringExtra(EXTRA_DESCRIPTION)!!,
                    data.getIntExtra(EXTRA_PRIORITY, -1)
                )
                miCoroutineScope.launch {
                    updateNoteOnCloud(body,id)
                    /**
                     * Aqui podemos cachar excepciones de guardado del API y mostrar un mensaje
                     * de a cuerdo a la respuesta
                     */
                }
            }else{
                /**
                 * Si no hay conexion a internet se pueden guardar las notas no sincronizadas
                 * en una nueva tabla por ejemplo not_sync_notes_table
                 * para posteriormente en un nuevo ingreso a la app verificar si existen notas
                 * en esta tabla, de ser asi subir cada una de ellas recorriendo la lista de notas
                 * de esta tabla con el metodo

                                updateNoteOnCloud(body,id)

                 *Asegurate de vacíar esta tabla una vez sincronizadas las notas que habia en ella.
                 */
            }

            Snackbar.make(binding.root, "Nota actualizada!", Snackbar.LENGTH_SHORT).show()

        } else {
            Snackbar.make(binding.root, "Nota no guardada", Snackbar.LENGTH_SHORT).show()
        }
    }


    /**
     * Crear las suspend fun para llamar a los metodos del ViewModel que operan con el API
     */
    private suspend fun saveNoteOnCloud(body: NewNote, idNote: Number): JSONObject? = withContext(Dispatchers.IO) {
        val result = vm.saveNoteOnCloud(body, "notes/$idNote")
        result
    }
    private suspend fun updateNoteOnCloud(body: NewNote, idNote: Number): JSONObject? = withContext(Dispatchers.IO) {
        val result = vm.updateNoteOnCloud(body, "notes/$idNote")
        result
    }
    private suspend fun deleteNoteOnCloud(idNote: Number): JSONObject? = withContext(Dispatchers.IO) {
        val result = vm.deleteNoteOnCloud("notes/$idNote")
        result
    }
    private suspend fun deleteAllNotesOnCloud(): JSONObject? = withContext(Dispatchers.IO) {
        val result = vm.deleteAllNotesOnCloud("notes")
        result
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                vm.deleteAllNotes()
                miCoroutineScope.launch {
                    if(networkUtils.isNetworkAvailable()){
                        deleteAllNotesOnCloud()
                    }
                }
                Toast.makeText(this, "Se han eliminado todas las notas", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        miCoroutineScope.cancel()
        super.onDestroy()
    }
}