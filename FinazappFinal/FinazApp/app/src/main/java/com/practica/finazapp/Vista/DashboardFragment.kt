package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.databinding.FragmentDashboardBinding
import com.practica.finazapp.ui.Estilos.CustomSpinnerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import lecho.lib.hellocharts.model.*
import java.text.NumberFormat
import android.graphics.Color as Color1


class DashboardFragment : Fragment(), OnItemClickListener2 {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var ingresoViewModel: IncomeViewModel
    private var disponible: Double = 0.0
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        ingresoViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]


        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentGastos", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                cargarDatos()
                val bloqueTransporte = binding.bloqueTransporte
                val bloqueGastosVarios = binding.bloqueGastosVarios
                val bloqueMercado = binding.bloqueMercado
                val bloqueServicios = binding.bloqueServicios
                val bloqueAlimentos = binding.bloqueAlimentos
                val recyclrerViewTransporte = binding.recyclerViewTransporte
                val recyclrerViewGastosVarios = binding.recyclerViewGastosVarios
                val recyclrerViewMercado = binding.recyclerViewMercado
                val recyclrerViewServicios = binding.recyclerViewServicios
                val recyclrerViewAlimentos = binding.recyclerViewAlimentos


                bloqueTransporte.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewTransporte, "Transporte")
                }
                bloqueMercado.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewMercado, "Mercado")
                }
                bloqueServicios.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewServicios, "Servicios")
                }
                bloqueAlimentos.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewAlimentos, "Alimentos")
                }
                bloqueGastosVarios.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewGastosVarios, "Gastos Hormiga")
                }


            }
        }

        gastosViewModel.operacionCompletada.observe(viewLifecycleOwner) { completada ->
            if (completada == true) {
                cargarDatos()// Actualizar la UI
            }
        }
        val btnNuevoGasto = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        btnNuevoGasto.setOnClickListener {

            ingresoViewModel.obtenerTotalIngresos(usuarioId)
            ingresoViewModel.totalIngresosLiveData.observeOnce(viewLifecycleOwner) { totalIngresos ->
                if (totalIngresos == null || totalIngresos == 0.0) {
                    // No hay ingresos registrados, mostrar un mensaje y evitar el registro de gastos
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Aviso")
                    builder.setMessage("Debe poner ingresos antes de registrar un gasto.")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()

                } else {

                    val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_gasto, null)
                    val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
                    val items = resources.getStringArray(R.array.categorias).toList()
                    val adapter = CustomSpinnerAdapter(requireContext(), items)
                    spinnerCategoria.adapter = adapter
                    val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
                    val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
                    val editTextDescripcion =
                        dialogView.findViewById<EditText>(R.id.editTextDescripcion)

                    editTextFecha.setOnClickListener {
                        showDatePickerDialog(editTextFecha)
                    }

                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton("Guardar") { dialog, _ ->
                            val categoria = spinnerCategoria.selectedItem.toString()
                            val cantidad = editTextCantidad.text.toString()
                            val fechaOriginal = editTextFecha.text.toString()
                            val descripcion = editTextDescripcion.text.toString()

                            // Validar que los campos obligatorios no estén vacíos
                            if (categoria.isNotBlank() && cantidad.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {
                                try {
                                    // Validar que la cantidad sea un número válido
                                    val valor = cantidad.toDouble()

                                    // Realizar la conversión de fecha y guardar el nuevo gasto
                                    val parts = fechaOriginal.split("/")
                                    val dia = parts[0].padStart(2, '0')
                                    val mes = parts[1].padStart(2, '0')
                                    val anio = parts[2]
                                    val fecha = "${anio}-${mes}-${dia}"
                                    val nuevoGasto = GastoDTO(
                                        id_gasto = 0,
                                        categoria = categoria,
                                        fecha = fecha,
                                        valor = valor,
                                        nombre_gasto = descripcion
                                    )

                                    gastosViewModel.registrarGasto(usuarioId,nuevoGasto)


                                    dialog.dismiss()
                                } catch (e: NumberFormatException) {
                                    // Manejar el caso en que la cantidad no sea un número válido
                                    Toast.makeText(
                                        requireContext(),
                                        "La cantidad ingresada no es válida",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Mostrar un mensaje de error si algún campo obligatorio está vacío
                                Toast.makeText(
                                    requireContext(),
                                    "Por favor complete todos los campos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    dialog.show()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    fun onBackPressed() {
        // No hacemos nada aquí para evitar que el usuario regrese a la actividad anterior
    }

    private fun showDatePickerDialog(editTextFecha: EditText) {
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear y mostrar el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year1, monthOfYear, dayOfMonth1 ->
                // Aquí obtienes la fecha seleccionada y la estableces en el EditText
                val fechaSeleccionada = "$dayOfMonth1/${monthOfYear + 1}/$year1"
                editTextFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun disponible() {

    }


    @SuppressLint("SetTextI18n")
    private fun cargarDatos() {

        gastosViewModel.obtenerDineroDisponible(usuarioId)
        gastosViewModel.dineroDisponibleLiveData.observeOnce(viewLifecycleOwner) { disp ->
            val numberFormat = NumberFormat.getInstance()
            numberFormat.maximumFractionDigits = 2
            if (disp != null) {
                disponible = disp
                val disponibleTextView = binding.cantidadDisponible
                disponibleTextView.setText("${numberFormat.format(disponible)}$")
                val barraDisponible = binding.barraDisponible
                cargarBarraDisp(disponible, barraDisponible)
            }
        }
        gastosViewModel.obtenerValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
        gastosViewModel.valorGastosMesCategoriaLiveData
            .observeOnce(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val gastosVariosTextView = binding.cantidadGastosVarios
                    gastosVariosTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraGastosVarios = binding.barraGastosVarios
                    cargarBarra(cantidadCategoria, barraGastosVarios)
                }
            }

        gastosViewModel.obtenerValorGastosMesCategoria1(usuarioId, "Alimentos")
        gastosViewModel.valorGastosMesCategoriaLiveData1
            .observeOnce(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val AlimentosTextView = binding.cantidadAlimentos
                    AlimentosTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraAlimentos = binding.barraAlimentos
                    cargarBarra(cantidadCategoria, barraAlimentos)
                }
            }

        gastosViewModel.obtenerValorGastosMesCategoria2(usuarioId, "Transporte")
        gastosViewModel.valorGastosMesCategoriaLiveData2
            .observeOnce(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val TransporteTextView = binding.cantidadTransporte
                    TransporteTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraTransporte = binding.barraTransporte
                    cargarBarra(cantidadCategoria, barraTransporte)
                }
            }

        gastosViewModel.obtenerValorGastosMesCategoria3(usuarioId, "Servicios")
        gastosViewModel.valorGastosMesCategoriaLiveData3
            .observeOnce(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val ServiciosTextView = binding.cantidadServicios
                    ServiciosTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraServicios = binding.barraServicios
                    cargarBarra(cantidadCategoria, barraServicios)
                }
            }

        gastosViewModel.obtenerValorGastosMesCategoria4(usuarioId, "Mercado")
        gastosViewModel.valorGastosMesCategoriaLiveData4
            .observeOnce(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val MercadoTextView = binding.cantidadMercado
                    MercadoTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraMercado = binding.barraMercado
                    cargarBarra(cantidadCategoria, barraMercado)
                }
            }

        gastosViewModel.obtenerValorGastosMes(usuarioId)
        gastosViewModel.valorGastosMesLiveData.observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes != null) {
                val numberFormat = NumberFormat.getInstance()
                numberFormat.maximumFractionDigits = 2
                val cantidadGastos = gastosMes
                val gastadosTextView = binding.TxtGastoTotal
                gastadosTextView.setText("${numberFormat.format(cantidadGastos)}$")
                cargarDona()
            }
        }

        ingresoViewModel.obtenerTotalIngresos(usuarioId)
        ingresoViewModel.totalIngresosLiveData.observeOnce(viewLifecycleOwner) { ingresoMensual ->
            if (ingresoMensual != null) {
                // Luego cargamos los gastos por categoría y los comparamos
                verificarPorcentajeDeGastos(ingresoMensual)
            }
        }

    }

    private fun verificarPorcentajeDeGastos(ingresoMensual: Double) {
        // Gastos Hormiga
        gastosViewModel.obtenerValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
        gastosViewModel.valorGastosMesCategoriaLiveData
            .observeOnce(viewLifecycleOwner) { cantidadGastosVarios ->
                cantidadGastosVarios?.let {
                    val porcentajeGastosVarios = (cantidadGastosVarios / ingresoMensual) * 100
                    if (porcentajeGastosVarios > obtenerLimitePorCategoria("Gastos Hormiga", ingresoMensual)) {
                        mostrarAdvertencia("Gastos Hormiga", porcentajeGastosVarios)
        }
                }
            }

        // Alimentos
        gastosViewModel.obtenerValorGastosMesCategoria1(usuarioId, "Alimentos")
        gastosViewModel.valorGastosMesCategoriaLiveData1
            .observeOnce(viewLifecycleOwner) { cantidadAlimentos ->
                cantidadAlimentos?.let {
                    val porcentajeAlimentos = (cantidadAlimentos / ingresoMensual) * 100
                    if (porcentajeAlimentos > obtenerLimitePorCategoria("Alimentos", ingresoMensual)) {
                        mostrarAdvertencia("Alimentos", porcentajeAlimentos)
                    }
                }
            }

        // Transporte
        gastosViewModel.obtenerValorGastosMesCategoria2(usuarioId, "Transporte")
        gastosViewModel.valorGastosMesCategoriaLiveData2
            .observeOnce(viewLifecycleOwner) { cantidadTransporte ->
                cantidadTransporte?.let {
                    val porcentajeTransporte = (cantidadTransporte / ingresoMensual) * 100
                    if (porcentajeTransporte > obtenerLimitePorCategoria("Transporte", ingresoMensual)) {
                        mostrarAdvertencia("Transporte", porcentajeTransporte)
                    }
                }
            }

        // Servicios
        gastosViewModel.obtenerValorGastosMesCategoria3(usuarioId, "Servicios")
        gastosViewModel.valorGastosMesCategoriaLiveData3
            .observeOnce(viewLifecycleOwner) { cantidadServicios ->
                cantidadServicios?.let {
                    val porcentajeServicios = (cantidadServicios / ingresoMensual) * 100
                    if (porcentajeServicios > obtenerLimitePorCategoria("Servicios", ingresoMensual)) {
                        mostrarAdvertencia("Servicios", porcentajeServicios)
                    }
                }
            }

        // Mercado
        gastosViewModel.obtenerValorGastosMesCategoria4(usuarioId, "Mercado")
        gastosViewModel.valorGastosMesCategoriaLiveData4
            .observeOnce(viewLifecycleOwner) { cantidadMercado ->
                cantidadMercado?.let {
                    val porcentajeMercado = (cantidadMercado / ingresoMensual) * 100
                    if (porcentajeMercado > obtenerLimitePorCategoria("Mercado", ingresoMensual)) {
                        mostrarAdvertencia("Mercado", porcentajeMercado)
                    }
                }
            }
    }

    private fun obtenerLimitePorCategoria(categoria: String, ingresoMensual: Double): Double {
        return when (ingresoMensual) {
            in 1_000_000.0..1_500_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 5.0
                    "Alimentos" -> 35.0
                    "Servicios" -> 20.0
                    "Transporte" -> 15.0
                    "Mercado" -> 25.0
                    else -> 100.0
                }
            }
            in 1_500_000.0..2_500_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 6.0
                    "Alimentos" -> 30.0
                    "Servicios" -> 18.0
                    "Transporte" -> 17.0
                    "Mercado" -> 29.0
                    else -> 100.0
                }
            }
            in 2_500_000.0..4_000_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 8.0
                    "Alimentos" -> 28.0
                    "Servicios" -> 15.0
                    "Transporte" -> 15.0
                    "Mercado" -> 34.0
                    else -> 100.0
                }
            }
            in 4_000_000.0..6_000_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 10.0
                    "Alimentos" -> 25.0
                    "Servicios" -> 12.0
                    "Transporte" -> 15.0
                    "Mercado" -> 38.0
                    else -> 100.0
                }
            }
            in 6_000_000.0..10_000_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 12.0
                    "Alimentos" -> 22.0
                    "Servicios" -> 10.0
                    "Transporte" -> 14.0
                    "Mercado" -> 42.0
                    else -> 100.0
                }
            }
            else -> {
                when (categoria) {
                    "Gastos Hormiga" -> 15.0
                    "Alimentos" -> 18.0
                    "Servicios" -> 8.0
                    "Transporte" -> 10.0
                    "Mercado" -> 49.0
                    else -> 100.0
                }
            }
        }
    }

    // Método para mostrar el mensaje de advertencia
    private fun mostrarAdvertencia(categoria: String, porcentaje: Double) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Advertencia de Gastos")
        builder.setMessage("Has gastado el $porcentaje% en $categoria, lo cual supera el límite permitido.")

        // Botón de Aceptar
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss() // Cierra el diálogo al hacer clic en "Aceptar"
        }

        // Crear y mostrar el diálogo
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



    private fun mostrarListaDeGastos(recyclerView: RecyclerView, categoria: String) {
        gastosViewModel.gastosMesCategoriaLiveData.removeObservers(viewLifecycleOwner)
        gastosViewModel.obtenerGastosMesCategoria(usuarioId, categoria)
        gastosViewModel.gastosMesCategoriaLiveData.observe(viewLifecycleOwner) { gastosCat ->
            if (gastosCat != null) {
                Log.d("mostrarListaDeGastos", "Gastos cargados: ${gastosCat.size}")
                val adapter = GastoAdapterPrincipal(gastosCat)
                adapter.setOnItemClickListener2(this) // Pasamos el Fragment como listener
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                recyclerView.visibility = if (recyclerView.visibility == View.GONE) View.VISIBLE else View.GONE
            } else {
                Log.d("mostrarListaDeGastos", "No hay gastos para la categoría: $categoria")
            }
        }
    }


    override fun onItemClick2(gasto: GastoDTO) {
        Log.d("onItemClick", "Gasto clickeado: ${gasto.nombre_gasto}")
        val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_gasto, null)
        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val items = resources.getStringArray(R.array.categorias).toList()
        val adapter = CustomSpinnerAdapter(requireContext(), items)
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

        val dialog = AlertDialog.Builder(requireContext())
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
                        Toast.makeText(requireContext(), "La cantidad ingresada no es válida", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
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

    fun cargarBarra(cantidad: Double, barra: View) {
        gastosViewModel.obtenerValorGastosMes(usuarioId)
        gastosViewModel.valorGastosMesLiveData.observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes != null) {
                gastosViewModel.obtenerDineroDisponible(usuarioId)
                gastosViewModel.dineroDisponibleLiveData.observe(viewLifecycleOwner) { disponible ->
                    if (disponible != null) {
                        val barraGris = binding.barraGrisDisponible
                        val cien = barraGris.width
                        val total = disponible + gastosMes
                        val layoutParams = barra.layoutParams as ConstraintLayout.LayoutParams
                        layoutParams.width = ((cien*cantidad)/total).toInt()
                        barra.layoutParams = layoutParams
                        barra.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    fun cargarBarraDisp(cantidad: Double, barra: View) {
        gastosViewModel.obtenerValorGastosMes(usuarioId)
        gastosViewModel.valorGastosMesLiveData.observe(viewLifecycleOwner) { gastosMes ->
            // Manejar el caso en que gastosMes sea nulo
            val gastosMesSeguro = gastosMes ?: 0.0
            val total = gastosMesSeguro + cantidad

            if (cantidad >= 0 && total > 0) { // Asegurarse de que haya algo que mostrar
                val barraGris = binding.barraGrisDisponible
                val cien = barraGris.width
                val layoutParams = barra.layoutParams as ConstraintLayout.LayoutParams

                // Ajustar el tamaño de la barra basado en la cantidad y el total
                layoutParams.width = ((cien * cantidad) / total).toInt()
                barra.layoutParams = layoutParams
                barra.visibility = View.VISIBLE
            } else {
                // Si no hay nada que mostrar, asegurarse de que la barra esté oculta
                barra.visibility = View.INVISIBLE
            }
        }
    }

    private fun cargarDona() {
        Log.d("CargarDona", "Método cargarDona() llamado")
        gastosViewModel.obtenerDineroDisponible(usuarioId)
        gastosViewModel.dineroDisponibleLiveData.observeOnce(viewLifecycleOwner) { disponible ->
            Log.d("CargarDona", "Valor de disponible: $disponible")
            gastosViewModel.obtenerValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
            gastosViewModel.valorGastosMesCategoriaLiveData
                .observeOnce(viewLifecycleOwner) { cantGastosVarios ->
                    Log.d("CargarDona", "Valor de cantGastosVarios: $cantGastosVarios")
                    gastosViewModel.obtenerValorGastosMesCategoria1(usuarioId, "Alimentos")
                    gastosViewModel.valorGastosMesCategoriaLiveData1
                        .observeOnce(viewLifecycleOwner) { cantAlimentos ->
                            Log.d("CargarDona", "Valor de cantAlimentos: $cantAlimentos")
                            gastosViewModel.obtenerValorGastosMesCategoria2(usuarioId, "Transporte")
                            gastosViewModel.valorGastosMesCategoriaLiveData2
                                .observeOnce(viewLifecycleOwner) { cantTransporte ->
                                    Log.d("CargarDona", "Valor de cantTransporte: $cantTransporte")
                                    gastosViewModel.obtenerValorGastosMesCategoria3(usuarioId, "Servicios")
                                    gastosViewModel.valorGastosMesCategoriaLiveData3
                                        .observeOnce(viewLifecycleOwner) { cantServicios ->
                                            Log.d("CargarDona", "Valor de cantServicios: $cantServicios")
                                            gastosViewModel.obtenerValorGastosMesCategoria4(usuarioId, "Mercado")
                                            gastosViewModel.valorGastosMesCategoriaLiveData4
                                                .observeOnce(viewLifecycleOwner){ cantMercado ->
                                                    Log.d("CargarDona", "Valor de cantMercado: $cantMercado")
                                            val pieChart = binding.dona1
                                            val pieData = mutableListOf<SliceValue>()
                                            if (cantAlimentos != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantAlimentos.toFloat(),
                                                        obtenerColorCategoria("Alimentos")
                                                    )
                                                )
                                            }
                                            if (cantGastosVarios != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantGastosVarios.toFloat(),
                                                        obtenerColorCategoria("Gastos Hormiga")
                                                    )
                                                )
                                            }
                                            if (cantTransporte != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantTransporte.toFloat(),
                                                        obtenerColorCategoria("Transporte")
                                                    )
                                                )
                                            }
                                            if (cantServicios != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantServicios.toFloat(),
                                                            obtenerColorCategoria("Servicios")
                                                    )
                                                )
                                            }
                                            if (cantMercado != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantMercado.toFloat(),
                                                        obtenerColorCategoria("Mercado")
                                                    )
                                                )
                                            }
                                            if (disponible != null && disponible >= 0) {
                                                pieData.add(
                                                    SliceValue(
                                                        disponible.toFloat(),
                                                        obtenerColorCategoria("disponible")
                                                    )
                                                )
                                            }
                                            val pieChartData = PieChartData(pieData)
                                            pieChartData.setHasCenterCircle(true)
                                            pieChartData.setCenterCircleScale(0.8f)
                                            pieChartData.setCenterCircleColor(Color1.WHITE)
                                            pieChart.pieChartData = pieChartData
                                        }
                                    }
                                }
                        }
                }
        }
    }

    // Función para obtener el color correspondiente a cada categoría
    fun obtenerColorCategoria(categoria: String): Int {
        val categoriasColores = mapOf(
            "disponible" to "#87EE2B",
            "Gastos Hormiga" to "#F66B6B",
            "Alimentos" to "#FF66C1",
            "Transporte" to "#339AF0",
            "Servicios" to "#EEB62B",
            "Mercado" to "#FD8435"
        )
        val color = Color1.parseColor(categoriasColores[categoria])
        return color
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this) // Elimina el observador después de la primera actualización
                observer.onChanged(value)
            }
        })
    }

}


