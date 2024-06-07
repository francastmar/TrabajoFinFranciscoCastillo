package com.example.VideoclubFCM

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.VideoclubFCM.administracion.InsertarCliente
import com.example.VideoclubFCM.conexion.Api
import com.example.VideoclubFCM.conexion.Cliente
import com.example.VideoclubFCM.databinding.ActivityLoginBinding
import com.example.VideoclubFCM.modelo.Clientes
import com.example.VideoclubFCM.modelo.Persona
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class Login : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private lateinit var binding: ActivityLoginBinding

    //insertar el cliente desde el login
    val resultInsertar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val bundle = data!!.getBundleExtra("Bundle")
            val cliente = bundle?.getParcelable<Clientes>("Cliente")
            val persona = bundle?.getParcelable<Persona>("persona")
            insertarDatos(cliente!!,persona!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = Cliente.obtenerCliente()

        //recoge el valor de los edit text y llama a la funcion de login
        binding.btnEmpezar.setOnClickListener {
            val username = binding.txtUser.text.toString()
            val password = binding.txtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                login(username, password)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Por favor, ingrese nombre de usuario y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //acceder como invitado pasando un 3 como valor que representa al invitado
        binding.btnInvitado.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            // Agregar un numero que no sea 1 ni 2 para diferenciar el rol del usuario(1 y 2 son admin y cliente)
            //cualquier otro es invitado
            intent.putExtra("invitado", 3)
            startActivity(intent)
        }

        // a la actividad crear cuenta
        binding.btnCrearCuenta.setOnClickListener{
            val intent = Intent(applicationContext, InsertarCliente::class.java)
            intent.putExtra("crear", true)
            resultInsertar.launch(intent)
        }

    }

    // recibe usuario y conraseña , verifica en la base de daos si existe el usuario
    private fun login(username: String, contraseña: String) {
        val api: Api? = retrofit?.create(Api::class.java)
        val call = api!!.login(username, contraseña)
        call.enqueue(object : Callback<Clientes> {
            override fun onResponse(call: Call<Clientes>, response: Response<Clientes>) {
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario?.id_persona != 0) {
                        // El objeto Persona no es nulo, continuar
                        val intent = Intent(applicationContext, MainActivity::class.java).apply {
                            MainActivity.setPersona(usuario!!)
                            putExtra("persona", usuario)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Usuario no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Error en la respuesta del servido, mostrar mensaje de error
                    Toast.makeText(
                        applicationContext,
                        "Error en la respuesta del servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Clientes>, t: Throwable) {
                // Error de red
                Toast.makeText(applicationContext, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // crear la cuenta desde el login
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
                        Log.v("clientes", clienteInsertado.toString())
                        Toast.makeText(applicationContext, "Cliente insertado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Fallo en la respuesta al insertar cliente", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Clientes>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}