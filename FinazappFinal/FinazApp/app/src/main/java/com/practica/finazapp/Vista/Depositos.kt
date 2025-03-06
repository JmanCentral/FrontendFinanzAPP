package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.DepositoViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import java.text.ParseException
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

    override fun onBackPressed() {
        // No hagas nada para bloquear el botón atrás
    }

    @SuppressLint("MissingInflatedId")
    override fun onItemClick(deposito: DepositoDTO) {
        Log.d("DepositoClick", "Depósito seleccionado: $deposito")

        val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_deposito, null)

        val editTextMonto = dialogView.findViewById<EditText>(R.id.editTextMonto)
        val editTextdepositante = dialogView.findViewById<EditText>(R.id.editTextNombreDepositante)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminarDeposito)

        Log.d("DepositoClick", "Fecha formateada para edición: ${editTextFecha.text}")

        editTextMonto.setText(deposito.monto.toString())

        val fecha = deposito.fecha
        val parts = fecha.split("-")
        val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"

        editTextFecha.setText(fechaFormateada)  // Mostrar la fecha en formato dd/MM/yyyy
        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }
        editTextdepositante.setText(deposito.nombre_depositante)


        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
            .setNegativeButton("Cancelar") { dialog, _ ->
                Log.d("DepositoClick", "Edición cancelada por el usuario")
                dialog.dismiss()
            }
            .create()

        btnEliminar.setOnClickListener {
            Log.d("DepositoClick", "Eliminando depósito con ID: ${deposito.idDeposito}")
            depositoViewModel.eliminarDepositos(deposito.idDeposito, idAlcancia)
            dialog.dismiss()
        }

        dialog.show()

        // Sobrescribir el comportamiento del botón "Guardar"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val monto = editTextMonto.text.toString()
            val nombre = editTextdepositante.text.toString()
            val fechaOriginal = editTextFecha.text.toString()



            Log.d("DepositoClick", "Valores ingresados - Monto: $monto, Nombre: $nombre, Fecha: $fechaOriginal")

            if (monto.isBlank() || nombre.isBlank() || fechaOriginal.isBlank()) {
                // Mostrar alerta sin cerrar el diálogo principal
                AlertDialog.Builder(this)
                    .setTitle("Campos vacíos")
                    .setMessage("Por favor, complete todos los campos antes de guardar.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val montoDouble = monto.toDoubleOrNull()
            if (montoDouble == null) {
                // Mostrar alerta si el monto no es válido
                AlertDialog.Builder(this)
                    .setTitle("Monto inválido")
                    .setMessage("Ingrese un valor numérico válido para el monto.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            // Validar el formato de la fecha
            val parts = fechaOriginal.split("/")
            if (parts.size < 3) {
                // Mostrar alerta si el formato de la fecha es incorrecto
                AlertDialog.Builder(this)
                    .setTitle("Formato de fecha incorrecto")
                    .setMessage("El formato de la fecha debe ser dd/MM/yyyy.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val dia = parts[0].padStart(2, '0')
            val mes = parts[1].padStart(2, '0')
            val anio = parts[2]
            val fecha = "$anio-$mes-$dia"

            Log.d("DepositoClick", "Fecha convertida: $fecha")

            // Crear el objeto DepositoDTO actualizado
            val depositoActualizado = deposito.copy(
                monto = montoDouble,
                nombre_depositante = nombre,
                fecha = fecha
            )

            Log.d("DepositoClick", "Depósito actualizado: $depositoActualizado")

            // Modificar el depósito
            depositoViewModel.modificarDepositos(depositoActualizado, deposito.idDeposito, idAlcancia)
            dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
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
                depositoAdapter.setOnItemClickListener(this)
                recyclerViewDepositos.visibility = View.VISIBLE
                ivListaVacia.visibility = View.GONE
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
            .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
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

        // Sobrescribir el comportamiento del botón "Guardar"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val monto = dialogView.findViewById<TextInputEditText>(R.id.editTextMonto).text.toString().toDoubleOrNull() ?: 0.0
            val nombreDepositante = dialogView.findViewById<TextInputEditText>(R.id.editTextNombreDepositante).text.toString()
            val fecha = dialogView.findViewById<EditText>(R.id.editTextFecha).text.toString()

            Log.d("Depositos", "Monto: $monto, Nombre Depositante: $nombreDepositante, Fecha: $fecha")

            if (nombreDepositante.isEmpty() || fecha.isEmpty()) {
                // Mostrar alerta sin cerrar el diálogo principal
                AlertDialog.Builder(this)
                    .setTitle("Campos vacíos")
                    .setMessage("Por favor, complete todos los campos antes de guardar.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            // Validar el formato de la fecha
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = try {
                dateFormat.parse(fecha)
            } catch (e: ParseException) {
                Log.e("Depositos", "Formato de fecha incorrecto: $fecha", e)
                // Mostrar alerta si el formato de la fecha es incorrecto
                AlertDialog.Builder(this)
                    .setTitle("Formato de fecha incorrecto")
                    .setMessage("El formato de la fecha debe ser dd/MM/yyyy.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val fechaoriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

            // Crear el objeto DepositoDTO
            val depositoDTO = DepositoDTO(
                idDeposito = 0,
                monto = monto,
                nombre_depositante = nombreDepositante,
                fecha = fechaoriginal
            )

            Log.d("Depositos", "Registrando depósito con los datos: $depositoDTO")
            depositoViewModel.registrarDeposito(depositoDTO, usuarioId, idAlcancia)
            Toast.makeText(this, "Depósito registrado correctamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
        }
    }

    private fun showDatePickerDialog(editTextFecha: EditText) {
        // Obtener la fecha actual
        val calendar = android.icu.util.Calendar.getInstance()
        val year = calendar.get(android.icu.util.Calendar.YEAR)
        val month = calendar.get(android.icu.util.Calendar.MONTH)
        val dayOfMonth = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)

        // Crear y mostrar el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year1, monthOfYear, dayOfMonth1 ->
                val fechaSeleccionada = "$dayOfMonth1/${String.format("%02d", monthOfYear + 1)}/$year1"
                editTextFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

}

