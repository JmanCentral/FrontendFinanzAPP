package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.DepositoViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Depositos : AppCompatActivity(), DepositoListener {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private val depositoViewModel: DepositoViewModel by viewModels()
    private lateinit var depositoAdapter: DepositoAdapter
    private var idAlcancia: Long = -1 // Variable para almacenar el idAlcancia

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_depositos)

        Log.d("Depositos", "onCreate() llamado")

        usuarioId = intent.getLongExtra("usuarioId", -1)

        // Obtener el idAlcancia del Intent
        idAlcancia = intent.getLongExtra("idAlcancia", -1L)
        if (idAlcancia == -1L) {
            Log.e("Depositos", "Error: No se encontró la alcancía")
            Toast.makeText(this, "Error: No se encontró la alcancía", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Depositos", "idAlcancia obtenido: $idAlcancia")
        }

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        usuarioId = intent.getLongExtra("usuarioId", -1)

        // Configurar RecyclerView
        val recyclerViewDepositos = findViewById<RecyclerView>(R.id.recyclerViewDepositos)
        recyclerViewDepositos.layoutManager = LinearLayoutManager(this)
        depositoAdapter = DepositoAdapter(emptyList()) // Inicializar con lista vacía
        recyclerViewDepositos.adapter = depositoAdapter

        val btnRegistrar = findViewById<ImageView>(R.id.btnRegistrarDeposito)
        val btnvolver = findViewById<ImageView>(R.id.btnImagenIzquierda)


        obtenerDepositos(idAlcancia)


            // Configurar el botón de registrar depósito
            Log.d("Depositos", "Usuario ID observado: $usuarioId")
            btnRegistrar.setOnClickListener {
                registrarDepositos(idAlcancia, usuarioId)
            }

        btnvolver.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("navigate_to_graphics", true)  // Indicamos que se debe ir al gráfico avanzado
            startActivity(intent)

        }

        depositoViewModel.operacionCompletada.observe(this) { completada ->
            if (completada) {
                obtenerDepositos(idAlcancia)
            }
        }

    }

    private fun obtenerDepositos(idAlcancia: Long) {
        Log.d("Depositos", "Llamando a obtenerDepositos() para idAlcancia: $idAlcancia")
        depositoViewModel.obtenerDepositos(idAlcancia)
        depositoViewModel.depositosList.observe(this, Observer { depositos ->
            val recyclerViewDepositos = findViewById<RecyclerView>(R.id.recyclerViewDepositos)
            val ivListaVacia = findViewById<ImageView>(R.id.ivListaVacia)
            val tvTitulo = findViewById<TextView>(R.id.tvTitulo)

            Log.d("Depositos", "Depósitos recibidos: ${depositos?.size}")
            if (!depositos.isNullOrEmpty()) {
                depositoAdapter.updateList(depositos)
                recyclerViewDepositos.visibility = View.VISIBLE
                ivListaVacia.visibility = View.GONE
                tvTitulo.visibility = View.GONE
                Log.d("Depositos", "Lista de depósitos no vacía")
            } else {
                recyclerViewDepositos.visibility = View.GONE
                ivListaVacia.visibility = View.VISIBLE
                tvTitulo.visibility = View.VISIBLE
                Log.d("Depositos", "Lista de depósitos vacía")
            }
        })

        depositoViewModel.errorLiveData.observe(this, Observer { error ->
            error?.let {
                Log.e("Depositos", "Error al obtener depósitos: $error")
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registrarDepositos(idAlcancia: Long, usuarioId: Long) {
        Log.d("Depositos", "Iniciando registro de depósito para alcancía: $idAlcancia, usuario: $usuarioId")
        val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_deposito, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Registrar Depósito")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val monto = dialogView.findViewById<TextInputEditText>(R.id.editTextMonto).text.toString().toDoubleOrNull() ?: 0.0
                val nombreDepositante = dialogView.findViewById<TextInputEditText>(R.id.editTextNombreDepositante).text.toString()
                val fecha = dialogView.findViewById<EditText>(R.id.editTextFecha).text.toString()

                Log.d("Depositos", "Monto: $monto, Nombre Depositante: $nombreDepositante, Fecha: $fecha")

                if (nombreDepositante.isNotEmpty() && fecha.isNotEmpty()) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = dateFormat.parse(fecha)
                    val fechaoriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

                    val depositoDTO = DepositoDTO(
                        idDeposito = 0,
                        monto = monto,
                        nombre_depositante = nombreDepositante,
                        fecha = fechaoriginal
                    )

                    Log.d("Depositos", "Registrando depósito con los datos: $depositoDTO")
                    depositoViewModel.registrarDeposito(depositoDTO, usuarioId, idAlcancia)
                    Toast.makeText(this, "Depósito registrado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("Depositos", "Campos incompletos al registrar depósito")
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                Log.d("Depositos", "Registro de depósito cancelado")
                dialog.dismiss()
            }
            .create()

        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        editTextFecha.setOnClickListener {
            Log.d("Depositos", "Abriendo selector de fecha")
            showDatePickerDialog(editTextFecha)
        }

        dialog.show()
    }

    private fun showDatePickerDialog(editTextFecha: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedDay.toString().padStart(2, '0')}/${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                editTextFecha.setText(formattedDate)
                Log.d("Depositos", "Fecha seleccionada: $formattedDate")
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    // Implementación de la interfaz DepositoListener
    override fun onItemClick(deposito: DepositoDTO) {
        Log.d("Depositos", "Clic en depósito: ${deposito.nombre_depositante}")
        // Lógica al hacer clic en un elemento
        Toast.makeText(this, "Clic en: ${deposito.nombre_depositante}", Toast.LENGTH_SHORT).show()
    }
}

