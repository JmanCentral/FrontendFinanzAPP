package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzapp.viewmodels.AlcanciaViewModel
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.R

class Alcancias_Compartidas : AppCompatActivity(), AlcanciaListener {

    private var usuarioId: Long = -1
    private lateinit var alcanciaViewModel: AlcanciaViewModel
    private lateinit var alcanciaAdapter: AlcanciaAdapter
    private lateinit var recyclerViewAlcancias: RecyclerView
    private lateinit var ivListaVacia: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alcancias_compartidas)

        usuarioId = intent.getLongExtra("usuarioId", -1)

        // Inicializar las vistas con findViewById
        recyclerViewAlcancias = findViewById(R.id.recyclerViewAlcanciasCompartidas)

        alcanciaViewModel = ViewModelProvider(this).get(AlcanciaViewModel::class.java)

        // Configurar RecyclerView
        recyclerViewAlcancias.layoutManager = LinearLayoutManager(this)
        alcanciaAdapter = AlcanciaAdapter(emptyList()) // Inicializar con lista vacía
        recyclerViewAlcancias.adapter = alcanciaAdapter

        val codigo = intent.getStringExtra("codigo")
        if (codigo != null) {
            buscarAlcanciasPorCodigo(codigo)
        }

        val btnvolver = findViewById<ImageView>(R.id.btnImagenIzquierda)
        btnvolver.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("navigate_to_graphics", true)  // Indicamos que se debe ir al gráfico avanzado
            startActivity(intent)
        }
    }


    private fun buscarAlcanciasPorCodigo(codigo: String) {
        alcanciaViewModel.buscarAlcanciaPorCodigo(codigo)
        alcanciaViewModel.alcanciacodigoLiveData.observe(this) { alcancias ->
            if (!alcancias.isNullOrEmpty()) {
                alcanciaAdapter = AlcanciaAdapter(alcancias)
                alcanciaAdapter.setOnItemClickListener(this)
                recyclerViewAlcancias.adapter = alcanciaAdapter
                recyclerViewAlcancias.visibility = View.VISIBLE
            } else {
                recyclerViewAlcancias.visibility = View.GONE
            }
        }
    }

    override fun onItemClick5(alcancia: AlcanciaDTO) {
        // Crear un Intent para iniciar la Activity de depósitos
        val intent = Intent(this, Depositos::class.java).apply {
            putExtra("idAlcancia", alcancia.idAlcancia)
            putExtra("usuarioId", usuarioId)
        }
        startActivity(intent)
    }
}
