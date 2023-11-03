package com.fcbiyt.mynotes.ui.addEditNote

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.fcbiyt.mynotes.R
import com.fcbiyt.mynotes.databinding.ActivityAddEditNoteBinding
import com.google.android.material.snackbar.Snackbar

const val EXTRA_ID = " com.fcbiyt.mynotes.EXTRA_ID"
const val EXTRA_TITLE = " com.fcbiyt.mynotes.EXTRA_TITLE"
const val EXTRA_DESCRIPTION = " com.fcbiyt.mynotes.EXTRA_DESCRIPTION"
const val EXTRA_PRIORITY = " com.fcbiyt.mynotes.EXTRA_PRIORITY"

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddEditNoteBinding

    //Declaramos el objeto mode que sera un objeto de clase sellada para determinar
    //el estado de la actividad EXPLICAR QUE ES UNA sealed class
    private lateinit var mode : Mode

    //Definimos un valor por defecto en nuestra ID = -1 que sera nuestro trigger
    // de NuevaNota en caso de mantenerse en -1
    private var noteId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Habilitamos  el actionbar y definimos un icono para home
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        /**
         Obtenemos el EXTRA_ID si existe y lo asignamos al noteId para determinar
         si la nota será editada o en caso de que no llege el extra crear una nueva nota
         */
        noteId = intent.getIntExtra(EXTRA_ID, -1)
        mode = if(noteId == -1) Mode.AddNote
        else Mode.EditNote

        /**
         Mediante la funcion when verificamos el estado/modo actual del activity
         si esta en el modo AddNote Agregamos un titulo en el action bar como nueva nota
         y definimos el valor de la prioridad en 1

         si esta en el estado/modo EditNote obtenemos los extras del intent, los cuales contienen
         la informacion de la nota, como el titulo, la descripcion y prioridad, para mostrarlos
         en la UI en sus campos correspondientes
         */
        when(mode) {
            Mode.AddNote -> {
                title = "Nueva nota"
                binding.sliderPriority.value = 1.0F
                val txtPrioridad= "Prioridad: "+1
                binding.tvPriority.text = getString(R.string.textInView, txtPrioridad)
            }
            Mode.EditNote -> {
                title = (intent.getStringExtra(EXTRA_TITLE))
                binding.etDesc.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
                val txtPrioridad= "Prioridad: "+intent.getIntExtra(EXTRA_PRIORITY, -1)
                binding.tvPriority.text = getString(R.string.textInView, txtPrioridad)
                binding.sliderPriority.value = intent.getIntExtra(EXTRA_PRIORITY, -1).toFloat()
            }
        }

        //Definimos un listener para el slider de prioridad y obtenemos su valor para mostrarlo
        binding.sliderPriority.addOnChangeListener{_, value, _ ->
            val txtPrioridad= "Prioridad: "+value.toInt()
            binding.tvPriority.text = getString(R.string.textInView, txtPrioridad)
        }
    }

    /**
     Inflar el menu de nuestro activity
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    /**
      Obtener el item seleccionado del menu y asignar una accion
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        //Obtener los valores de los elementos de UI
        //Obtener las primeras 3 palabras de la descripcion para armar el titulo
        val title = obtenerTitulo(binding.etDesc.text.toString(), 3)

        val desc = binding.etDesc.text.toString()
        val priority = binding.sliderPriority.value.toInt()

        //Validar el titulo (Unico valor que puede quedar nulo si no hay una descripcion)
        if(title.isEmpty()) {
            Snackbar.make(binding.root, "Por favor ingrese una nota", Snackbar.LENGTH_SHORT).show()
            return
        }

        val data = Intent()
        /**
          Solo si el ID fue proporcionado tambien lo incluimos en los extras con el EXTRA_ID
          de lo contrario no retornamos un id y se creará una nueva nota con un nuevo ID
         */
        if(noteId != -1)
            data.putExtra(EXTRA_ID, noteId)

        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DESCRIPTION, desc)
        data.putExtra(EXTRA_PRIORITY, priority)

        /**
         * Al finalizar (Activity.RESULT_OK) indicamos que la actividad se completo de forma correcta
         * y adjuntamos el intent llamado data como resultado, lo que le permite a la activity
         * que llamó a esta activitdad reciba estos datos cuando ESTA actividad finalice
         */
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun obtenerTitulo(cadena: String, n: Int): String {
        val palabras = cadena.trim().split("\\s+".toRegex()) // Divide la cadena en palabras
        val primerasNPalabras = if (palabras.size <= n) {
            // Si la cadena tiene menos de N palabras, obtén todas las palabras
            palabras
        } else {
            // Si la cadena tiene N o más palabras, selecciona las primeras N palabras
            palabras.take(n)
        }
        return capitalizarPrimeraLetra(primerasNPalabras.joinToString(" "))
    }
    private fun capitalizarPrimeraLetra(cadena: String): String {
        return if (cadena.isNotEmpty()) {
            cadena[0].toUpperCase() + cadena.substring(1)
        } else {
            cadena
        }
    }

    /**
     Creamos la clase sellada para definir los dos unicos posibles estados de nuestro actovity
     AddNote y EditNote
     */
    private sealed class Mode {
        data object AddNote : Mode()
        data object EditNote : Mode()

    }
}