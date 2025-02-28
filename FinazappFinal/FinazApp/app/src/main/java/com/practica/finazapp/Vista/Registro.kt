package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.UserViewModel


class Registro : AppCompatActivity() {

    private lateinit var usuarioViewModel: UserViewModel
    private lateinit var sharedViewModel: SharedViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        validarCorreoEnTiempoReal()

        Log.d("Registro", "onCreate del Registro")
        usuarioViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val btnVolver = findViewById<TextView>(R.id.btnVolver)
        val txtInputContrasena2 = findViewById<TextInputEditText>(R.id.txtinputContrasena2)
        val txtInputConf = findViewById<TextInputEditText>(R.id.txtinputConf)
        val txtInputNombres = findViewById<TextInputEditText>(R.id.txtinputNombres)
        val txtInputApellidos = findViewById<TextInputEditText>(R.id.txtinputApellidos)
        val txtInputUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val btnRegistro1 = findViewById<Button>(R.id.btnRegistro)
        val txtAdvertencia = findViewById<TextView>(R.id.Advertencia)
        val txtCorreo = findViewById<TextInputEditText>(R.id.txtinputCorreo)

        val btnMostrarOcultar = findViewById<ImageView>(R.id.btnMostrarOcultar1)
        var isPasswordVisible = false

        val btnMostrarOcultar1= findViewById<ImageView>(R.id.btnMostrarOcultar2)
        var isPasswordVisible1 = false

        btnMostrarOcultar.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                txtInputContrasena2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnMostrarOcultar.setImageResource(R.drawable.ojoabierto) // Cambia al icono de ojo abierto
            } else {
                txtInputContrasena2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnMostrarOcultar.setImageResource(R.drawable.invisible) // Cambia al icono de ojo cerrado
            }

            txtInputContrasena2.setSelection(txtInputContrasena2.text?.length ?: 0) // Mantiene el cursor en su posición
        }

        btnMostrarOcultar1.setOnClickListener {
            isPasswordVisible1 = !isPasswordVisible1

            if (isPasswordVisible1) {
                txtInputConf.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnMostrarOcultar1.setImageResource(R.drawable.ojoabierto) // Cambia al icono de ojo abierto
            } else {
                txtInputConf.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnMostrarOcultar1.setImageResource(R.drawable.invisible) // Cambia al icono de ojo cerrado
            }

            txtInputConf .setSelection(txtInputConf .text?.length ?: 0) // Mantiene el cursor en su posición
        }



        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")

        mostrarDialogoNombres()
        mostrarDialogoApellidos()
        mostrarDialogoCorreo()

        btnRegistro1.setOnClickListener {
            val nombres = txtInputNombres.text.toString().trim()
            val apellidos = txtInputApellidos.text.toString().trim()
            val usuario = txtInputUsuario.text.toString().trim()
            val contrasena = txtInputContrasena2.text.toString()
            val confirmContrasena = txtInputConf.text.toString()
            val email = txtCorreo.text.toString().trim()
            val layoutCorreo = findViewById<TextInputLayout>(R.id.wrapperinputCorreo)

            when {
                nombres.isEmpty() || apellidos.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || confirmContrasena.isEmpty() || email.isEmpty()  -> {
                    txtAdvertencia.text = getString(R.string.todos_los_campos_son_obligatorios)
                    return@setOnClickListener
                }
                contrasena != confirmContrasena -> {
                    txtAdvertencia.text = getString(R.string.las_contrase_as_no_coinciden)
                    return@setOnClickListener
                }
                !regex.matches(contrasena) -> {
                    txtAdvertencia.text = getString(R.string.requerimientos_cont)
                    return@setOnClickListener
                }
                !esCorreoValido(email) -> {
                    layoutCorreo.error = "Correo inválido. Debe ser @gmail.com, @hotmail.com o @xxx.edu.co"
                    return@setOnClickListener
                }
            }

            val nuevoUsuario = UsuarioDTO(
                id_usuario = 0,
                username = usuario,
                email = email,
                contrasena = contrasena,
                nombre = nombres,
                apellido = apellidos,
                roles = setOf("USER")
            )

            usuarioViewModel.registrarUsuario(nuevoUsuario)
        }

        usuarioViewModel.errorLiveData1.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                mostrarDialogoError()
            }
        }

        usuarioViewModel.usuarioLiveData.observe(this) { usuario ->
            usuario?.let {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }


        btnVolver.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validarCorreoEnTiempoReal() {
        val txtCorreo = findViewById<TextInputEditText>(R.id.txtinputCorreo)
        val layoutCorreo = findViewById<TextInputLayout>(R.id.wrapperinputCorreo) // Asegúrate de tenerlo en el XML

        txtCorreo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (!esCorreoValido(email)) {
                    layoutCorreo.error = "Correo inválido. Debe ser @gmail.com, @hotmail.com o @xxx.edu.co"
                } else {
                    layoutCorreo.error = null // Si es válido, elimina el mensaje de error
                }
            }
        })
    }

    // Función para validar el email basado en las reglas del backend
    private fun esCorreoValido(email: String): Boolean {
        val patron = Regex("^[^\\s@]+@(gmail\\.com|hotmail\\.com|[a-zA-Z0-9]+\\.edu\\.co)$")
        return patron.matches(email)
    }



    private fun mostrarDialogoNombres() {
        AlertDialog.Builder(this)
            .setTitle("Uso de los Nombres")
            .setMessage("Tus nombres serán usados de forma segura en la aplicación únicamente para identificarte y mejorar tu experiencia de usuario.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun mostrarDialogoApellidos() {
        AlertDialog.Builder(this)
            .setTitle("Uso de los Apellidos")
            .setMessage("Tus apellidos serán usados de forma segura en la aplicación únicamente para fines de personalización y gestión de tu cuenta.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun mostrarDialogoCorreo() {
        AlertDialog.Builder(this)
            .setTitle("Uso del correo")
            .setMessage("El correo será utilizado únicamente para la recuperación de contraseña. Asegúrese de ingresarlo correctamente.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun mostrarDialogoError(){
        AlertDialog.Builder(this)
            .setTitle("Error al registrarse")
            .setIcon(R.drawable.problem)
            .setMessage("El nombre de usuario ya existe. Porfavor intenta con otro nombre de usuario.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()

    }

}

