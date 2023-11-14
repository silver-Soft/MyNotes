package com.fcbiyt.mynotes.core

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Single (es un Observable que devuelve un unico valor o un error)
 */
fun subscribeOnBackground(function: () -> Unit) {
    // Utiliza la clase Single de RxJava para ejecutar una tarea en un hilo en segundo plano.
    Single.fromCallable {
        // Llama a la función proporcionada como argumento.
        // Esta es la tarea que se ejecutará en segundo plano.
        function()
    }
        // Se suscribe al Single en el hilo de operaciones de entrada/salida (I/O) usando RxJava.
        .subscribeOn(Schedulers.io())
        // Observa los resultados en el hilo principal de la interfaz de usuario en Android.
        .observeOn(AndroidSchedulers.mainThread())
        // Se suscribe al Single. La suscripción es necesaria para iniciar la ejecución de la tarea.
        .subscribe()
}