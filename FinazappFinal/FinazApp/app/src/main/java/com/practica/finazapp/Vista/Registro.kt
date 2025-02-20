package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.UserViewModel
import com.google.firebase.messaging.FirebaseMessaging


class Registro : AppCompatActivity() {

    private lateinit var usuarioViewModel: UserViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var firebaseToken: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Obtener el token de Firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseToken = task.result
                Log.d("FirebaseToken", "Token: $firebaseToken")
            } else {
                Log.e("FirebaseToken", "Error obteniendo el token", task.exception)
            }
        }


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
            }

            val nuevoUsuario = UsuarioDTO(
                id_usuario = 0,
                username = usuario,
                email = email,
                contrasena = contrasena,
                nombre = nombres,
                apellido = apellidos,
                token = firebaseToken ?: "",
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
            .setTitle("Uso de los Apellidos")
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

