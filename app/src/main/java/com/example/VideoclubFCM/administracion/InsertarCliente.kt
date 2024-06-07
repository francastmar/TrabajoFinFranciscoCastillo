package com.example.VideoclubFCM.administracion

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.VideoclubFCM.R
import com.example.VideoclubFCM.databinding.ActivityInsertarClienteBinding
import com.example.VideoclubFCM.modelo.Clientes
import com.example.VideoclubFCM.modelo.Persona
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class InsertarCliente : AppCompatActivity() {

    private lateinit var binding: ActivityInsertarClienteBinding
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnEdit = findViewById<Button>(R.id.btnEdit)

        val etNombre: EditText = findViewById(R.id.et_nombre)
        val etApellido1: EditText = findViewById(R.id.et_apellido1)
        val etApellido2: EditText = findViewById(R.id.et_apellido2)
        val etDireccion: EditText = findViewById(R.id.et_Direccion)
        val etMail: EditText = findViewById(R.id.et_mail)
        val etTelefono: EditText = findViewById(R.id.et_telefono)
        val etUsername: EditText = findViewById(R.id.et_username)
        val etPassword: EditText = findViewById(R.id.et_contrasenya)
        val datePickerFechaNacimiento: DatePicker = findViewById(R.id.datePicker_fecha_nacimiento)
        var esCliente: Int = 0

        binding.btnVer.setOnClickListener {
            if (isPasswordVisible) {
                // Ocultar contraseña
                binding.etContrasenya.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnVer.setImageResource(R.drawable.eye_closed) // Cambia el icono
            } else {
                // Mostrar contraseña
                binding.etContrasenya.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnVer.setImageResource(R.drawable.eye_open) // Cambia el icono
            }
            isPasswordVisible = !isPasswordVisible

            // Mueve el cursor al final del texto
            binding.etContrasenya.setSelection(binding.etContrasenya.text.length)
        }

        // si llega un cliente significa actualizar
        if(intent.hasExtra("cliente")){
            btnAdd.visibility = View.INVISIBLE
            btnEdit.visibility = View.VISIBLE
            val cliente = intent.getParcelableExtra<Clientes>("cliente")
            if (cliente != null) {
                etNombre.setText(cliente.personas.nombre)
                etApellido1.setText(cliente.personas.apellido1)
                etApellido2.setText(cliente.personas.apellido2)
                etDireccion.setText(cliente.personas.direccion)
                etMail.setText(cliente.personas.mail)
                etTelefono.setText(cliente.personas.telefono)
                etUsername.setText(cliente.personas.username)
                //llamar a el metodo para actualizar cliente
            }
        }else{
            //si el intent viene vacio entonces esta creando uno nuevo
            btnAdd.visibility = View.VISIBLE
            btnEdit.visibility = View.INVISIBLE
            binding.btnAdd.setOnClickListener {

                // Obtener los valores de los campos de entrada
                val nombre = etNombre.text.toString()
                val apellido1 = etApellido1.text.toString()
                val apellido2 = etApellido2.text.toString()
                val direccion = etDireccion.text.toString()
                val mail = etMail.text.toString()
                val telefono = etTelefono.text.toString()
                val username = etUsername.text.toString()
                val fechaNacimientoString = "${datePickerFechaNacimiento.year}-${datePickerFechaNacimiento.month + 1}-${datePickerFechaNacimiento.dayOfMonth}"
                val formatoFechaNacimiento = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fechaNacimientoDate = formatoFechaNacimiento.parse(fechaNacimientoString)
                val fechaNacimientoFormateada = formatoFechaNacimiento.format(fechaNacimientoDate)

                // Validar campos vacíos
                if (nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() || direccion.isEmpty() ||
                    mail.isEmpty() || telefono.isEmpty() || username.isEmpty() || fechaNacimientoFormateada.isEmpty()) {
                    Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Validar formato de correo electrónico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //condición para ver si el usuario esta siendo insertado por si mismo o por un trabajador
                var contrasenya: String = " "

                contrasenya = if (intent.hasExtra("crear")) {
                    etPassword.text.toString()
                }else{
                    generarContrasena(
                        longitud = 12,
                        incluirMayusculas = true,
                        incluirMinusculas = true,
                        incluirNumeros = true,
                        incluirSimbolos = true
                    )
                }

                // Crear el objeto Persona con los valores recolectados
                val persona = Persona(
                    idPersona = 0, // El ID se establecerá cuando se guarde en la base de datos
                    nombre = nombre,
                    apellido1 = apellido1,
                    apellido2 = apellido2,
                    direccion = direccion,
                    mail = mail,
                    telefono = telefono,
                    nacimiento = fechaNacimientoFormateada.toString(),
                    username = username,
                    password = contrasenya, // implementar otra forma de generar contraseñas(por ejemplo generar aleatoriamentey mandarla al usuario por correo)
                    idRol = 2 //va a ser siempre 2 que es el rol de cliente
                )
                val cliente = Clientes(
                    id_cliente = 0, // El ID se establecerá cuando se guarde en la base de datos
                    id_persona = persona.idPersona,
                    metodo_pago = "ES666222333444555",
                    personas = persona
                )
                val bundle = Bundle()
                bundle.putParcelable("persona", persona)
                bundle.putParcelable("Cliente", cliente)

                val intent = Intent().apply {
                    putExtra("Bundle", bundle)
                }
                setResult(RESULT_OK, intent)
                finish()
                // guardar el la persona
            }
        }
    }
    fun generarContrasena(
        longitud: Int = 12,
        incluirMayusculas: Boolean = true,
        incluirMinusculas: Boolean = true,
        incluirNumeros: Boolean = true,
        incluirSimbolos: Boolean = true
    ): String {
        val mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val minusculas = "abcdefghijklmnopqrstuvwxyz"
        val numeros = "0123456789"
        val simbolos = "!@#\$%^&*()-_=+<>?"

        var caracteresDisponibles = ""

        if (incluirMayusculas) caracteresDisponibles += mayusculas
        if (incluirMinusculas) caracteresDisponibles += minusculas
        if (incluirNumeros) caracteresDisponibles += numeros
        if (incluirSimbolos) caracteresDisponibles += simbolos

        require(caracteresDisponibles.isNotEmpty()) {
            "Debe incluir al menos un tipo de carácter en la contraseña."
        }

        return (1..longitud)
            .map { caracteresDisponibles[Random.nextInt(caracteresDisponibles.length)] }
            .joinToString("")
    }
}