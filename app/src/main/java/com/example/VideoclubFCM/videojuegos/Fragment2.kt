package com.example.VideoclubFCM.videojuegos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.HintAdapter
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Desarrolladora
import com.example.VideoclubFCM.modelo.Plataforma
import com.example.VideoclubFCM.modelo.Videojuego
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class Fragment2 : Fragment() {

    private var retrofit: Retrofit? = null
    private var juegoAdapter: JuegosAdapter? = null
    private var listaJuegos: ArrayList<Videojuego>? = null

    private var listaPlataformas: ArrayList<Plataforma>? = null
    private var listaDesarrolladoras: ArrayList<Desarrolladora>? = null

    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_fragment, container, false)
        // Inflate the layout for this fragment
        val texto = view.findViewById<TextView>(R.id.txtSeccion)
        texto.text = "VIDEOJUEGOS"


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrofit = Cliente.obtenerCliente()

        spinner1 = view.findViewById(R.id.spinner1)
        spinner2 = view.findViewById(R.id.spinner2)
        spinner3 = view.findViewById(R.id.spinner3)

        //crear Spinner en las funciones que obtienen los datos para evitar la asincronia entre la llamada al server
        // y la ejecucion de la creacion de los spinner
        obtenerPlataformas()
        obtenerDesarrolladoras()
        crearSpinnerMulti()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        juegoAdapter = JuegosAdapter()

        juegoAdapter?.setOnItemClickListener { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val juegoSeleccionado = listaJuegos?.get(position)
            if (juegoSeleccionado != null) {
                val intent = Intent(requireContext(), JuegoAlquilar::class.java)
                intent.putExtra("videojuego", juegoSeleccionado)
                startActivity(intent)
            }
        }

        //boton para resetear los filtros para que aparezcan todos los productos
        val btnResetFiltros: Button = view.findViewById(R.id.btnResetFiltros)

        btnResetFiltros.setOnClickListener{
            obtenerDatos()
            //hacer que la seleccion vuelva a 0
        }
    }
    private fun getPlataformaName(plataformas: List<Plataforma>?): List<String> {
        return plataformas?.map { it.nombre } ?: emptyList()
    }
    private fun getDesarrolladoraName(desarrolladora: List<Desarrolladora>?): List<String> {
        return desarrolladora?.map { it.nombre } ?: emptyList()
    }
    private fun obtenerDatos() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerJuegos()?.enqueue(object : Callback<ArrayList<Videojuego>> {
            override fun onResponse(call: Call<ArrayList<Videojuego>>, response: Response<ArrayList<Videojuego>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaJuegos = lista
                        // Update adapter data
                        juegoAdapter?.anyadirALista(listaJuegos!!)
                        // Set adapter to RecyclerView
                        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerview)
                        recyclerView?.adapter = juegoAdapter
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Videojuego>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun obtenerJuegosPlataforma(plataforma: String) {
        val api = retrofit!!.create(Api::class.java)

        api.obtenerJuegosPlataforma(plataforma).enqueue(object : Callback<ArrayList<Videojuego>> {
            override fun onResponse(call: Call<ArrayList<Videojuego>>, response: Response<ArrayList<Videojuego>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaJuegos = lista
                        juegoAdapter?.anyadirALista(listaJuegos!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al obtener películas del genero", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Videojuego>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red al obtener películas del genero", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun obtenerJuegosDesarrolladora(desarrolladora: String) {
        val api = retrofit!!.create(Api::class.java)

        api.obtenerJuegosDesarrolladora(desarrolladora).enqueue(object : Callback<ArrayList<Videojuego>> {
            override fun onResponse(call: Call<ArrayList<Videojuego>>, response: Response<ArrayList<Videojuego>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaJuegos = lista
                        juegoAdapter?.anyadirALista(listaJuegos!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al obtener películas del genero", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Videojuego>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red al obtener películas del genero", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun obtenerJuegosMultijugador() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerJuegosMltijugador()?.enqueue(object : Callback<ArrayList<Videojuego>> {
            override fun onResponse(call: Call<ArrayList<Videojuego>>, response: Response<ArrayList<Videojuego>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaJuegos = lista
                        // Update adapter data
                        juegoAdapter?.anyadirALista(listaJuegos!!)
                        // Set adapter to RecyclerView
                        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerview)
                        recyclerView?.adapter = juegoAdapter

                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Videojuego>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun crearSpinnerMulti() {
        val listNameGeneros = listOf("Multi", "Si")
        // SPINNER PARA MULTI
        val stringArrayAdapter1 = HintAdapter(requireContext(), android.R.layout.simple_spinner_item, listNameGeneros)
        stringArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = stringArrayAdapter1

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccionado = listNameGeneros[position]
                if (seleccionado != "Multi" && seleccionado == "Si") {
                    obtenerJuegosMultijugador()
                } else {
                    obtenerDatos()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun obtenerPlataformas() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerPlataformas()?.enqueue(object : Callback<ArrayList<Plataforma>> {
            override fun onResponse(call: Call<ArrayList<Plataforma>>, response: Response<ArrayList<Plataforma>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPlataformas = lista
                        val listNamePlataforma = listOf("Plataforma") + getPlataformaName(listaPlataformas)
                        //SPINNER PARA Plataformas, se hace dentro del metodo que recupera los datos, debido a la asincronía entre la llamada al
                        // servidor y la ejecucion del codigo.
                        val stringArrayAdapter3 = HintAdapter(requireContext(), android.R.layout.simple_spinner_item, listNamePlataforma);
                        stringArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner3.adapter = stringArrayAdapter3

                        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listNamePlataforma[position]
                                if(!seleccionado.equals("Plataforma")){
                                    obtenerJuegosPlataforma(seleccionado)
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Plataforma>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun obtenerDesarrolladoras() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerDesarroladoras()?.enqueue(object : Callback<ArrayList<Desarrolladora>> {
            override fun onResponse(call: Call<ArrayList<Desarrolladora>>, response: Response<ArrayList<Desarrolladora>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaDesarrolladoras = lista
                        val listNameDesarrolladora = listOf("Desarrolladora") + getDesarrolladoraName(listaDesarrolladoras)
                        //SPINNER PARA DESARROLLADORA, se hace dentro del metodo que recupera los datos, debido a la asincronía entre la llamada al
                        // servidor y la ejecucion del codigo.
                        val stringArrayAdapter2 = HintAdapter(requireContext(), android.R.layout.simple_spinner_item, listNameDesarrolladora);
                        stringArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.adapter = stringArrayAdapter2

                        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listNameDesarrolladora[position]
                                if(!seleccionado.equals("Desarrolladora")){
                                    obtenerJuegosDesarrolladora(seleccionado)
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Desarrolladora>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}