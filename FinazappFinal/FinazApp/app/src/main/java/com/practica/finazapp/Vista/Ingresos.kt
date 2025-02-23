package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.databinding.FragmentIngresosBinding
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.IngresoDTO
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.LocalDate.now

class Ingresos : Fragment(), IngresosListener {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IncomeViewModel
    private var ingresosMensuales: MutableList<IngresoDTO> = mutableListOf()
    private var ingresosCasuales: MutableList<IngresoDTO> = mutableListOf()
    private var totalIngresos :Double = 0.00
    private var _binding: FragmentIngresosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngresosBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ingresoViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentIngresos", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                cargarIngresos()
            }
        }

        ingresoViewModel.operacionCompletada.observe(viewLifecycleOwner) { completada ->
            if (completada == true) {
                cargarIngresos() // Actualizar la UI
            }
        }

        val btnNuevoIngresoMes = binding.btnNuevoIngresoMes
        val btnNuevoIngresoCas = binding.btnNuevoIngresoCas

        btnNuevoIngresoCas.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_ingreso_cas, null)
            val editTextCantidad = dialogView.findViewById<TextInputEditText>(R.id.editTextCantidad)
            val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
            val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)

            editTextFecha.setOnClickListener {
                showDatePickerDialog(editTextFecha)
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create()

            dialog.show()

            // Sobrescribir el comportamiento del botón "Guardar"
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val cantidad = editTextCantidad.text.toString()
                val fechaOriginal = editTextFecha.text.toString()
                val descripcion = editTextDescripcion.text.toString()

                if (cantidad.isBlank() || fechaOriginal.isBlank() || descripcion.isBlank()) {
                    // Mostrar alerta sin cerrar el diálogo principal
                    AlertDialog.Builder(requireContext())
                        .setTitle("Campos vacíos")
                        .setMessage("Por favor, llene todos los campos antes de guardar.")
                        .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                        .create()
                        .show()
                    return@setOnClickListener
                }

                val cantidadValor = cantidad.toDoubleOrNull()
                if (cantidadValor == null) {
                    // Mostrar alerta si la cantidad no es válida
                    AlertDialog.Builder(requireContext())
                        .setTitle("Cantidad inválida")
                        .setMessage("Ingrese una cantidad numérica válida.")
                        .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                        .create()
                        .show()
                    return@setOnClickListener
                }

                // Formatear la fecha
                val parts = fechaOriginal.split("/")
                val dia = parts[0].padStart(2, '0')
                val mes = parts[1].padStart(2, '0')
                val anio = parts[2]
                val fecha = "${anio}-${mes}-${dia}"

                // Crear el nuevo ingreso
                val nuevoIngreso = IngresoDTO(
                    id_ingreso = 0,
                    nombre_ingreso = descripcion,
                    valor = cantidadValor,
                    fecha = fecha,
                    tipo_ingreso = "casual"
                )

                // Registrar el ingreso
                ingresoViewModel.registrarIngreso(usuarioId, nuevoIngreso)
                dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
            }
        }


        btnNuevoIngresoMes.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_ingreso_mes, null)
            val editTextCantidad = dialogView.findViewById<TextInputEditText>(R.id.editTextCantidad)
            val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
            val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)

            editTextFecha.setOnClickListener {
                showDatePickerDialog(editTextFecha)
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create()

            dialog.show()

            // Sobrescribir el comportamiento del botón "Guardar"
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val cantidadText = editTextCantidad.text.toString()
                val fechaOriginal = editTextFecha.text.toString()
                val descripcion = editTextDescripcion.text.toString()

                // Validación de campos vacíos
                if (cantidadText.isBlank() || fechaOriginal.isBlank() || descripcion.isBlank()) {
                    // Mostrar alerta sin cerrar el diálogo principal
                    AlertDialog.Builder(requireContext())
                        .setTitle("Campos vacíos")
                        .setMessage("Por favor, llene todos los campos antes de guardar.")
                        .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                        .create()
                        .show()
                    return@setOnClickListener
                }

                val cantidad = cantidadText.toDoubleOrNull()
                if (cantidad == null) {
                    // Mostrar alerta si la cantidad no es válida
                    AlertDialog.Builder(requireContext())
                        .setTitle("Cantidad inválida")
                        .setMessage("Ingrese una cantidad numérica válida.")
                        .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                        .create()
                        .show()
                    return@setOnClickListener
                }

                // Formatear la fecha
                val parts = fechaOriginal.split("/")
                val dia = parts[0].padStart(2, '0')
                val mes = parts[1].padStart(2, '0')
                val anio = parts[2]
                val fecha = "${anio}-${mes}-${dia}"

                // Crear el nuevo ingreso
                val nuevoIngreso = IngresoDTO(
                    id_ingreso = 0,
                    nombre_ingreso = descripcion,
                    valor = cantidad,
                    fecha = fecha,
                    tipo_ingreso = "mensual"
                )

                // Registrar el ingreso
                ingresoViewModel.registrarIngreso(usuarioId, nuevoIngreso)
                dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarIngresos() {

        ingresoViewModel.listarIngresosCasualesPorAnio(usuarioId)
        ingresoViewModel.listarIngresosMensuales(usuarioId)
        ingresoViewModel.obtenerTotalIngresos(usuarioId)

        ingresoViewModel.ingresosCasualesLiveData.observe(viewLifecycleOwner) { ingCasuales ->
            ingCasuales?.let {
                ingresosCasuales = it.toMutableList()
                Log.d("FragmentIngresos", "Ingresos Casuales: $ingresosCasuales")
                checkDataLoaded()
            } ?: run {
                Log.e("FragmentIngresos", "No se recibieron ingresos casuales")
            }
        }

        ingresoViewModel.ingresosMensualesLiveData.observe(viewLifecycleOwner) { ingresos ->
            ingresos?.let {
                ingresosMensuales = it.toMutableList()
                verificarIngresosMensuales(ingresosMensuales)
                Log.d("FragmentIngresos", "Ingresos Mensuales: $ingresosMensuales")
                checkDataLoaded()
            } ?: run {
                Log.e("FragmentIngresos", "No se recibieron ingresos mensuales")
            }
        }
        // Observa el total de ingresos
        ingresoViewModel.totalIngresosLiveData.observe(viewLifecycleOwner) { ingTotal ->
            totalIngresos = ingTotal ?: 0.0
            Log.d("FragmentIngresos", "Total Ingresos: $totalIngresos")
            checkDataLoaded()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDataLoaded() {
        Log.d("FragmentIngresos", "Check datos cargados")
        if ((ingresosMensuales.isNotEmpty() || ingresosCasuales.isNotEmpty()) && totalIngresos != 0.00) {
            Log.d("FragmentIngresos", "datos cargados")
            onIngresosCargados(totalIngresos, ingresosMensuales, ingresosCasuales)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onIngresosCargados(totalIngresos: Double, ingresosMensuales: List<IngresoDTO>, ingresosCasuales: List<IngresoDTO>) {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2
        binding.txtBlanco.text = "${numberFormat.format(totalIngresos)}$"

        val contenedorIngresosMes = binding.contenedorIngresosMes
        val contenedorIngresosCas = binding.contenedorIngresosCas

        Log.d("FragmentIngresos", "ingresos cargados")
        cargarIngresos(ingresosMensuales, contenedorIngresosMes)
        cargarIngresos(ingresosCasuales, contenedorIngresosCas)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun cargarIngresos(ingresos: List<IngresoDTO>, contenedor: ViewGroup) {
        Log.d("FragmentIngresos", "cargando contenedor ${contenedor.id}")
        // Limpiar el contenedor antes de cargar los nuevos ingresos
        contenedor.removeAllViews()
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        for (ingreso in ingresos) {
            val descripcionTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = ingreso.nombre_ingreso
                setTextAppearance(R.style.TxtNegroMedianoItalic)
            }

            val valorTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = "${numberFormat.format(ingreso.valor)}$"
                setTextAppearance(R.style.TxtNegroMedianoItalic)
            }

            val registroLayout = ConstraintLayout(requireContext()).apply {
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                addView(descripcionTextView)
                addView(valorTextView)

                val descParams = descripcionTextView.layoutParams as ConstraintLayout.LayoutParams
                descParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                descParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                val valorParams = valorTextView.layoutParams as ConstraintLayout.LayoutParams
                valorParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                valorParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                // Agregar OnClickListener para abrir el diálogo de modificación
                setOnClickListener {
                    val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_ingreso, null)
                    val textViewTitulo = dialogView.findViewById<TextView>(R.id.titulo)
                    val editTextCantidad = dialogView.findViewById<TextInputEditText>(R.id.editTextCantidad)
                    val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
                    val btnEliminarIngreso = dialogView.findViewById<Button>(R.id.btnEliminarIngreso)

                    val dialogModificarIngreso = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton("Aceptar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
                        .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        .create()

                    textViewTitulo.setText("Modificar ingreso ${ingreso.nombre_ingreso}")

                    val fecha = ingreso.fecha
                    val parts = fecha.split("-")
                    val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"

                    editTextCantidad.setText(ingreso.valor.toString())
                    editTextFecha.setText(fechaFormateada)  // Mostrar la fecha en formato dd/MM/yyyy
                    editTextFecha.setOnClickListener {
                        showDatePickerDialog(editTextFecha)
                    }

                    btnEliminarIngreso.setOnClickListener {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                ingresoViewModel.eliminarIngreso(ingreso.id_ingreso)
                            }
                        }
                        dialogModificarIngreso.dismiss()
                    }

                    dialogModificarIngreso.show()

                    // Sobrescribir el comportamiento del botón "Aceptar"
                    dialogModificarIngreso.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val cantidad = editTextCantidad.text.toString()
                        val fecha = editTextFecha.text.toString()

                        if (cantidad.isBlank() || fecha.isBlank()) {
                            // Mostrar alerta sin cerrar el diálogo principal
                            AlertDialog.Builder(requireContext())
                                .setTitle("Campos vacíos")
                                .setMessage("Por favor, llene todos los campos antes de guardar.")
                                .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                                .create()
                                .show()
                            return@setOnClickListener
                        }

                        val cantidadValor = cantidad.toDoubleOrNull()
                        if (cantidadValor == null) {
                            // Mostrar alerta si la cantidad no es válida
                            AlertDialog.Builder(requireContext())
                                .setTitle("Cantidad inválida")
                                .setMessage("Ingrese una cantidad numérica válida.")
                                .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                                .create()
                                .show()
                            return@setOnClickListener
                        }

                        // Formatear la fecha
                        val partsFecha = fecha.split("/")
                        val dia = partsFecha[0].padStart(2, '0')
                        val mes = partsFecha[1].padStart(2, '0')
                        val anio = partsFecha[2]
                        val fechaFormateadaModificada = "${anio}-${mes}-${dia}"

                        // Crear el nuevo ingreso modificado
                        val ingresoModificado = ingreso.copy(
                            valor = cantidadValor,
                            fecha = fechaFormateadaModificada
                        )

                        // Modificar el ingreso
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                ingresoViewModel.modificarIngreso(idIngreso = ingreso.id_ingreso, ingresoDTO = ingresoModificado)
                            }
                        }
                        dialogModificarIngreso.dismiss() // Cerrar el diálogo principal después del guardado exitoso
                    }
                }
            }

            contenedor.addView(registroLayout)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun verificarIngresosMensuales(ingresosMesActual: List<IngresoDTO>) {
        val currentDate = now()
        val lastMonthDate = currentDate.minusMonths(1)

        val yearString = lastMonthDate.year.toInt()
        val monthString = lastMonthDate.monthValue

        val descripcionesActuales = ingresosMesActual.map { it.nombre_ingreso }.toSet()
        Log.d("FragmentIngresos", "Descripciones actuales: $descripcionesActuales")
        var isObserving = false
        // Llamada para obtener los ingresos mensuales del mes pasado
        ingresoViewModel.getIngresosMensuales(usuarioId, yearString, monthString)
        // Configurar la observación de los ingresos mensuales.
        ingresoViewModel.ingresosLiveData.observe(viewLifecycleOwner) { ingresos ->
            Log.d("FragmentIngresos", "Flujo de datos activado")

            if (!isObserving) {
                Log.d("FragmentIngresos", "Flujo de datos activado")
                for (ingreso in ingresos!!) {
                    // Verificar si la descripción del ingreso mensual del mes pasado ya está presente en el conjunto de descripciones actuales
                    if (!descripcionesActuales.contains(ingreso.nombre_ingreso)) {
                        Log.d(
                            "FragmentIngresos",
                            "creando ingreso mensual ${ingreso.nombre_ingreso} mes $monthString/$yearString"
                        )
                        val parts = ingreso.fecha.split("-")
                        val year = parts[0].toInt()
                        val day = parts[2]
                        val month = currentDate.monthValue.toString().padStart(2, '0')
                        val fechaNueva = "$year-$month-$day"
                        val nuevoIngreso = IngresoDTO(
                            id_ingreso = 0,
                            nombre_ingreso = ingreso.nombre_ingreso,
                            valor = ingreso.valor,
                            fecha = fechaNueva,
                            tipo_ingreso = ingreso.tipo_ingreso
                        )
                        ingresoViewModel.registrarIngreso(usuarioId, nuevoIngreso)
                    }
                }

                isObserving = true
            }
        }
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
                val fechaSeleccionada = "$dayOfMonth1/${String.format("%02d", monthOfYear + 1)}/$year1"
                editTextFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar la referencia al enlace de datos para evitar fugas de memoria
        _binding = null
    }

}
