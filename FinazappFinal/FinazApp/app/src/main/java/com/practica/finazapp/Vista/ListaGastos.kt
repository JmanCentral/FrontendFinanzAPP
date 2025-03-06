package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import com.practica.finazapp.ui.Estilos.CustomSpinnerAdapter
import java.util.Calendar

class ListaGastos : AppCompatActivity(), OnItemClickListener2 {

    private var usuarioId: Long = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GastoAdapterPrincipal
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var imgNoGastos: ImageView
    private lateinit var txtNoGastos: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_gastos)

        usuarioId = intent.getLongExtra("usuarioId", -1)

        imgNoGastos = findViewById(R.id.img_no_gastos)
        recyclerView = findViewById(R.id.recyclerViewGastos)
        txtNoGastos = findViewById(R.id.txt_no_gastos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]

        ObtenerGastos()

        gastosViewModel.operacionCompletada.observe(this) { completada ->
            if (completada) {
                ObtenerGastos()
            }
        }

        val btnActualizarLista = findViewById<ImageView>(R.id.update)
        val btndevolver = findViewById<ImageView>(R.id.devolverse)

        btnActualizarLista.setOnClickListener {
            ObtenerGastos()
        }

        val btnBuscarGasto = findViewById<ImageView>(R.id.busqueda)
        btnBuscarGasto.setOnClickListener { mostrarDialogoBuscarGasto() }

        val btnEliminarGasto = findViewById<ImageView>(R.id.serch)
        btnEliminarGasto.setOnClickListener { AdvertenciaGastos() }


        btndevolver.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        // No hagas nada para bloquear el botón atrás
    }

    private fun ObtenerGastos() {
        usuarioId = intent.getLongExtra("usuarioId", -1)
        val categoria = intent.getStringExtra("categoria") ?: ""

        gastosViewModel.obtenerGastosMesCategoria(usuarioId, categoria)

        gastosViewModel.gastosMesCategoriaLiveData.observe(this) { gastosCat ->
            if (!gastosCat.isNullOrEmpty()) {
                adapter = GastoAdapterPrincipal(gastosCat)
                adapter.setOnItemClickListener2(this)
                recyclerView.adapter = adapter

                recyclerView.visibility = View.VISIBLE
                imgNoGastos.visibility = View.GONE
                txtNoGastos.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                imgNoGastos.visibility = View.VISIBLE
                txtNoGastos.visibility = View.VISIBLE
            }
        }
    }


    private fun mostrarDialogoBuscarGasto() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_buscar_gasto, null)
        val editTextFechaInicial = dialogView.findViewById<EditText>(R.id.Busca_gasto_fecha_inicial)
        val editTextFechaFinal = dialogView.findViewById<EditText>(R.id.Busca_gasto_fecha_final)
        val botonBuscar = dialogView.findViewById<Button>(R.id.button3)

        editTextFechaInicial.setOnClickListener { showDatePickerDialog(editTextFechaInicial) }
        editTextFechaFinal.setOnClickListener { showDatePickerDialog(editTextFechaFinal) }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        botonBuscar.setOnClickListener {
            val fechaInicialStr = editTextFechaInicial.text.toString()
            val fechaFinalStr = editTextFechaFinal.text.toString()
            val categoria = intent.getStringExtra("categoria") ?: ""

            if (fechaInicialStr.isNotBlank() && fechaFinalStr.isNotBlank() && categoria.isNotBlank()) {
                try {
                    val fechaInicial = convertirFechaAFormatoISO(fechaInicialStr)
                    val fechaFinal = convertirFechaAFormatoISO(fechaFinalStr)

                    gastosViewModel.listarPorFechas(usuarioId, fechaInicial, fechaFinal, categoria)

                    gastosViewModel.ListarPorFechasLiveData.observe(this) { gastos ->
                        if (!gastos.isNullOrEmpty()) {
                            adapter = GastoAdapterPrincipal(gastos)
                            adapter.setOnItemClickListener2(this)
                            recyclerView.adapter = adapter
                            recyclerView.visibility = View.VISIBLE
                            imgNoGastos.visibility = View.GONE
                            txtNoGastos.visibility = View.GONE
                        } else {
                            recyclerView.visibility = View.GONE
                            imgNoGastos.visibility = View.VISIBLE
                            txtNoGastos.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ingrese todos los datos", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun convertirFechaAFormatoISO(fecha: String): String {
        val parts = fecha.split("/")
        if (parts.size != 3) throw IllegalArgumentException("Formato incorrecto")

        val dia = parts[0].padStart(2, '0')
        val mes = parts[1].padStart(2, '0')
        val anio = parts[2]

        return "$anio-$mes-$dia"
    }




    private fun AdvertenciaGastos() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que deseas eliminar todos los Gastos de esta categoría? Esta acción no se puede deshacer.")

        builder.setPositiveButton("Sí") { _, _ ->
            eliminarGastoPorIdusuarioAndCategoria()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Cierra el diálogo sin hacer nada
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun eliminarGastoPorIdusuarioAndCategoria() {
        val categoria = intent.getStringExtra("categoria") ?: ""

        if (usuarioId == -1L) {
            Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }
        gastosViewModel.eliminarGastosPorNombre(usuarioId, categoria)
        gastosViewModel.operacionCompletada.removeObservers(this) // Evita múltiples observaciones
        gastosViewModel.operacionCompletada.observe(this) { completada ->
            if (completada) {
                ObtenerGastos()
            }
        }
    }

    override fun onItemClick2(gasto: GastoDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_gasto, null)

        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminarGasto)

        val items = resources.getStringArray(R.array.categorias).toList()
        val adapter = CustomSpinnerAdapter(this, items)
        spinnerCategoria.adapter = adapter

        editTextCantidad.setText(gasto.valor.toString())
        val fecha = gasto.fecha
        val parts = fecha.split("-")
        val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"

        editTextFecha.setText(fechaFormateada)  // Mostrar la fecha en formato dd/MM/yyyy
        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }
        editTextDescripcion.setText(gasto.nombre_gasto)
        val posicionCategoria = items.indexOf(gasto.categoria)
        if (posicionCategoria != -1) spinnerCategoria.setSelection(posicionCategoria)


        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        btnEliminar.setOnClickListener {
            gastosViewModel.eliminarGasto(gasto.id_gasto)
            dialog.dismiss()
        }

        dialog.show()

        // Sobrescribir el comportamiento del botón "Guardar"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val categoria = spinnerCategoria.selectedItem.toString()
            val cantidad = editTextCantidad.text.toString()
            val fechaOriginal = editTextFecha.text.toString()
            val descripcion = editTextDescripcion.text.toString()

            if (categoria.isBlank() || cantidad.isBlank() || fechaOriginal.isBlank() || descripcion.isBlank()) {
                // Mostrar alerta sin cerrar el diálogo principal
                AlertDialog.Builder(this)
                    .setTitle("Campos vacíos")
                    .setMessage("Por favor complete todos los campos antes de guardar.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
            } else {
                try {
                    val valor = cantidad.toDouble()
                    val parts = fechaOriginal.split("/")
                    val dia = parts[0].padStart(2, '0')
                    val mes = parts[1].padStart(2, '0')
                    val anio = parts[2]
                    val fecha = "$anio-$mes-$dia"

                    val gastoActualizado = gasto.copy(
                        categoria = categoria,
                        valor = valor,
                        nombre_gasto = descripcion,
                        fecha = fecha
                    )
                    gastosViewModel.modificarGasto(gasto.id_gasto, gastoActualizado)
                    dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
                } catch (e: NumberFormatException) {
                    AlertDialog.Builder(this)
                        .setTitle("Cantidad inválida")
                        .setMessage("Ingrese una cantidad numérica válida.")
                        .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                        .create()
                        .show()
                }
            }
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
