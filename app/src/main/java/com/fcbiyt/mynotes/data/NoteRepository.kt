package com.fcbiyt.mynotes.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.fcbiyt.mynotes.core.subscribeOnBackground
import com.fcbiyt.mynotes.data.database.NoteDatabase
import com.fcbiyt.mynotes.data.database.dao.NoteDao
import com.fcbiyt.mynotes.data.database.entities.NoteEntity

/**
 *El repositorio actua como intermediario entre dos de las capas de MVVM, con la capa de datos y con la UI
 */
class NoteRepository(application: Application) {//Se indica que esta clase tomara una instancia de Application
            //Esto quiere decuir que la clase esta diseñada para se utilizada en el contexto de la aplicación

    private var noteDao: NoteDao // Se crea una instancia de NoteDao que se utilizará para realizar operaciones en la base de datos.

    private val database = NoteDatabase.getInstance(application)//Aquí se crea una instancia de NoteDatabase utilizando el método getInstance y
                                                // pasando la aplicación como contexto. Esto establece una conexión con la base de datos local.

    init {
        /**
         * Se inicializa la variable noteDao asignándole una instancia de NoteDao a partir de la instancia de NoteDatabase.
         * Esto prepara noteDao para interactuar con la base de datos.
         */
        noteDao = database.noteDao()
    }

    /**
     * A continuacion se definen los metodos que realizarán las operaciones a la BD por medio de NoteDao
     */
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
    //La siguiente funcion se extiende del tipo LiveData lo que permite a las vistas observar los cambios
    // en la lista de notas de forma reactiva realTime.
    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    /**
     * A continuacion se definen los metodos que realizarán las consultas al API
     */
}