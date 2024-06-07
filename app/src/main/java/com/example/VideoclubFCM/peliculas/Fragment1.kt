package com.example.VideoclubFCM.peliculas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.HintAdapter
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Director
import com.example.VideoclubFCM.modelo.Genero
import com.example.VideoclubFCM.modelo.Pelicula
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Calendar

class Fragment1 : Fragment() {

    private var retrofit: Retrofit? = null
    private var peliAdapter: PeliculasAdapter? = null
    private var listaPelis: ArrayList<Pelicula>? = null
    private var listaDirectores: ArrayList<Director>? = null
    private var listaGeneros: ArrayList<Genero>? = null

    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner

    //lista de años para el spinner filtro de años
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val listAnyos = listOf("Año") + (1994..currentYear).map { it.toString() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_fragment, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrofit = Cliente.obtenerCliente()

        spinner1 = view.findViewById(R.id.spinner1)
        spinner2 = view.findViewById(R.id.spinner2)
        spinner3 = view.findViewById(R.id.spinner3)

        obtenerDatos()
        obtenerDirectores() //se llena la lista de directores y se crea otra solo con sus nombres
        obtenerGeneros() //se llena el spinner de generos

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        peliAdapter = PeliculasAdapter()
        recyclerView.adapter = peliAdapter

        //SPINNER PARA ANYO
        val stringArrayAdapter = HintAdapter(requireContext(), android.R.layout.simple_spinner_item, listAnyos);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.adapter = stringArrayAdapter

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccionado = listAnyos[position]
              if(!seleccionado.equals("Año")){
                    obtenerPeliculasPorAnio(seleccionado.toInt())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        peliAdapter?.setOnItemClickListener { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val peliculaSeleccionada = listaPelis?.get(position)
            if (peliculaSeleccionada != null) {
                val intent = Intent(requireContext(), PeliculaAlquilar::class.java)
                intent.putExtra("pelicula", peliculaSeleccionada) // Aquí pasamos el objeto Pelicula como extra
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
    private fun getDirectorNames(directors: List<Director>?): List<String> {
        return directors?.map { it.nombre } ?: emptyList()
    }
    private fun getGenderNames(genders: List<Genero>?): List<String> {
        return genders?.map { it.nombre } ?: emptyList()
    }
    //funcion para llenar el recycler con datos
    private fun obtenerDatos() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerPeliculas()?.enqueue(object : Callback<ArrayList<Pelicula>> {
            override fun onResponse(call: Call<ArrayList<Pelicula>>, response: Response<ArrayList<Pelicula>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPelis = lista
                        peliAdapter?.anyadirALista(listaPelis!!)
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Pelicula>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    // llamada pasando el director seleccionado en el spinner para obtener solo las peliculas con el mismo
    private fun obtenerPeliculasPorDirector(director: String) {
        val api = retrofit!!.create(Api::class.java)
        api.obtenerPeliculasPorDirector(director).enqueue(object : Callback<ArrayList<Pelicula>> {
            override fun onResponse(call: Call<ArrayList<Pelicula>>, response: Response<ArrayList<Pelicula>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPelis = lista
                        peliAdapter?.anyadirALista(listaPelis!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al obtener películas del director", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Pelicula>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red al obtener películas del director", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // llamada pasando el año seleccionado en el spinner para obtener solo las peliculas con el mismo
    private fun obtenerPeliculasPorAnio(anio: Int) {
        val api = retrofit!!.create(Api::class.java)
        api.obtenerPeliculasPorAnio(anio.toString()).enqueue(object : Callback<ArrayList<Pelicula>> {
            override fun onResponse(call: Call<ArrayList<Pelicula>>, response: Response<ArrayList<Pelicula>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPelis = lista
                        peliAdapter?.anyadirALista(listaPelis!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al obtener películas por año", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Pelicula>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red al obtener películas por eño", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // llamada pasando el genero seleccionado en el spinner para obtener solo las peliculas con el mismo
    private fun obtenerPeliculasPorGenero(genero: String) {
        val api = retrofit!!.create(Api::class.java)

        api.obtenerPeliculasPorGenero(genero).enqueue(object : Callback<ArrayList<Pelicula>> {
            override fun onResponse(call: Call<ArrayList<Pelicula>>, response: Response<ArrayList<Pelicula>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPelis = lista
                        peliAdapter?.anyadirALista(listaPelis!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al obtener películas del genero", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Pelicula>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red al obtener películas del genero", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //crear spinner con la lista de todos los directores
    private fun obtenerDirectores() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerDirectores()?.enqueue(object : Callback<ArrayList<Director>> {
            override fun onResponse(call: Call<ArrayList<Director>>, response: Response<ArrayList<Director>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaDirectores = lista
                        val listNameDirector = listOf("Director") + getDirectorNames(listaDirectores)
                        //SPINNER PARA DIRECTOR, se hace denrto del metodo que recupera los datos, debido a la asincronía entre la llamada al
                        // servidor y la ejecucion del codigo.
                        val stringArrayAdapter3 = HintAdapter(requireContext(), android.R.layout.simple_spinner_item, listNameDirector);
                        stringArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner3.adapter = stringArrayAdapter3

                        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listNameDirector[position]
                                if(!seleccionado.equals("Director")){
                                    obtenerPeliculasPorDirector(seleccionado)
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
            override fun onFailure(call: Call<ArrayList<Director>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    //crear spinner con la lista de todos los generos que hay
    private fun obtenerGeneros() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerGenerosPelicula()?.enqueue(object : Callback<ArrayList<Genero>> {
            override fun onResponse(call: Call<ArrayList<Genero>>, response: Response<ArrayList<Genero>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaGeneros = lista
                        val listNameGeneros = listOf("Genero") + getGenderNames(listaGeneros)
                        //SPINNER PARA GENEROS
                        val stringArrayAdapter2 = HintAdapter(requireContext(), android.R.layout.simple_spinner_item, listNameGeneros);
                        stringArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.adapter = stringArrayAdapter2

                        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listNameGeneros[position]
                                if(!seleccionado.equals("Genero")){
                                    obtenerPeliculasPorGenero(seleccionado)
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
            override fun onFailure(call: Call<ArrayList<Genero>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

