package com.fcbiyt.mynotes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fcbiyt.mynotes.data.database.dao.NoteDao
import com.fcbiyt.mynotes.data.database.entities.NoteEntity

/**
 * Definimos nuestra base de datos con la anotacion @Database y el array
 * de entidades que contiene, asi como la versión
 *
 * La version de nuestra base de datos nos ayudará en un futuro a llevar un
 * mejor control en migraciones futuras
 *
 * Nuestro companion object declara un objeto compañero que contiene métodos
 * para obtener una instancia de NoteDatabase.
 * Este patrón se utiliza para garantizar que solo haya una instancia de la
 * base de datos en la aplicación (patrón Singleton).
 */
@Database(entities = [NoteEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    //declara un método que proporciona acceso a un objeto NoteDao que será
    // accesable desde el repositorio.
    // Recordar que NoteDao es una interfaz que define operaciones específicas
    // de acceso a la base de datos
    abstract fun noteDao(): NoteDao
    companion object {
        private var instance: NoteDatabase? = null //Obtenemos una instancia de NoteDatabase

        @Synchronized //@Synchronized se utiliza para garantizar que esta operación sea segura en entornos con múltiples hilos.
        //Obtenemos una instancia de NoteDatabase. Si ya existe una instancia,
        // la devuelve si no crea una nueva
        fun getInstance(ctx: Context): NoteDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, NoteDatabase::class.java,
                    "note_database")
                    //La función .fallbackToDestructiveMigration() se utiliza para
                    // gestionar migraciones de la base de datos en caso de cambios en
                    // el esquema de la base de datos.
                    .fallbackToDestructiveMigration()
                    // Construimos la instancia de nuestra BD y la retornamos
                    .build()
            return instance!!
        }
    }
}