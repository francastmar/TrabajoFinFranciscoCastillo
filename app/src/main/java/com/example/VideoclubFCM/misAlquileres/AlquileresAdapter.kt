package com.example.VideoclubFCM.misAlquileres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.modelo.Alquiler
import com.example.VideoclubFCM.modelo.Pelicula

class AlquileresAdapter : RecyclerView.Adapter<AlquileresAdapter.MiViewHolder>() {

    private var listener: View.OnClickListener? = null
    private var lista: ArrayList<Alquiler> = ArrayList()

    inner class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val texto: TextView = view.findViewById(R.id.txtTituloProducto)
        val fecha: TextView = view.findViewById(R.id.txtFechaDevolucion)
        init {
            view.setOnClickListener {
                listener?.onClick(view)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.elementos_lista_alquileres, viewGroup, false)

        return MiViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MiViewHolder, position: Int) {
        val alquiler = lista[position]
        viewHolder.texto.text = alquiler.producto!!.titulo
        viewHolder.fecha.text = alquiler.fecha_devolucion.toString()
    }

    override fun getItemCount() = lista.size

    fun setOnItemClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun anyadirALista(lista_: ArrayList<Alquiler>) {
        lista.clear()
        lista.addAll(lista_)
        notifyDataSetChanged()
    }
}