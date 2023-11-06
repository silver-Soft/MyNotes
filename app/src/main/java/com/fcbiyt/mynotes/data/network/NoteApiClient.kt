package com.fcbiyt.mynotes.data.network

import com.fcbiyt.mynotes.data.dto.NewNote
import com.fcbiyt.mynotes.data.model.NoteModel
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface NoteApiClient {
    @GET("notes")
    suspend fun getAllNotes(): Response<List<NoteModel>>
    @POST()                                     //notes/1 <-- ID personalizado
    suspend fun createNote(@Body body: NewNote, @Url url:String): Response<JSONObject>
    @PUT()                                      //notes/1
    suspend fun updateNote(@Body body: NewNote, @Url url:String): Response<JSONObject>
    @DELETE()                //notes/1
    suspend fun deleteNote(@Url url:String): Response<JSONObject>
    @DELETE()                //notes
    suspend fun deleteAllNotes(@Url url:String): Response<JSONObject>

}