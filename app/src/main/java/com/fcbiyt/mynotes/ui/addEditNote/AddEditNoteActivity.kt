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

    private lateinit var mode : Mode

    private var noteId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        noteId = intent.getIntExtra(EXTRA_ID, -1)
        mode = if(noteId == -1) Mode.AddNote
        else Mode.EditNote

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

        binding.sliderPriority.addOnChangeListener{_, value, _ ->
            val txtPrioridad= "Prioridad: "+value.toInt()
            binding.tvPriority.text = getString(R.string.textInView, txtPrioridad)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

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
        if(noteId != -1)
            data.putExtra(EXTRA_ID, noteId)

        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DESCRIPTION, desc)
        data.putExtra(EXTRA_PRIORITY, priority)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun obtenerTitulo(cadena: String, n: Int): String {
        val palabras = cadena.trim().split("\\s+".toRegex())
        val primerasNPalabras = if (palabras.size <= n) {
            palabras
        } else {
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
    private sealed class Mode {
        data object AddNote : Mode()
        data object EditNote : Mode()

    }
}