package com.practica.finazapp.Vista

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_gastos)

        // Recibir los datos enviados desde el fragmento
        usuarioId = intent.getLongExtra("usuarioId", -1)
        val categoria = intent.getStringExtra("categoria") ?: ""

        recyclerView = findViewById(R.id.recyclerViewGastos) // Asegúrate de que el ID sea correcto
        recyclerView.layoutManager = LinearLayoutManager(this)

        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]

        // Obtener y mostrar gastos por categoría
        gastosViewModel.obtenerGastosMesCategoria(usuarioId, categoria)
        gastosViewModel.gastosMesCategoriaLiveData.observe(this) { gastosCat ->
            if (gastosCat != null) {
                adapter = GastoAdapterPrincipal(gastosCat)
                adapter.setOnItemClickListener2(this) // Pasamos la Activity como listener
                recyclerView.adapter = adapter
            } else {
                Log.d("ListaGastos", "No hay gastos para la categoría: $categoria")
            }
        }
    }

    // Implementación del método de la interfaz OnItemClickListener2
    override fun onItemClick2(gasto: GastoDTO) {
        Log.d("onItemClick", "Gasto clickeado: ${gasto.nombre_gasto}")
        val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_gasto, null)
        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val items = resources.getStringArray(R.array.categorias).toList()
        val adapter = CustomSpinnerAdapter(this, items)
        spinnerCategoria.adapter = adapter
        val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminarGasto)
        editTextCantidad.setText(gasto.valor.toString())
        val parts = gasto.fecha.split("-")
        val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"
        editTextFecha.setText(fechaFormateada)
        editTextDescripcion.setText(gasto.nombre_gasto)
        val posicionCategoria = items.indexOf(gasto.categoria)
        if (posicionCategoria != -1) {
            spinnerCategoria.setSelection(posicionCategoria)
        }

        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val categoria = spinnerCategoria.selectedItem.toString()
                val cantidad = editTextCantidad.text.toString()
                val fechaOriginal = editTextFecha.text.toString()
                val descripcion = editTextDescripcion.text.toString()

                if (categoria.isNotBlank() && cantidad.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {
                    try {
                        val valor = cantidad.toDouble()
                        val partes = fechaOriginal.split("/")
                        val dia = partes[0].padStart(2, '0')
                        val mes = partes[1].padStart(2, '0')
                        val anio = partes[2]
                        val fecha = "${anio}-${mes}-${dia}"
                        val gastoActualizado = GastoDTO(
                            id_gasto = gasto.id_gasto,
                            categoria = categoria,
                            valor = valor,
                            nombre_gasto = descripcion,
                            fecha = fecha
                        )
                        gastosViewModel.modificarGasto(gasto.id_gasto, gastoActualizado)
                        dialog.dismiss()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "La cantidad ingresada no es válida", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        btnEliminar.setOnClickListener {
            gastosViewModel.eliminarGasto(gasto.id_gasto)
            dialog.dismiss()
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
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
