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
        val editTextNombre = dialogView.findViewById<EditText>(R.id.Busca_gasto)
        val botonBuscar = dialogView.findViewById<Button>(R.id.button3)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        botonBuscar.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val categoria = intent.getStringExtra("categoria") ?: ""

            if (nombre.isNotBlank()) {
                gastosViewModel.listarGastosPorNombre(usuarioId, nombre, categoria)
                gastosViewModel.listarhastospornombre.observe(this) { gastos ->
                    if (!gastos.isNullOrEmpty()) {
                        adapter = GastoAdapterPrincipal(gastos)
                        adapter.setOnItemClickListener2(this)
                        recyclerView.adapter = adapter
                        recyclerView.visibility = View.VISIBLE
                        imgNoGastos.visibility = View.GONE
                    } else {
                        recyclerView.visibility = View.GONE
                        imgNoGastos.visibility = View.VISIBLE
                        txtNoGastos.visibility = View.VISIBLE
                    }
                }
            } else {
                Toast.makeText(this, "Ingrese un nombre válido", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
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
        editTextFecha.setText(gasto.fecha.replace("-", "/"))
        editTextDescripcion.setText(gasto.nombre_gasto)

        val posicionCategoria = items.indexOf(gasto.categoria)
        if (posicionCategoria != -1) spinnerCategoria.setSelection(posicionCategoria)

        editTextFecha.setOnClickListener { showDatePickerDialog(editTextFecha) }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val categoria = spinnerCategoria.selectedItem.toString()
                val cantidad = editTextCantidad.text.toString().toDoubleOrNull()
                val fechaOriginal = editTextFecha.text.toString()
                val descripcion = editTextDescripcion.text.toString()

                if (cantidad != null && categoria.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {

                    // Realizar la conversión de fecha y guardar el nuevo gasto
                    val parts = fechaOriginal.split("/")
                    val dia = parts[0].padStart(2, '0')
                    val mes = parts[1].padStart(2, '0')
                    val anio = parts[2]
                    val fecha = "${anio}-${mes}-${dia}"

                    val gastoActualizado = gasto.copy(
                        categoria = categoria,
                        valor = cantidad,
                        nombre_gasto = descripcion,
                        fecha = fecha
                    )
                    gastosViewModel.modificarGasto(gasto.id_gasto, gastoActualizado)
                } else {
                    Toast.makeText(this, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
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
