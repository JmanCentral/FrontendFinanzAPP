package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.EmailRequest
import com.practica.finazapp.Entidades.LoginDTO
import com.practica.finazapp.ViewModelsApiRest.PasswordViewModel

class Login : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var passwordViewModel: PasswordViewModel

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("Login", "onCreate del Login para problemas")

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)


        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        val txtUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val txtContrasena = findViewById<TextInputEditText>(R.id.txtinputContrasena)
        val btnMostrarOcultar = findViewById<ImageView>(R.id.btnMostrarOcultar)
        var isPasswordVisible = false

        val btnrecuperacion = findViewById<TextView>(R.id.btnRecuperar)

        btnrecuperacion.setOnClickListener{
            recuperarcontra()
        }

        btnMostrarOcultar.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                txtContrasena.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnMostrarOcultar.setImageResource(R.drawable.ojoabierto) // Cambia al icono de ojo abierto
            } else {
                txtContrasena.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnMostrarOcultar.setImageResource(R.drawable.invisible) // Cambia al icono de ojo cerrado
            }

            txtContrasena.setSelection(txtContrasena.text?.length ?: 0) // Mantiene el cursor en su posición
        }


        userViewModel.loginResponse.observe(this) { usuario ->
            Log.d("Login", "Usuario recibido en el observador: $usuario") // 🕵️‍♂️ Verificar qué llega aquí
            if (usuario != null) {
                guardarDatosUsuario(usuario.id, usuario.token)
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()
            }
        }


        userViewModel.errorLiveData.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                mostrarDialogoError()
            }
        }


        passwordViewModel.errorLiveData.observe(this) { error ->
            Log.d("Login", "Error recibido en el observador: $error") // 🕵️‍♂️ Verificar qué llega aquí
            if (!error.isNullOrEmpty()) {
                mostrarDialogoError1()
            }
            }

        passwordViewModel.passwordLiveData.observe(this) { password ->
            Log.d("Login", "Password recibido en el observador: $password") // 🕵️‍♂️ Verificar qué llega aquí
            password?.let {
                mostrarDialogoEnvio()
            }
        }

        btnIngresar.setOnClickListener {
            val usuarioInput = txtUsuario.text.toString()
            val contrasenaInput = txtContrasena.text.toString()

                val loginDTO = LoginDTO(
                    username = usuarioInput,
                    contrasena = contrasenaInput
                )
                userViewModel.iniciarSesion(loginDTO)

        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun guardarDatosUsuario(id: Long, token: String) {
        val sharedPref = getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("USUARIO_ID", id)  // Guardar el ID del usuario
            putString("TOKEN", token)  // Guardar el Token
            apply()
        }
    }

    private fun mostrarDialogoError(){
        AlertDialog.Builder(this)
            .setTitle("Error al iniciar sesión")
            .setIcon(R.drawable.problem)
            .setMessage("Credenciales incorrectas. Por favor, verifica tu nombre de usuario y contraseña e inténtalo de nuevo.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()

    }

    private fun mostrarDialogoEnvio(){
        AlertDialog.Builder(this)
            .setTitle("Mensaje enviado al correo electrónico")
            .setIcon(R.drawable.disponi)
            .setMessage("Se envio el mensaje de recuperación de contraseña.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()

    }

    private fun mostrarDialogoError1(){
        AlertDialog.Builder(this)
            .setTitle("Error al recuperar contraseña")
            .setIcon(R.drawable.problem)
            .setMessage("Correo electrónico incorrecto")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()

    }

    private fun recuperarcontra() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_recuperar_password, null)
        val editTextCorreo = dialogView.findViewById<TextInputEditText>(R.id.Busca_correo)
        val btnRecuperar = dialogView.findViewById<Button>(R.id.buttonPassword)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()

        btnRecuperar.setOnClickListener {
            val correo = editTextCorreo.text.toString().trim()

            if (correo.isEmpty()) {
                Toast.makeText(this, "Ingrese un correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailRequest = EmailRequest(email = correo)

            passwordViewModel.recuperarpassword(emailRequest)
        }
    }
}