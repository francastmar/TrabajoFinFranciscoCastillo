package com.example.VideoclubFCM.administracion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.databinding.ActivityInsertarPeliculaBinding
import com.example.VideoclubFCM.modelo.Pelicula
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Director
import com.example.VideoclubFCM.modelo.Genero
import com.example.VideoclubFCM.modelo.Idioma
import com.example.VideoclubFCM.modelo.Pais
import com.example.VideoclubFCM.modelo.Productos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Calendar

class InsertarPelicula : AppCompatActivity() {

    private lateinit var binding: ActivityInsertarPeliculaBinding
    private val REQUEST_READ_EXTERNAL_STORAGE = 1
    private var imagen: Uri? = null
    private var retrofit: Retrofit? = null

    //variables para el spiner de director
    private var listaDirectores: ArrayList<Director>? = null
    private lateinit var directorSeleccionado :Director
    private lateinit var spinnerDirector: Spinner

    //variables para el spinner de Genero
    private var listaGeneros: ArrayList<Genero>? = null
    private lateinit var generoSeleccionado :Genero
    private lateinit var spinnerGenero: Spinner

    //variables para el spinner de Pais
    private var listaPaises: ArrayList<Pais>? = null
    private lateinit var paisSeleccionado :Pais
    private lateinit var spinnerPais: Spinner

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
        binding = ActivityInsertarPeliculaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = Cliente.obtenerCliente()

        //Inicializar el spinner para seleccionar el director
        spinnerDirector = binding.spinnerDirector
        obtenerDirectores()

        //Inicializar el spinner para seleccionar el genero
        spinnerGenero = binding.spinnerGenero
        obtenerGeneros()

        //Inicializar el spinner para seleccionar el genero
        spinnerPais = binding.spinnerPais
        obtenerPaises()

        val btnAdd = binding.btnAdd
        val btnEdit = binding.btnEdit
        val etTitulo: EditText = binding.etTitulo
        val etAnyo: EditText = binding.etAnio
        val etDuracion: EditText = binding.etDuracion
        val etSinopsis: EditText = binding.etSinopsis

        if (intent.hasExtra("pelicula")) {
            btnAdd.visibility = View.INVISIBLE
            btnEdit.visibility = View.VISIBLE

            val peli: Pelicula? = intent.getParcelableExtra("pelicula")

            if (peli != null) {
                etTitulo.setText(peli.titulo_original)
                etDuracion.setText(peli.duracion.toString())
                etSinopsis.setText(peli.sinopsis)
                binding.portadaPreview.setImageURI(Uri.parse(peli.productos.portada))
            }
        } else {
            btnAdd.visibility = View.VISIBLE
            btnEdit.visibility = View.INVISIBLE
        }

        binding.botonanadirfoto.setOnClickListener {
            solicitarPermisoAlmacenamiento()
        }

        // hacer funciones para obtener los directores y cargarlos en un spinner asi como los paises y el genero
        // y el idioma, hay que meter el id del elemento seleccionado en pelicula

        // la funcion de recoger idioma se puede hacer en el main activity, ya que se necesitará al insertar el jeugo tambien

        btnAdd.setOnClickListener {
            // Obtener los valores de los campos de entrada
            val titulo = etTitulo.text.toString().trim()
            val anyo = etAnyo.text.toString().trim()
            val duracion = etDuracion.text.toString().trim()
            val sinopsis = etSinopsis.text.toString().trim()

            // Validar campos vacíos
            if (titulo.isEmpty() || anyo.isEmpty() || duracion.isEmpty() || sinopsis.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de año (debería ser un número válido)
            val anyoInt = anyo.toIntOrNull()
            if (anyoInt == null || anyoInt < 1888 || anyoInt > Calendar.getInstance().get(Calendar.YEAR)) {
                Toast.makeText(this, "Año no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar duración (debería ser un número válido)
            val duracionInt = duracion.toIntOrNull()
            if (duracionInt == null || duracionInt <= 0) {
                Toast.makeText(this, "Duración no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //establecer el director seleccionado del spinner
            val anioText = etAnyo.text.toString()
            val producto = Productos(
                idProducto = 0,
                titulo = etTitulo.text.toString(),
                anio = anioText.toInt(),
                portada = imagen?.toString() ?: "",
                precio_alquiler = 3.5,
                precio_compra = 20.0,
            )
            val pelicula = Pelicula(
                id_pelicula = 0,
                titulo_original = etTitulo.text.toString(),
                duracion = etDuracion.text.toString().toInt(),
                sinopsis = etSinopsis.text.toString(),
                actores = emptyList(),
                directores = directorSeleccionado,
                paises = paisSeleccionado,
                productos = producto,
                // recoger posteriormente, actores tambien, y despues de la insercion con post hacer actualizacion con put
                generos = emptyList(),
                // recoger los paises ,generos y directores con spinner y cuando seleccione
                // una opcion que ponga el id aqui
                id_pais = paisSeleccionado.id_pais,
                id_genero = generoSeleccionado.id_genero,
                id_director = directorSeleccionado.id_director
            )

            val bundle = Bundle()
            bundle.putParcelable("Pelicula", pelicula)

            val intent = Intent().apply {
                putExtra("Bundle", bundle)
            }
            setResult(RESULT_OK, intent)

            finish()
        }

        btnEdit.setOnClickListener {
            // editar con los campos cambiados, no implementado
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

    private fun getDirectorNames(directors: List<Director>?): List<String> {
        return directors?.map { it.nombre } ?: emptyList()
    }
    private fun obtenerDirectores() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerDirectores()?.enqueue(object : Callback<ArrayList<Director>> {
            override fun onResponse(call: Call<ArrayList<Director>>, response: Response<ArrayList<Director>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaDirectores = lista
                        val listNameDirector = getDirectorNames(listaDirectores)
                        val stringArrayAdapter3 = ArrayAdapter(applicationContext, R.layout.spinner_item, listNameDirector)
                        stringArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerDirector.adapter = stringArrayAdapter3

                        spinnerDirector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listaDirectores!![position]
                                    // asignar el director seleccionado a la variable director
                                    directorSeleccionado = seleccionado
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Director>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getGenderNames(genders: List<Genero>?): List<String> {
        return genders?.map { it.nombre } ?: emptyList()
    }
    private fun obtenerGeneros() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerGenerosPelicula()?.enqueue(object : Callback<ArrayList<Genero>> {
            override fun onResponse(call: Call<ArrayList<Genero>>, response: Response<ArrayList<Genero>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaGeneros = lista
                        val listNameGeneros = getGenderNames(listaGeneros)
                        val stringArrayAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, listNameGeneros)
                        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerGenero.adapter = stringArrayAdapter

                        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listaGeneros!![position]
                                // asignar el director seleccionado a la variable director
                                generoSeleccionado = seleccionado
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Genero>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPaisesNames(paises: List<Pais>?): List<String> {
        return paises?.map { it.nombre } ?: emptyList()
    }
    private fun obtenerPaises() {
        val api: Api? = retrofit?.create(Api::class.java)
        api?.obtenerPaises()?.enqueue(object : Callback<ArrayList<Pais>> {
            override fun onResponse(call: Call<ArrayList<Pais>>, response: Response<ArrayList<Pais>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPaises = lista

                        val listNamePais = getPaisesNames(listaPaises)
                        val stringArrayAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item,
                            listNamePais)
                        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerPais.adapter = stringArrayAdapter

                        spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val seleccionado = listaPaises!![position]
                                // asignar el pais seleccionado a la variable director
                                paisSeleccionado = seleccionado
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Pais>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}