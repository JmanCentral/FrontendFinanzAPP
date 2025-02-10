package com.practica.finazapp.Vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

        Log.d("Login", "onCreate del Login para problemas")

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        val txtUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val txtContrasena = findViewById<TextInputEditText>(R.id.txtinputContrasena)
        val txtAdvertencia = findViewById<TextView>(R.id.txtAdvertenciaLogin)

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

    private fun mostrarDialogoError(){
        AlertDialog.Builder(this)
            .setTitle("Error al iniciar sesión")
            .setIcon(R.drawable.problem)
            .setMessage("Credenciales incorrectas. Por favor, verifica tu nombre de usuario y contraseña e inténtalo de nuevo.")
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .create().show()

    }

}