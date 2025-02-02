package com.practica.finazapp.Vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.UsuarioViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.UserViewModel
import org.mindrot.jbcrypt.BCrypt

class Login : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userViewModel: UserViewModel
    private var usuarioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Obtener el ID del usuario del Intent
        usuarioId = intent.getLongExtra("usuario_id", -1)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        sharedViewModel.setUsuarioId(usuarioId)
        // Inicializar ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Referencias a los elementos de la interfaz
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        val txtUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val txtContrasena = findViewById<TextInputEditText>(R.id.txtinputContrasena)
        val txtAdvertencia = findViewById<TextView>(R.id.txtAdvertenciaLogin)

        // Observador para manejar la respuesta de login
        userViewModel.usuarioLiveData.observe(this) { usuario ->
            if (usuario != null) {
                // Guardar sesión en SharedPreferences
                guardarSesion(usuario)

                guardarIdUsuario(usuario.id_usuario)

                // Navegar al Dashboard
                val intent = Intent(this, Dashboard::class.java)
                intent.putExtra("usuario_id", usuarioId)
                startActivity(intent)
                finish()
            }
        }

        // Observador para manejar errores
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
                val usuarioDTO = UsuarioDTO(
                    id_usuario = 0, // No se necesita para login
                    username = usuarioInput,
                    password = contrasenaInput,
                    nombre = "",
                    apellido = ""
                )
                userViewModel.loginUsuario(usuarioDTO)
            }
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun guardarIdUsuario(idUsuario: Long) {
        val sharedPref = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putLong("usuario_id", idUsuario)
            apply()  // Usamos 'apply' para guardar de forma asíncrona
        }
    }

    private fun guardarSesion(usuario: UsuarioDTO) {
        val sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("usuario_id", usuario.id_usuario)
        editor.putString("username", usuario.username)
        editor.putString("nombre", usuario.nombre)
        editor.putString("apellido", usuario.apellido)
        editor.apply()
    }
}

