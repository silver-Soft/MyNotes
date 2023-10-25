package com.fcbiyt.mynotes.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Creamos nuestra entidad de datos para interactuar con la DB local
 * cada propiedad de la clase corresponde a una columna en la tabla
 *
 * @ColumnInfo(name="NAME") se utiliza esta anotacion para especificar cual es el nombre de la columna
 * al que hará referencia la propiedad definida
 *
 * @PrimaryKey(autoGenerate = true) Indicamos que la siguiente propiedad sera la llave primaria, y que
 * ademas sera autogenerada, ademas de indicar que la llave primaria iniciará en 0
 */
@Entity(tableName = "note_table")//Nombramos la tabla
data class NoteEntity(//Definimos las propiedades o columnas de la tabla
    @ColumnInfo(name="title") var title: String,
    @ColumnInfo(name="description") var description: String,
    @ColumnInfo(name="priority") var priority: Int,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id") val id: Int = 0
)
