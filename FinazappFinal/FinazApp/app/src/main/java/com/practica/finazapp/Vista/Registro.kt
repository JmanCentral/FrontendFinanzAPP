package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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


class Registro : AppCompatActivity() {

    private lateinit var usuarioViewModel: UserViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        usuarioViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val btnVolver = findViewById<TextView>(R.id.btnVolver)
        val txtInputContrasena2 = findViewById<TextInputEditText>(R.id.txtinputContrasena2)
        val txtInputConf = findViewById<TextInputEditText>(R.id.txtinputConf)
        val txtInputNombres = findViewById<TextInputEditText>(R.id.txtinputNombres)
        val txtInputApellidos = findViewById<TextInputEditText>(R.id.txtinputApellidos)
        val txtInputUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val btnRegistro1 = findViewById<Button>(R.id.btnRegistro)
        val txtAdvertencia = findViewById<TextView>(R.id.Advertencia)

        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")

        mostrarDialogoNombres()
        mostrarDialogoApellidos()

        btnRegistro1.setOnClickListener {
            val nombres = txtInputNombres.text.toString()
            val apellidos = txtInputApellidos.text.toString()
            val usuario = txtInputUsuario.text.toString()
            val contrasena = txtInputContrasena2.text.toString()
            val confirmContrasena = txtInputConf.text.toString()

            if (nombres.isEmpty() || apellidos.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
                txtAdvertencia.text = getString(R.string.todos_los_campos_son_obligatorios)
                return@setOnClickListener
            }

            if (contrasena != confirmContrasena) {
                txtAdvertencia.text = getString(R.string.las_contrase_as_no_coinciden)
                return@setOnClickListener
            }

            if (!regex.matches(contrasena)) {
                txtAdvertencia.text = getString(R.string.requerimientos_cont)
                return@setOnClickListener
            }

            val nuevoUsuario = UsuarioDTO(
                id_usuario = 0,
                username = usuario,
                contrasena = contrasena,
                nombre = nombres,
                apellido = apellidos,
                roles = setOf("USER")
            )

            usuarioViewModel.registrarUsuario(nuevoUsuario)
        }

        usuarioViewModel.usuarioLiveData.observe(this) { usuario ->
            if (usuario != null) {
                // Registro exitoso, redirigir al dashboard
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()


                val intent = Intent(this, Login::class.java)
                intent.putExtra("usuario_id", usuario.id_usuario)
                startActivity(intent)
                finish()
            }
        }

        usuarioViewModel.errorLiveData.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                txtAdvertencia.text = error
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
}

