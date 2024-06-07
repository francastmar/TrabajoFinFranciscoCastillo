package com.example.VideoclubFCM.videojuegos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.modelo.Videojuego

class JuegosAdapter : RecyclerView.Adapter<JuegosAdapter.MiViewHolder>() {

    private var listener: View.OnClickListener? = null
    private var lista: ArrayList<Videojuego> = ArrayList()

    inner class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.portada)
        val texto: TextView = view.findViewById(R.id.txtTitulo)

        init {
            view.setOnClickListener {
                listener?.onClick(view)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.elementos_lista_productos, viewGroup, false)

        return MiViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MiViewHolder, position: Int) {
        val juego = lista[position]
        Glide.with(viewHolder.itemView.context).load(lista[position].productos.portada).into(viewHolder.imagen)
        viewHolder.texto.text = juego.productos.titulo
    }

    override fun getItemCount() = lista.size

    fun setOnItemClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun anyadirALista(lista_: ArrayList<Videojuego>) {
        lista.clear()
        lista.addAll(lista_)
        notifyDataSetChanged()
    }
}