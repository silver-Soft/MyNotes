package com.fcbiyt.mynotes.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.fcbiyt.mynotes.data.NoteRepository
import com.fcbiyt.mynotes.data.database.entities.NoteEntity

/**
 * Esta clase interactúa con la clase NoteRepository con las operacionea DAO
 *
 *  Esta clase extiende AndroidViewModel y toma una instancia de Application
 *  como argumento en su constructor.
 *  AndroidViewModel es una clase base que se utiliza para gestionar y exponer
 *  datos a las vistas en Android.
 *
 *  La aplicación (representada por app) se utiliza para proporcionar un
 *  contexto a esta clase, presente en toda la aplicación.
 *
 * Recordar que el contexto es un objeto, el cual proporciona informacion sobre el entorno
 */
class NoteViewModel(app:Application) :AndroidViewModel(app) {
    //Se crea una instancia de NoteRepository para acceder a las funciones
    //definidas en NoteRepository
    private val repository = NoteRepository(app)

    //creamos una variable que obtenga todas las notas repository.getAllNotes().
    // de tipo LiveData<List<NoteEntity>>, lo que permite
    // a las vistas observar los cambios en la lista de notas de forma reactiva.
    private val allNotes = repository.getAllNotes()

    /**
     * A continuación, se definen los métodos en la clase NoteViewModel.
     * Estos métodos interactúan con el repositorio para realizar operaciones en la base de datos:
     */
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
}