package com.fcbiyt.mynotes.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fcbiyt.mynotes.data.database.entities.NoteEntity

/**
 * Esta es una interfaz que define métodos de acceso a la base de datos
 *
 * @Insert(onConflict = OnConflictStrategy.REPLACE) Indicamos que si hay algun problema al realizar
 * una inserción de datos, o si el id que intentamos insertar ya existe, lo reemplace, en este metodo
 * podemos hacer muchas otras cosas en caso de que una situacion de conflicto ocurra.
 */
@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NoteEntity): Long

    @Update
    fun update(note: NoteEntity)

    @Delete
    fun delete(note: NoteEntity)

    @Query("delete from note_table")
    fun deleteAllNotes()

    @Query("select * from note_table order by priority asc")//Podemos crear otra consulta personalizada
    fun getAllNotes(): LiveData<List<NoteEntity>>
}