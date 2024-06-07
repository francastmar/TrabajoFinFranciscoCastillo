package com.example.VideoclubFCM.administracion

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.VideoclubFCM.HintAdapter
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.databinding.ActivityIinsertarVideojuegoBinding
import com.example.VideoclubFCM.modelo.Desarrolladora
import com.example.VideoclubFCM.modelo.Director
import com.example.VideoclubFCM.modelo.Distribuidora
import com.example.VideoclubFCM.modelo.Genero
import com.example.VideoclubFCM.modelo.GeneroVideojuego
import com.example.VideoclubFCM.modelo.Pais
import com.example.VideoclubFCM.modelo.Plataforma
import com.example.VideoclubFCM.modelo.Productos
import com.example.VideoclubFCM.modelo.Videojuego
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class IInsertarVideojuego : AppCompatActivity() {

    private lateinit var binding: ActivityIinsertarVideojuegoBinding
    private val REQUEST_READ_EXTERNAL_STORAGE = 1
    private var imagen: Uri? = null
    private var retrofit: Retrofit? = null

    //variables para el spiner de Desarrolladora
    private var listaDesarrolladoras: ArrayList<Desarrolladora>? = null
    private lateinit var desarrolladoraSeleccionada :Desarrolladora
    private lateinit var spinnerDesarrolladora: Spinner

    //variables para el spinner de Plataforma
    private var listaPlataformas: ArrayList<Plataforma>? = null
    private lateinit var plataformaSeleccionada : Plataforma
    private lateinit var spinnerPlataforma: Spinner

    //variables para el spinner de Distribuidora
    private var listaDistribuidora: ArrayList<Distribuidora>? = null
    private lateinit var distribuidoraSeleccionada : Distribuidora
    private lateinit var spinnerDistribuidora: Spinner

    //variables para el spinner de Genero
    private var listaGeneros: ArrayList<GeneroVideojuego>? = null
    private lateinit var generoSeleccionado : GeneroVideojuego
    private lateinit var spinnerGenero: Spinner

    // Recoger el Intent enviado por la función abrirGaleria
    val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                imagen = it.data as Uri
                binding.portadaPreview.setImageURI(imagen)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIinsertarVideojuegoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = Cliente.obtenerCliente()

        //inicializar los spinner y cargarlos con datos
        spinnerDesarrolladora = binding.spinnerDesarrolladora
        obtenerDesarrolladoras()

        spinnerDistribuidora=binding.spinnerDistribuidora
        obtenerDistribuidoras()

        spinnerPlataforma=binding.spinnerPlataforma
        obtenerPlataformas()

        spinnerGenero=binding.spinnerGeneroVideojuego
        obtenerGeneros()

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val etTitulo: EditText = findViewById(R.id.et_titulo)
        val cbDoblado: CheckBox = findViewById(R.id.cb_doblado)
        val cbMultijugador: CheckBox = findViewById(R.id.cb_multijugador)

        // si viene con extra entonces el usuario trata de actualizar
        if(intent.hasExtra("juego")){
            val juego: Videojuego? = intent.getParcelableExtra("juego")
            etTitulo.setText(juego!!.productos.titulo)

            if(juego.doblado ==1){
                cbDoblado.isChecked = true
            }
            if(juego.multijugador ==1){
                cbMultijugador.isChecked = true
            }

            btnAdd.visibility = View.INVISIBLE
            btnEdit.visibility = View.VISIBLE
        }else{
            // sino entoncez esta añadiendo
            btnAdd.visibility = View.VISIBLE
            btnEdit.visibility = View.INVISIBLE
        }

        //click para cargar la foto desde galeria
        binding.botonanadirfoto.setOnClickListener {
            solicitarPermisoAlmacenamiento()
        }

        btnAdd.setOnClickListener {

            val titulo = etTitulo.text.toString().trim()
            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val producto = Productos(
                idProducto = 0,
                titulo = binding.etTitulo.text.toString(),
                anio = 2024,
                portada = imagen?.toString() ?: "",
                precio_alquiler = 3.5,
                precio_compra = 20.0
            )

            val videojuego = Videojuego(
                doblado = if (binding.cbDoblado.isChecked) 1 else 0,
                multijugador = if (binding.cbMultijugador.isChecked) 1 else 0,
                plataformas = plataformaSeleccionada,
                distribuidoras = distribuidoraSeleccionada,
                desarrolladoras = desarrolladoraSeleccionada,
                productos = producto,
                generos_videojuegos = generoSeleccionado
            )

            val bundle = Bundle()
            bundle.putParcelable("Videojuego", videojuego)

            val intent = Intent().apply {
                putExtra("Bundle", bundle)
            }
            setResult(RESULT_OK, intent)

            finish()
        }
    }

    // Solicita permiso al usuario para acceder a la Galeria de imágenes.
    private fun solicitarPermisoAlmacenamiento() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE)
        } else {
            abrirGaleria()
        }
    }
    // Abre la Galería de imágenes
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        result.launch(intent)
    }

    private fun getPlataformaName(plataformas: List<Plataforma>?): List<String> {
        return plataformas?.map { it.nombre } ?: emptyList()
    }
    private fun getDesarrolladoraName(desarrolladora: List<Desarrolladora>?): List<String> {
        return desarrolladora?.map { it.nombre } ?: emptyList()
    }
    //obtiene los plataformas y crea un spinner con la lista de los nombres
    // al seleccionarlo se selecciona el objeto completo
    private fun obtenerPlataformas() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerPlataformas()?.enqueue(object : Callback<ArrayList<Plataforma>> {
            override fun onResponse(call: Call<ArrayList<Plataforma>>, response: Response<ArrayList<Plataforma>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPlataformas = lista
                        val listNamePlataforma = getPlataformaName(listaPlataformas)
                        //SPINNER PARA Plataformas, se hace dentro del metodo que recupera los datos, debido a la asincronía entre la llamada al
                        // servidor y la ejecucion del codigo.
                        val stringArrayAdapter3 = ArrayAdapter(applicationContext, R.layout.spinner_item, listNamePlataforma);
                        stringArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPlataforma.adapter = stringArrayAdapter3

                        spinnerPlataforma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listaPlataformas!![position]
                                plataformaSeleccionada = seleccionado
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Plataforma>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    //obtiene los desarrolladoras y crea un spinner con la lista de los nombres
    // al seleccionarlo se selecciona el objeto completo
    private fun obtenerDesarrolladoras() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerDesarroladoras()?.enqueue(object : Callback<ArrayList<Desarrolladora>> {
            override fun onResponse(call: Call<ArrayList<Desarrolladora>>, response: Response<ArrayList<Desarrolladora>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaDesarrolladoras = lista
                        val listNameDesarrolladora = getDesarrolladoraName(listaDesarrolladoras)
                        val stringArrayAdapter2 = ArrayAdapter(applicationContext, R.layout.spinner_item, listNameDesarrolladora);
                        stringArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDesarrolladora.adapter = stringArrayAdapter2

                        spinnerDesarrolladora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listaDesarrolladoras!![position]
                                desarrolladoraSeleccionada=seleccionado
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Desarrolladora>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    //obtiene los distribuidoras y crea un spinner con la lista de los nombres
    // al seleccionarlo se selecciona el objeto completo
    private fun obtenerDistribuidoras() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerDistribuidoras()?.enqueue(object : Callback<ArrayList<Distribuidora>> {
            override fun onResponse(call: Call<ArrayList<Distribuidora>>, response: Response<ArrayList<Distribuidora>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaDistribuidora = lista
                        val listNameDistribuidora = listaDistribuidora!!.map { it.nombre }
                        val stringArrayAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, listNameDistribuidora)
                        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerDistribuidora.adapter = stringArrayAdapter

                        spinnerDistribuidora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                distribuidoraSeleccionada = listaDistribuidora!![position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Distribuidora>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //obtiene los generos y crea un spinner con la lista de los nombres
    // al seleccionarlo se selecciona el objeto completo
    private fun obtenerGeneros() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerGenerosVideojuego()?.enqueue(object : Callback<ArrayList<GeneroVideojuego>> {
            override fun onResponse(call: Call<ArrayList<GeneroVideojuego>>, response: Response<ArrayList<GeneroVideojuego>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaGeneros = lista
                        val listNameGeneros = listaGeneros!!.map { it.nombre }
                        val stringArrayAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, listNameGeneros)
                        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerGenero.adapter = stringArrayAdapter

                        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                generoSeleccionado = listaGeneros!![position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<GeneroVideojuego>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}