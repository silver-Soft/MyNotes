package com.fcbiyt.mynotes.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fcbiyt.mynotes.R
import com.fcbiyt.mynotes.data.database.entities.NoteEntity

/**
 * La clase toma una función lambda como argumento llamada onItemClickListener,
 * que se utiliza para manejar eventos de clic en elementos individuales de la lista.
 *
 * NoteAdapter hereda de la clase ListAdapter<NoteEntity, NoteHolder>.
 * La ListAdapter es una clase proporcionada por Android que simplifica la implementación de un adaptador para un RecyclerView.
 * La lista contiene elementos de tipo NoteEntity, y el adaptador utiliza una clase interna llamada NoteHolder
 * para mantener las vistas de los elementos en el reciclaje.
 */
class NoteAdapter(private val onItemClickListener: (NoteEntity) -> Unit)
    : ListAdapter<NoteEntity, NoteAdapter.NoteHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        /**
         * Este método se llama cuando el adaptador necesita crear una nueva vista de elemento de la lista.
         * Utiliza un archivo de diseño XML llamado note_item para inflar la vista y la devuelve en un objeto NoteHolder.
         */
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent,
            false)
        return NoteHolder(itemView)
    }
    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        /**
         * Este método se llama cuando se necesita vincular datos a una vista de elemento en una posición específica.
         * Utiliza el método getItem(position) para obtener el objeto NoteEntity correspondiente a la posición y
         * luego actualiza las vistas de NoteHolder con los datos de ese objeto.
         */
        with(getItem(position)) {
            holder.tvTitle.text = title
            holder.tvDescription.text = description
            holder.tvPriority.text = priority.toString()
        }
    }

    fun getNoteAt(position: Int) = getItem(position)//Obtiene un objeto en una determinada posicion


    inner class NoteHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        /**
         * Esta es una clase interna que extiende RecyclerView.ViewHolder. Se utiliza para mantener
         * las vistas de un elemento individual en el reciclaje.
         * Las vistas, como tvTitle, tvDescription, y tvPriority, se inicializan en el constructor de NoteHolder.
         * Se configura un evento de clic en itemView (el elemento raíz de la vista de elemento)
         * para llamar a la función onItemClickListener cuando se hace clic en un elemento.
         */
        val tvTitle: TextView = itemView.findViewById(R.id.text_view_title)
        val tvDescription: TextView = itemView.findViewById(R.id.text_view_description)
        val tvPriority: TextView = itemView.findViewById(R.id.text_view_priority)

        init {
            itemView.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION)
                    onItemClickListener(getItem(adapterPosition))
            }
        }

    }
}

private val diffCallback = object : DiffUtil.ItemCallback<NoteEntity>() {
    /**
     * Esta es una implementación personalizada de DiffUtil.ItemCallback utilizada para comparar elementos en la lista.
     * areItemsTheSame compara si los elementos tienen el mismo ID.
     * areContentsTheSame compara si los elementos tienen el mismo título, descripción y prioridad.
     */
    override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity) =
        oldItem.title == newItem.title
                && oldItem.description == newItem.description
                && oldItem.priority == newItem.priority
}