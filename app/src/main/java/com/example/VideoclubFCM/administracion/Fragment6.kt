package com.example.VideoclubFCM.administracion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Clientes
import com.example.VideoclubFCM.modelo.Pelicula
import com.example.VideoclubFCM.modelo.Persona
import com.example.VideoclubFCM.peliculas.PeliculaAlquilar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class Fragment6 : Fragment() {

    private var retrofit: Retrofit? = null
    private var clienteAdapter: AdapterClientesAdmin? = null
    private var listaClientes: ArrayList<Clientes>? = null

    val resultInsertar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val bundle = data!!.getBundleExtra("Bundle")
            val cliente = bundle?.getParcelable<Clientes>("Cliente")
            val persona = bundle?.getParcelable<Persona>("persona")
            insertarDatos(cliente!!,persona!!)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_fragmentadmin, container, false)
        val texto = view.findViewById<TextView>(R.id.txtSeccion)
        texto.text = "CLIENTES"
        retrofit = Cliente.obtenerCliente()
        obtenerDatos()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        clienteAdapter = AdapterClientesAdmin()

        //al pulsar sobre uno de loss elementos del recycler
        clienteAdapter!!.setOnItemClickListener(View.OnClickListener { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val clienteSeleccionado = listaClientes?.get(position)
            if (clienteSeleccionado != null) {
                val intent = Intent(requireContext(), InsertarCliente::class.java)
                intent.putExtra("cliente", clienteSeleccionado)
                startActivity(intent)
            }
        })

        recyclerView.adapter = clienteAdapter

        // ir a insertar un cliente
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(context, InsertarCliente::class.java)
            resultInsertar.launch(intent)
        }
    }

    // obtener una lista de los clientes para añadirla al recycler
    private fun obtenerDatos() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerClientes()?.enqueue(object : Callback<ArrayList<Clientes>> {
            override fun onResponse(call: Call<ArrayList<Clientes>>, response: Response<ArrayList<Clientes>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaClientes = lista
                        clienteAdapter?.anyadirALista(listaClientes!!)
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Clientes>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    //funcion para insertar al cliente
    private fun insertarDatos(cliente: Clientes, persona: Persona) {
        val api = retrofit?.create(Api::class.java)
        Log.v("clientes", cliente.toString())
        api?.guardarCliente(persona.nombre,persona.apellido1,persona.apellido2
                , persona.direccion,persona.mail,persona.telefono,persona.nacimiento,
            persona.username,persona.password,persona.idRol,cliente.metodo_pago)?.enqueue(object : Callback<Clientes> {
            override fun onResponse(call: Call<Clientes>, response: Response<Clientes>) {
                if (response.isSuccessful) {
                    val clienteInsertado = response.body()
                    if (clienteInsertado != null) {
                        Toast.makeText(context, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show()
                        obtenerDatos()
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta al insertar cliente", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Clientes>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}