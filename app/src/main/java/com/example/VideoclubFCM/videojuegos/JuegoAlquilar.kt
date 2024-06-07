package com.example.VideoclubFCM.videojuegos

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.VideoclubFCM.MainActivity
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.modelo.Alquiler
import com.example.VideoclubFCM.modelo.Videojuego
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class JuegoAlquilar : AppCompatActivity() {

    private lateinit var txtTitulo: TextView
    private lateinit var txtPlataforma: TextView
    private lateinit var txtGenero: TextView
    private lateinit var txtPrecio: TextView
    private lateinit var txtDistro: TextView
    private lateinit var txtMulti: TextView
    private lateinit var txtDesarrolladora: TextView
    private lateinit var btnAlquilar: Button
    private lateinit var imgJuego: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_juego_alquilar)

        // Inicializar vistas
        txtTitulo = findViewById(R.id.txtTitulo)
        txtPlataforma = findViewById(R.id.txtPlataforma)
        txtGenero = findViewById(R.id.txtGenero)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtDistro = findViewById(R.id.txtDistro)
        txtMulti = findViewById(R.id.txtMulti)

        txtDesarrolladora = findViewById(R.id.txtDesarrolladora)
        btnAlquilar = findViewById(R.id.btnAlquilar)
        imgJuego = findViewById(R.id.imageView)

        // Ocultar botón de alquilar si el usuario es invitado
        if (MainActivity.isInvitado()) {
            btnAlquilar.isVisible = false
        }

        // Recibir el objeto Videojuego del intent
        val videojuego: Videojuego? = intent.getParcelableExtra("videojuego")

        // Llenar las vistas con los datos del Videojuego
        videojuego?.let {
            with(it) {
                txtTitulo.text = "Título: ${productos.titulo}"
                txtPlataforma.text = "Plataforma: ${plataformas.nombre}"
                txtGenero.text = "Género: ${generos_videojuegos.nombre}"
                txtPrecio.text = "Precio: ${productos.precio_alquiler}"
                txtDistro.text = "Distribuidora: ${distribuidoras.nombre}"
                txtMulti.text = "Multijugador: ${if (multijugador == 1) "Sí" else "No"}"
                txtDesarrolladora.text = "Desarrolladora: ${desarrolladoras.nombre}"
                Glide.with(this@JuegoAlquilar).load(productos.portada).into(imgJuego)
            }

            // Configurar el evento de clic para el botón de alquilar
            btnAlquilar.setOnClickListener {
                mostrarConfirmacionDialogo(videojuego)
            }
        } ?: run {
            Toast.makeText(applicationContext, "Error: Videojuego no encontrado", Toast.LENGTH_SHORT).show()
        }

    }

    // Método para mostrar el diálogo de confirmación de alquiler
    private fun mostrarConfirmacionDialogo(videojuego: Videojuego) {
        AlertDialog.Builder(this)
            .setTitle("Confirmación")
            .setMessage("¿Está seguro que desea realizar el alquiler?")
            .setPositiveButton("Sí") { dialog, _ ->
                realizarAlquiler(videojuego)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Método para realizar el alquiler del videojuego, recibe un objeto alquiler y llama a insertar
    private fun realizarAlquiler(videojuego: Videojuego) {
        val alquiler = Alquiler(
            null,
            videojuego.productos.idProducto,
            null,
            MainActivity.getPersona()?.id_cliente,
            null,
            null,
            null,
            null,
            videojuego.productos
        )

        // Insertar los datos del alquiler en el servidor
        insertarDatos(alquiler)
    }

    // Método para insertar los datos del alquiler en el servidor, completa con las fechas
    private fun insertarDatos(alquiler: Alquiler) {
        val calendar = Calendar.getInstance()
        val fechaActual = calendar.time

        // Formatear la fecha actual a un string con el formato "AAAA-MM-DD"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActualFormatted = dateFormat.format(fechaActual)

        // Añadir una semana a la fecha actual
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val fechaHoyMasUnaSemana = calendar.time

        // Formatear la fecha de una semana más a un string con el formato "AAAA-MM-DD"
        val fechaHoyMasUnaSemanaFormatted = dateFormat.format(fechaHoyMasUnaSemana)

        // Crear instancia de la API
        val api = Cliente.obtenerCliente()!!.create(Api::class.java)

        // Realizar la llamada a la API para guardar el alquiler
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

            override fun onResponse(call: Call<Alquiler>, response: retrofit2.Response<Alquiler>) {
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