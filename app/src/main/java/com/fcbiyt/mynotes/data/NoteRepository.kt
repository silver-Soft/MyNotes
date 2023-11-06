package com.fcbiyt.mynotes.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.fcbiyt.mynotes.core.subscribeOnBackground
import com.fcbiyt.mynotes.data.database.NoteDatabase
import com.fcbiyt.mynotes.data.database.dao.NoteDao
import com.fcbiyt.mynotes.data.database.entities.NoteEntity
import com.fcbiyt.mynotes.data.dto.NewNote
import com.fcbiyt.mynotes.data.model.NoteModel
import com.fcbiyt.mynotes.data.network.NoteService
import org.json.JSONObject

class NoteRepository(application: Application) {

    private var noteDao: NoteDao

    private val database = NoteDatabase.getInstance(application)

    /**
     * Crear instancia del NoteService para obtener las repuestas de api
     */
    private val api = NoteService()

    init {
        noteDao = database.noteDao()
    }
    fun insert(note: NoteEntity) {
        subscribeOnBackground {
            noteDao.insert(note)
        }
    }
    fun insertGetId(note: NoteEntity): Long {
        return noteDao.insert(note)
    }
    fun update(note: NoteEntity) {
        subscribeOnBackground {
            noteDao.update(note)
        }
    }
    fun delete(note: NoteEntity) {
        subscribeOnBackground {
            noteDao.delete(note)
        }
    }
    fun deleteAllNotes() {
        subscribeOnBackground {
            noteDao.deleteAllNotes()
        }
    }
    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    /**
     * A continuacion se definen los metodos que realizarán las consultas al API
     * Estas funciones como se ejecutan en corrutinas deben ser funciones suspendidas
     * o asincronas
     */
    suspend fun getAllNotesFromApi(): LiveData<List<NoteModel>> {
        return api.getNotes()
    }
    suspend fun saveNoteOnApi(body: NewNote, url: String): JSONObject? {
        return api.createNote(body,url)
    }
    suspend fun updateNoteOnApi(body: NewNote, url: String): JSONObject? {
        return api.updateNote(body,url)
    }
    suspend fun deleteNoteOnApi(url: String): JSONObject? {
        return api.deleteNote(url)
    }
    suspend fun deleteAllNotesOnApi(url: String): JSONObject? {
        return api.deleteAllNotes(url)
    }
}