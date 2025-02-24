package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.ProyeccionDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel

class Recurrente : AppCompatActivity()  {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var adapter: RecurrenteAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recurrente)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        gastosViewModel = ViewModelProvider(this).get(SpendViewModel::class.java)
        recyclerView = findViewById(R.id.recyclerViewRecurrente)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuarioId = intent.getLongExtra("usuarioId", -1)
        ObtenerGastosRecurrentes()

        gastosViewModel = ViewModelProvider(this).get(SpendViewModel::class.java)
    }

    private fun  ObtenerGastosRecurrentes() {

        usuarioId = intent.getLongExtra("usuarioId", -1)
        gastosViewModel.obtenerFrecuentesTedeGastos(usuarioId)
        gastosViewModel.obtenerfrecuentestedegastos.observe(this) { recordatorioslist ->
            if (recordatorioslist != null) {
                adapter = RecurrenteAdapter(recordatorioslist)
                // Pasamos la Activity como listener
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No se encontraron gastos recurrentes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        // No hagas nada para bloquear el botón atrás
    }
    
}