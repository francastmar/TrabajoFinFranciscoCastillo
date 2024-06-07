package com.example.VideoclubFCM.administracion

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Pelicula
import com.example.VideoclubFCM.modelo.Videojuego
import com.example.VideoclubFCM.videojuegos.JuegoAlquilar
import com.example.VideoclubFCM.videojuegos.JuegosAdapter
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

class Fragment5 : Fragment() {

    private var retrofit: Retrofit? = null
    private var juegoAdapter: AdapterJuegosAdmin? = null
    private var listaJuegos: ArrayList<Videojuego>? = null


    //recibe resulado OK para añadir el videojuego y llama a la duncion que inserta
    val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                val bundle = data.getBundleExtra("Bundle")
                val videojuego = bundle?.getParcelable<Videojuego>("Videojuego")
                // Le pasamos el Uri de la portada y obtenemos la imagen
                val bitmap = leerImagenDesdeUri(videojuego?.productos?.portada!!.toUri())
                val fichero = bitmapAFichero(bitmap!!, "imagenElegida.jpg")
                if (fichero != null) {
                    val imagenPart = prepararDatosParaSubida(fichero)
                    insertarJuego(imagenPart, videojuego)
                }
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_fragmentadmin, container, false)
        // Infla el layout y obtiene los datos
        val texto = view.findViewById<TextView>(R.id.txtSeccion)
        texto.text = "VIDEOJUEGOS"
        retrofit = Cliente.obtenerCliente()
        obtenerDatos()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        juegoAdapter = AdapterJuegosAdmin()

        juegoAdapter!!.setOnItemClickListener(View.OnClickListener { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val juegoSeleccionado = listaJuegos?.get(position)
            if (juegoSeleccionado != null) {
                val intent = Intent(requireContext(), IInsertarVideojuego::class.java)
                intent.putExtra("juego", juegoSeleccionado)
                startActivity(intent)
            }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(context, IInsertarVideojuego::class.java)
            result.launch(intent)
        }
    }

    //obener los daos que se muestran en el recycler
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

    //separa los valores para insertarlos a la base de datos
    private fun insertarJuego(imagen: MultipartBody.Part, videojuego: Videojuego) {
        val api: Api? = Cliente.retrofit?.create(Api::class.java)

        val titulo = RequestBody.create(MediaType.parse("text/plain"), videojuego.productos.titulo)
        val anio = RequestBody.create(MediaType.parse("text/plain"), videojuego.productos.anio.toString())
        val precio_alquiler = RequestBody.create(MediaType.parse("text/plain"), videojuego.productos.precio_alquiler.toString())
        val precio_compra = RequestBody.create(MediaType.parse("text/plain"), videojuego.productos.precio_compra.toString())
        val doblado = RequestBody.create(MediaType.parse("text/plain"), videojuego.doblado.toString())
        val multijugador = RequestBody.create(MediaType.parse("text/plain"), videojuego.multijugador.toString())
        val id_plataforma = RequestBody.create(MediaType.parse("text/plain"), videojuego.plataformas.id_plataforma.toString())
        val id_distribuidora = RequestBody.create(MediaType.parse("text/plain"), videojuego.distribuidoras.id_distribuidora.toString())
        val id_desarrolladora = RequestBody.create(MediaType.parse("text/plain"), videojuego.desarrolladoras.id_desarrolladora.toString())
        val id_genero_videojuego = RequestBody.create(MediaType.parse("text/plain"), videojuego.generos_videojuegos.id_genero.toString())

        api?.insertarJuego(
            titulo,
            anio,
            imagen,
            precio_alquiler,
            precio_compra,
            doblado,
            multijugador,
            id_plataforma,
            id_distribuidora,
            id_desarrolladora,
            id_genero_videojuego
        )?.enqueue(object : Callback<Videojuego> {
            override fun onResponse(call: Call<Videojuego>, response: Response<Videojuego>) {
                if (response.isSuccessful) {
                    val videojuegoInsertado = response.body()
                    if (videojuegoInsertado != null) {
                        // se añade el videojuego recién insertado a la lista
                        obtenerDatos()

                    }
                } else {
                    Toast.makeText(context, "Fallo en la respuesta!!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Videojuego>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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