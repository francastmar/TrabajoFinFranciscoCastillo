package com.example.VideoclubFCM.administracion

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.modelo.Clientes
import com.example.VideoclubFCM.modelo.Pelicula
import com.example.VideoclubFCM.modelo.Videojuego

class AdapterClientesAdmin : RecyclerView.Adapter<AdapterClientesAdmin.MiViewHolder>() {

    private var listener: View.OnClickListener? = null
    private var lista: ArrayList<Clientes> = ArrayList()

    inner class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val texto: TextView = view.findViewById(R.id.txtTituloProducto)

        init {
            view.setOnClickListener {
                listener?.onClick(view)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.elementos_lista_admin, viewGroup, false)

        return MiViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MiViewHolder, position: Int) {
        val cliente = lista[position]
        viewHolder.texto.text = cliente.personas.username
    }

    override fun getItemCount() = lista.size

    fun setOnItemClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun anyadirALista(lista_: ArrayList<Clientes>) {
        lista.clear()
        lista.addAll(lista_)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun anyadirALista(cliente: Clientes){
        lista.add(cliente)
        notifyDataSetChanged() // Actualizamos el recyclerView
    }
}