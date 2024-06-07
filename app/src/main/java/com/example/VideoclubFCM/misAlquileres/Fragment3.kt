package com.example.VideoclubFCM.misAlquileres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.MainActivity
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Alquiler
import com.example.VideoclubFCM.modelo.Videojuego
import com.example.VideoclubFCM.peliculas.PeliculasAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class Fragment3 : Fragment() {

    private var retrofit: Retrofit? = null
    private var alquileresAdapter: AlquileresAdapter? = null
    private var listaAlquileres: ArrayList<Alquiler>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_fragment_alquileres, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Retrofit
        retrofit = Cliente.obtenerCliente()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1 )

        alquileresAdapter = AlquileresAdapter()
        recyclerView.adapter = alquileresAdapter
        val persona = MainActivity.getPersona()

        obtenerAlquileresCliente(persona!!.personas.username)
    }

    private fun obtenerAlquileresCliente(nombreCliente: String) {
        val api = retrofit!!.create(Api::class.java)

        api.obtenerAlquileresPorCliente(nombreCliente).enqueue(object : Callback<ArrayList<Alquiler>> {
            override fun onResponse(call: Call<ArrayList<Alquiler>>, response: Response<ArrayList<Alquiler>>) {
                if (response.isSuccessful) {
                    // Obtener lista de alquileres del cuerpo de la respuesta
                    val lista = response.body()
                    // Actualizar el adaptador con la lista de alquileres
                    if(lista != null){
                        listaAlquileres = lista
                        alquileresAdapter!!.anyadirALista(listaAlquileres!!)
                    }

                } else {
                    Toast.makeText(requireContext(), "Error al obtener alquileres del cliente", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Alquiler>>, t: Throwable) {
                // Manejar fallo de la llamada
                Toast.makeText(requireContext(), "Error de red al obtener alquileres del cliente", Toast.LENGTH_SHORT).show()
            }
        })
    }
}