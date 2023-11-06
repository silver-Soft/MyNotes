package com.fcbiyt.mynotes.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.fcbiyt.mynotes.data.NoteRepository
import com.fcbiyt.mynotes.data.database.entities.NoteEntity
import com.fcbiyt.mynotes.data.dto.NewNote
import com.fcbiyt.mynotes.data.model.NoteModel
import org.json.JSONObject

/**
 * Esta clase interactúa con la clase NoteRepository, que a su vez se encarga de interactuar con la base de datos local.
 *
 *  Esta clase extiende AndroidViewModel y toma una instancia de Application como argumento en su constructor.
 *  AndroidViewModel es una clase base que se utiliza para gestionar y exponer datos a las vistas en Android.
 *  La aplicación (representada por app) se utiliza para proporcionar un contexto a esta clase.
 *
 */
class NoteViewModel(app:Application) :AndroidViewModel(app) {
    private val repository = NoteRepository(app)

    private val allNotes = repository.getAllNotes()
    fun insert(note: NoteEntity) {
        repository.insert(note)
    }
    fun insertAndGetId(note: NoteEntity): Long {
        return repository.insertGetId(note)
    }

    fun update(note: NoteEntity) {
        repository.update(note)
    }

    fun delete(note: NoteEntity) {
        repository.delete(note)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return allNotes
    }

    /**
     * A continuación, los métodos para obtener, insertar, modificar y eliminar de la BD remota por
     * medio de un API
     */

    suspend fun getAllNotesOnCloud(): LiveData<List<NoteModel>> {
        return repository.getAllNotesFromApi()
    }

    suspend fun saveNoteOnCloud(body: NewNote, url: String): JSONObject? {
        return repository.saveNoteOnApi(body,url)
    }
    suspend fun updateNoteOnCloud(body: NewNote, url: String): JSONObject? {
        return repository.updateNoteOnApi(body,url)
    }

    suspend fun deleteNoteOnCloud(url: String): JSONObject? {
        return repository.deleteNoteOnApi(url)
    }
    suspend fun deleteAllNotesOnCloud(url: String): JSONObject? {
        return repository.deleteAllNotesOnApi(url)
    }
}