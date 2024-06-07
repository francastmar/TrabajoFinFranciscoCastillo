package com.example.VideoclubFCM.administracion

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
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
import com.example.VideoclubFCM.modelo.Pelicula
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class Fragment4 : Fragment() {

    private var retrofit: Retrofit? = null
    private var peliAdapter: AdapterPeliculasAdmin? = null
    private var listaPelis: ArrayList<Pelicula>? = null

    // recibe el resultado OK desde la actividad donde se insertan los datos y llama al metodo de insertar
    val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                val bundle = data.getBundleExtra("Bundle")
                val pelicula = bundle?.getParcelable<Pelicula>("Pelicula")
                // Le pasamos el Uri de la portada y obtenemos la imagen
                val bitmap = leerImagenDesdeUri(pelicula?.productos?.portada!!.toUri())
                val fichero = bitmapAFichero(bitmap!!, "imagenElegida.jpg")
                if (fichero != null) {
                    val imagenPart = prepararDatosParaSubida(fichero)
                    insertarpelicula(imagenPart, pelicula)
                }
            }
        }
    }

    val resultActualizar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                val bundle = data.getBundleExtra("Bundle")
                val pelicula = bundle?.getParcelable<Pelicula>("Pelicula")
                Log.v("actualizar", pelicula.toString())
                actualizarPelicula(pelicula)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_fragmentadmin, container, false)
        val texto = view.findViewById<TextView>(R.id.txtSeccion)
        texto.text = "PELICULAS"
        retrofit = Cliente.obtenerCliente()
        obtenerDatos()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        // adapter
        peliAdapter = AdapterPeliculasAdmin()
        recyclerView.adapter = peliAdapter



        peliAdapter!!.setOnItemClickListener(View.OnClickListener { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val peliculaSeleccionada = listaPelis?.get(position)
            if (peliculaSeleccionada != null) {
                val intent = Intent(requireContext(), InsertarPelicula::class.java)
                intent.putExtra("pelicula", peliculaSeleccionada)
                startActivity(intent)
            }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(context, InsertarPelicula::class.java)
            result.launch(intent)
        }
    }

    private fun obtenerDatos() {
        val api: Api? = retrofit?.create(Api::class.java)

        api?.obtenerPeliculas()?.enqueue(object : Callback<ArrayList<Pelicula>> {
            override fun onResponse(call: Call<ArrayList<Pelicula>>, response: Response<ArrayList<Pelicula>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null) {
                        listaPelis = lista
                        // Update adapter data
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

    // divide los datos que va a recibir el motodo insertar pelicula del API
    private fun insertarpelicula(imagen: MultipartBody.Part, pelicula: Pelicula) {

        val api: Api? = Cliente.retrofit?.create(Api::class.java)

        val titulo = RequestBody.create(MediaType.parse("text/plain"), pelicula.titulo_original)
        val duracion = RequestBody.create(MediaType.parse("text/plain"), pelicula.duracion.toString())
        val sinopsis = RequestBody.create(MediaType.parse("text/plain"), pelicula.sinopsis)
        val director = RequestBody.create(MediaType.parse("text/plain"), pelicula.directores.toString())
        val pais = RequestBody.create(MediaType.parse("text/plain"), pelicula.paises.toString())
        val tituloOriginal = RequestBody.create(MediaType.parse("text/plain"), pelicula.productos.titulo)
        val anio = RequestBody.create(MediaType.parse("text/plain"), pelicula.productos.anio.toString())
        val precioC = RequestBody.create(MediaType.parse("text/plain"), pelicula.productos.precio_compra.toString())
        val precioA = RequestBody.create(MediaType.parse("text/plain"), pelicula.productos.precio_alquiler.toString())
        val idPais = RequestBody.create(MediaType.parse("text/plain"), pelicula.id_pais.toString())
        val idGenero = RequestBody.create(MediaType.parse("text/plain"), pelicula.id_genero.toString())
        val idDirector = RequestBody.create(MediaType.parse("text/plain"), pelicula.id_director.toString())

        api?.insertarPelicula(tituloOriginal,anio,imagen,precioA,precioC, titulo, duracion, sinopsis,director,pais,idPais,idGenero,idDirector)?.enqueue(object :
            Callback<Pelicula> {
            override fun onResponse(call: Call<Pelicula>, response: Response<Pelicula>) {
                if (response.isSuccessful) {
                    val peliculaInsertada = response.body()

                    if (peliculaInsertada != null) {
                        //se añade la pelicula recien insertada a la lista
                        peliAdapter?.anyadirALista(peliculaInsertada)
                        //obtengo el ultimo indice de la lista de peliculas existentes y le agrego el genero
                        val ultimoIndice = listaPelis!!.lastIndex
                        agregarGeneroAPelicula(listaPelis!![ultimoIndice].id_pelicula,pelicula.id_genero)

                    }
                } else
                    Toast.makeText(context,"Fallo en la respuesta!!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Pelicula>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun agregarGeneroAPelicula(idPelicula: Int, idGenero: Int) {
        val api: Api? = retrofit?.create(Api::class.java)
        Log.v("actualizar", idPelicula.toString() + " " +idGenero.toString())
        // Llamar al método para agregar género a la película
        api?.anyadirGeneroAPelicula(idPelicula, idGenero)?.enqueue(object : Callback<Pelicula> {
            override fun onResponse(call: Call<Pelicula>, response: Response<Pelicula>) {
                if (response.isSuccessful) {
                    val peliculaActualizada = response.body()
                    if (peliculaActualizada != null) {
                        obtenerDatos()
                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta al añadir género a la película", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Pelicula>, t: Throwable) {
                Toast.makeText(context, "Error al añadir género a la película: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarPelicula(pelicula: Pelicula?){
        val api: Api? = retrofit?.create(Api::class.java)
        Log.v("actualizar", "entran2 a actualizar pelicula")
        Log.v("actualizar", pelicula.toString())
        // Utilizamos la operación let para confirmar que el id no es null
        pelicula!!.id_pelicula.let {
            api?.actualizaPelicula(it, pelicula)?.enqueue(object : Callback<Pelicula> {
                override fun onResponse(call: Call<Pelicula>, response: Response<Pelicula>) {
                    if (response.isSuccessful) {
                        Log.v("actualizar", "entran2 a actualizar pelicula")
                        val peli = response.body()
                        if (peli != null) {
                            Toast.makeText(context, "Pelicula insertada con exito", Toast.LENGTH_SHORT).show()
                        }
                    } else
                        Toast.makeText(context,"Fallo en la respuesta de actualizar pelicula", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Pelicula>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    // Obtiene la imagen desde un Uri
    private fun leerImagenDesdeUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
    // Transforma imagen en un fichero
    private fun bitmapAFichero(bitmap: Bitmap, nombreFichero: String): File? {
        val file = File(requireContext().cacheDir, nombreFichero)
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file
    }
    // Convierte el fichero de imagen a un MultipartBody.Part para enviarlo al servidor
    private fun prepararDatosParaSubida(fichero: File): MultipartBody.Part {
        val requestFile = RequestBody.create(MediaType.parse("image/*"), fichero)
        val imagenPart = MultipartBody.Part.createFormData("portada", fichero.name, requestFile)

        return imagenPart
    }
}