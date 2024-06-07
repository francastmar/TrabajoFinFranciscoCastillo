package com.example.VideoclubFCM

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.VideoclubFCM.administracion.Fragment4
import com.example.VideoclubFCM.administracion.Fragment5
import com.example.VideoclubFCM.administracion.Fragment6
import com.example.VideoclubFCM.charts.Charts
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.peliculas.Fragment1
import com.example.VideoclubFCM.databinding.ActivityMainBinding
import com.example.VideoclubFCM.misAlquileres.Fragment3
import com.example.VideoclubFCM.modelo.Clientes
import com.example.VideoclubFCM.modelo.Director
import com.example.VideoclubFCM.modelo.Genero
import com.example.VideoclubFCM.modelo.Pelicula
import com.example.VideoclubFCM.modelo.Persona
import com.example.VideoclubFCM.peliculas.PeliculasAdapter
import com.example.VideoclubFCM.videojuegos.Fragment2
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    companion object {
        private var persona: Clientes? = null
        private var esInvitado: Boolean = false

        fun getPersona(): Clientes? {
            return persona
        }

        fun setPersona(nuevaPersona: Clientes) {
            persona = nuevaPersona
        }

        fun isInvitado(): Boolean {
            return esInvitado
        }

        fun setInvitado(invitado: Boolean) {
            esInvitado = invitado
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Configurar ActionBar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = binding.drawerLayout

        val navigationView: NavigationView = findViewById(R.id.navview)
        val headerView = navigationView.getHeaderView(0)
        val textViewUsername = headerView.findViewById<TextView>(R.id.txtUsername)

        // si accede una persona o un invitado se cambian las opciones de menú dependiendo de su rol,
        // y se pone el nombre de usuario en el texto del menú
        if (intent.hasExtra("persona") || intent.hasExtra("invitado")) {
            val persona = intent.getParcelableExtra<Clientes>("persona")
            Log.v("log",persona.toString())
            var idRol = 0

            //set a la variable esInvitado para ocultar los botones de las actividades de alquilar
            //(si es invitado no puede alquilar)
            if (intent.hasExtra("invitado")) {
                idRol = 3
                textViewUsername.text = "Invitado"
                setInvitado(true)
            }
            if (intent.hasExtra("persona")) {
                idRol = persona!!.personas.idRol
                textViewUsername.text = persona.personas.username
            }

            val menu = navigationView.menu
            when (idRol) {
                2 -> {
                    // Es un trabajador, ocultar opciones de cliente
                    menu.findItem(R.id.menu_opcion_4).isVisible = false
                    menu.findItem(R.id.menu_opcion_5).isVisible = false
                    menu.findItem(R.id.menu_opcion_6).isVisible = false
                    menu.findItem(R.id.menu_opcion_7).isVisible = false
                }
                1 -> {
                    // Es un cliente, ocultar opciones de trabajador
                    menu.findItem(R.id.menu_seccion_1).isVisible = false
                    menu.findItem(R.id.menu_seccion_2).isVisible = false
                    menu.findItem(R.id.menu_seccion_3).isVisible = false
                    menu.findItem(R.id.menu_opcion_7).isVisible = false
                }
                else -> {
                    // Es un invitado, ocultar opciones de trabajador y mis productos
                    menu.findItem(R.id.menu_opcion_4).isVisible = false
                    menu.findItem(R.id.menu_opcion_5).isVisible = false
                    menu.findItem(R.id.menu_opcion_6).isVisible = false
                    menu.findItem(R.id.menu_seccion_3).isVisible = false
                    menu.findItem(R.id.menu_opcion_7).isVisible = false
                }
            }


            // distintas opciones del menú
            binding.navview.setNavigationItemSelectedListener {
                var fragmentTransaction = false
                lateinit var fragment: Fragment

                when (it.itemId) {
                    R.id.menu_seccion_1 -> {
                        fragment = Fragment1()
                        fragmentTransaction = true
                    }

                    R.id.menu_seccion_2 -> {
                        fragment = Fragment2()
                        fragmentTransaction = true
                    }

                    R.id.menu_seccion_3 -> {
                        fragment = Fragment3()
                        fragmentTransaction = true
                    }

                    R.id.menu_opcion_4 -> {
                        fragment = Fragment4()
                        fragmentTransaction = true
                    }

                    R.id.menu_opcion_5 -> {
                        fragment = Fragment5()
                        fragmentTransaction = true
                    }

                    R.id.menu_opcion_6 -> {
                        fragment = Fragment6()
                        fragmentTransaction = true
                    }
                    R.id.menu_opcion_7 -> {
                        // Iniciar la actividad Charts
                        val intent = Intent(this, Charts::class.java)
                        startActivity(intent)
                    }
                }

                if (fragmentTransaction) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit()

                    // Mostramos que la opción se ha pulsado.
                    it.isChecked = true

                    // Mostramos el título de la sección en el Toolbar.
                    title = it.title
                }

                // Cerramos el Drawer
                drawerLayout.closeDrawer(GravityCompat.START)

                true
            }

            // si el usuario es invitado
            if(esInvitado){
                // Mostrar Fragment 1 por defecto al iniciar la actividad
                val defaultFragment = Fragment1()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, defaultFragment)
                    .commit()
            }else{
                //si el usuario logueado es un trabajador
                if(persona!!.personas.idRol == 1){
                    // Mostrar Fragment 4 por defecto al iniciar la actividad como trabajador
                    val defaultFragment = Fragment4()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, defaultFragment)
                        .commit()
                }else{
                    // Mostrar Fragment 1 por defecto al iniciar la actividad
                    val defaultFragment = Fragment1()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, defaultFragment)
                        .commit()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Si está abierto lo cerramos sino lo abrimos.
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        // Si está abierto al pulsar "Atrás" lo cerramos.
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}
