package com.fcbiyt.mynotes.core

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Creamos una funcion observable que retorne un valor o un error al ser llamada
 * mediante un hilo secundario o hilo de fondo
 *
 * El método subscribeOn se encarga de especificar en qué hilo se ejecutará la tarea.
 * En este caso, Schedulers.io() indica que la tarea se ejecutará en un hilo de fondo,
 * adecuado para operaciones de E/S y otras tareas costosas
 *
 * El método observeOn especifica en qué hilo se manejarán los resultados o eventos del Single.
 * AndroidSchedulers.mainThread() indica que los resultados se manejarán en el hilo principal de la
 * interfaz de usuario de Android, lo que es esencial para actualizar la interfaz de usuario
 * después de completar la tarea en segundo plano.
 *
 * Finalmente nos suscribimos a la tarea definida en nuestra "function" y cuando termine el o los
 * resultados seran manejados en el mainThread.
 */
fun subscribeOnBackground(function: () -> Unit) {
    Single.fromCallable {
        function()
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
}