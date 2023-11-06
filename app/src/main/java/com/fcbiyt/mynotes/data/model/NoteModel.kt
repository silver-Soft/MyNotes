package com.fcbiyt.mynotes.data.model

/**
 * Este modelo será utilizado para definirlo como tipo de respuesta en nuestra api
 * la respuesta sera una lista de tipo NoteModel y tomará esta clase como modelo
 */
data class NoteModel(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Int
)
