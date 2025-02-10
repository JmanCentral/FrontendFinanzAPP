package com.practica.finazapp.Vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.LoginDTO

class Login : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        val txtUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val txtContrasena = findViewById<TextInputEditText>(R.id.txtinputContrasena)
        val txtAdvertencia = findViewById<TextView>(R.id.txtAdvertenciaLogin)

        // Observador para manejar la respuesta de login
        userViewModel.loginResponse.observe(this) { usuario ->
            if (usuario != null) {
                // Guardar el token
                guardarDatosUsuario(usuario.id, usuario.token)
                // Navegar al Dashboard
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
            }
        }

        userViewModel.errorLiveData.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                txtAdvertencia.text = error
            }
        }

        btnIngresar.setOnClickListener {
            val usuarioInput = txtUsuario.text.toString()
            val contrasenaInput = txtContrasena.text.toString()

            if (usuarioInput.isEmpty() || contrasenaInput.isEmpty()) {
                txtAdvertencia.text = "Por favor, ingrese usuario y contraseña"
            } else {
                val loginDTO = LoginDTO(
                    username = usuarioInput,
                    contrasena = contrasenaInput
                )
                userViewModel.iniciarSesion(loginDTO)
            }
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

}