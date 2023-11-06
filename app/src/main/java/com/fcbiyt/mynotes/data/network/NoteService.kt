package com.fcbiyt.mynotes.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fcbiyt.mynotes.core.RetrofitHelper
import com.fcbiyt.mynotes.data.dto.NewNote
import com.fcbiyt.mynotes.data.model.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NoteService {
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getNotes(): LiveData<List<NoteModel>> {
        val resultLiveData = MutableLiveData<List<NoteModel>>() // LiveData mutable que se actualizará con los datos
        // Ejecutamos la llamada en un hilo secundario para no congelar la UI mientras espera respuesta
        withContext(Dispatchers.IO) {
            val response = retrofit.create(NoteApiClient::class.java).getAllNotes()
            val notes = response.body() ?: emptyList() // Lista de notas obtenida de la respuesta

            resultLiveData.postValue(notes) // Actualizamos el LiveData con la lista de notas
        }
        return resultLiveData
    }

    suspend fun createNote(body: NewNote, url: String): JSONObject? {
        // Ejecutamos la llamada en un hilo secundario para no congelar la UI mientras espera respuesta
        var response : JSONObject?
        withContext(Dispatchers.IO) {
            response = retrofit.create(NoteApiClient::class.java).createNote(body, url).body()
        }
        return response
    }

    suspend fun updateNote(body: NewNote, url: String): JSONObject? {
        // Ejecutamos la llamada en un hilo secundario para no congelar la UI mientras espera respuesta
        var response : JSONObject?
        withContext(Dispatchers.IO) {
            response = retrofit.create(NoteApiClient::class.java).updateNote(body, url).body()
        }
        return response
    }
    suspend fun deleteNote(url: String): JSONObject? {
        // Ejecutamos la llamada en un hilo secundario para no congelar la UI mientras espera respuesta
        var response : JSONObject?
        withContext(Dispatchers.IO) {
            response = retrofit.create(NoteApiClient::class.java).deleteNote(url).body()
        }
        return response
    }
    suspend fun deleteAllNotes(url: String): JSONObject? {
        // Ejecutamos la llamada en un hilo secundario para no congelar la UI mientras espera respuesta
        var response : JSONObject?
        withContext(Dispatchers.IO) {
            response = retrofit.create(NoteApiClient::class.java).deleteAllNotes(url).body()
        }
        return response
    }
}