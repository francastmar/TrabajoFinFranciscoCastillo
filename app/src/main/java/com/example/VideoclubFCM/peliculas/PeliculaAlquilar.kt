package com.example.VideoclubFCM.peliculas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.VideoclubFCM.MainActivity
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.databinding.ActivityPeliculaAlquilarBinding
import com.example.VideoclubFCM.modelo.Alquiler
import com.example.VideoclubFCM.modelo.Pelicula
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PeliculaAlquilar : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private lateinit var txtTitulo: TextView
    private lateinit var txtDirector: TextView
    private lateinit var txtGenero: TextView
    private lateinit var txtPrecio:TextView
    private lateinit var txtDuracion: TextView
    private lateinit var txtSinopsis: TextView
    private lateinit var btnAlquilar: Button
    private lateinit var imgPelicula: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelicula_alquilar)
        retrofit = Cliente.obtenerCliente()

        imgPelicula = findViewById(R.id.imgPelicula)
        txtTitulo = findViewById(R.id.txtTitulo)
        txtDirector = findViewById(R.id.txtDirector)
        txtGenero = findViewById(R.id.txtGenero)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtDuracion = findViewById(R.id.txtDuracion)
        txtSinopsis = findViewById(R.id.txtSinopsi)
        btnAlquilar = findViewById(R.id.btnAlquilar)
        if (MainActivity.isInvitado()) {
            btnAlquilar.isVisible = false
        }
        val pelicula: Pelicula? = intent.getParcelableExtra("pelicula")
        // persona de MainActivity (quien se ha logueado)
        val persona = MainActivity.getPersona()

        if (pelicula != null) {

            txtTitulo.text = pelicula.titulo_original
            txtDirector.text = "Director: ${pelicula.directores.nombre}"
            txtGenero.text = "Género: ${pelicula.generos.joinToString(", ") { it.nombre }}"
            txtPrecio.text = "Precio: ${pelicula.productos.precio_alquiler} €"
            txtDuracion.text = "Duración: ${pelicula.duracion} minutos"
            txtSinopsis.text = pelicula.sinopsis

            Glide.with(this).load(pelicula.productos.portada).into(imgPelicula)

            //click del boton para alquilar
            btnAlquilar.setOnClickListener {
                // dialogo de confirmación
                AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Está seguro que desea realizar la acción?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        // respuesta positiva, realizar alquiler
                        if (persona != null) {
                            val alquiler = Alquiler(
                                null,
                                pelicula.productos.idProducto,
                                null,
                                persona.id_cliente,
                                null,
                                null,
                                null,
                                null,
                                pelicula.productos
                            )

                            insertarDatos(alquiler)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error: Persona no encontrada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } else {
            Toast.makeText(applicationContext, "Error: Película no encontrada", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // inserta un alquiler , pone las fechas aqui
    private fun insertarDatos(alquiler: Alquiler) {

        val calendar = Calendar.getInstance()
        val fechaActual = calendar.time
        // Crear una instancia de SimpleDateFormat con el formato deseado
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Formatear la fecha actual a un string con el formato "AAAA-MM-DD"
        val fechaActualFormatted = dateFormat.format(fechaActual)
        // Añadir una semana a la fecha actual
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val fechaHoyMasUnaSemana = calendar.time
        // Formatear la fecha de una semana más a un string con el formato "AAAA-MM-DD"
        val fechaHoyMasUnaSemanaFormatted = dateFormat.format(fechaHoyMasUnaSemana)


        val api: Api = retrofit!!.create(Api::class.java)

        api.guardarAlquiler(
            null,
            alquiler.id_producto,
            alquiler.id_cliente,
            alquiler.id_cliente,
            fechaActualFormatted,
            null,
            fechaHoyMasUnaSemanaFormatted,
            null,
            alquiler.producto
        ).enqueue(object : Callback<Alquiler> {

            override fun onResponse(call: Call<Alquiler>, response: Response<Alquiler>) {
                Log.d("valor", alquiler.toString())
                Log.d("Respuesta", "Entrando a onResponse")
                if (response.isSuccessful) {
                    Log.d("Respuesta", "Respuesta recibida")
                    val nuevoAlquiler = response.body()
                    if (nuevoAlquiler != null) {
                        Toast.makeText(applicationContext, "Alquiler realizado", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("Respuesta", "Sin respuesta del servidor")
                    Toast.makeText(
                        applicationContext,
                        "Fallo en la respuesta del alquiler",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<Alquiler>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}