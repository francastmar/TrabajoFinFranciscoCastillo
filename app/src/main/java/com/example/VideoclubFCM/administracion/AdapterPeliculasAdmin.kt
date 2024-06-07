package com.example.VideoclubFCM.administracion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.modelo.Pelicula

class AdapterPeliculasAdmin : RecyclerView.Adapter<AdapterPeliculasAdmin.MiViewHolder>() {

    private var listener: View.OnClickListener? = null
    private var lista: ArrayList<Pelicula> = ArrayList()

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
        val pelicula = lista[position]
        viewHolder.texto.text = pelicula.productos.titulo
    }

    override fun getItemCount() = lista.size

    fun setOnItemClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun anyadirALista(lista_: ArrayList<Pelicula>) {
        lista.clear()
        lista.addAll(lista_)
        notifyDataSetChanged()
    }
    fun anyadirALista(pelicula: Pelicula){
        lista.add(pelicula)

        notifyDataSetChanged() // Actualizamos el recyclerView
    }
}