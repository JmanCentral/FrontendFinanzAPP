package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel

class Categorias : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IncomeViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private var usuarioId: Long = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categorias)

        // Obtener el ID del usuario del Intent
        usuarioId = intent.getLongExtra("usuario_id", -1)

        // Inicializar ViewModels
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        ingresoViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]

        // Almacenar el ID del usuario en el SharedViewModel
        sharedViewModel.setUsuarioId(usuarioId)

        // Referencia al botón para gestionar las categorías
        val btnCategoria = findViewById<Button>(R.id.btn_categorias)

        // Al hacer clic en el botón, validar los gastos antes de redirigir
        val miFragment1 = FragmentCategorias()
        // Agregar el fragmento al contenedor si no está ya agregado
        if (supportFragmentManager.findFragmentById(R.id.fragment_container1) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container1, miFragment1)
                .commit()
        }
        var continuarADashboard = false // Bandera para controlar la redirección

        btnCategoria.setOnClickListener {
            gastosViewModel.obtenerValorGastosMes(usuarioId)
            gastosViewModel.valorGastosMesLiveData.observe(this) { totalGastos ->
                if (totalGastos != null && totalGastos > 0) {
                    if (continuarADashboard) {
                        // Redirigir si el usuario ya decidió continuar al Dashboard
                        val intent = Intent(this, Dashboard::class.java)
                        intent.putExtra("usuario_id", usuarioId)
                        startActivity(intent)
                    } else {
                        // Preguntar si el usuario quiere continuar o seguir ingresando más gastos
                        AlertDialog.Builder(this)
                            .setTitle("Acción requerida")
                            .setMessage("Ya tienes gastos registrados. ¿Quieres continuar al Dashboard o agregar más gastos?")
                            .setPositiveButton("Continuar") { dialog, _ ->
                                continuarADashboard = true // Cambiar la bandera para que al próximo click redirija
                                btnCategoria.performClick() // Volver a hacer clic en el botón para redirigir
                            }
                            .setNegativeButton("Agregar más gastos") { dialog, _ ->
                                dialog.dismiss() // Cerrar el diálogo y permitir seguir ingresando gastos
                            }
                            .show()
                    }
                } else {
                    // Si no hay gastos válidos, mostrar el AlertDialog
                    AlertDialog.Builder(this)
                        .setTitle("Información")
                        .setMessage("Debes ingresar un gasto válido para poder continuar al Dashboard.")
                        .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    override fun onBackPressed() {

        // No hacemos nada aquí para evitar que el usuario regrese a la actividad anterior
    }
}


