package com.practica.finazapp.Vista

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.Notificaciones.NotificationHelper
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.AlertViewModel
import com.practica.finazapp.ViewModelsApiRest.DepositoViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import com.practica.finazapp.databinding.FragmentDashboardBinding
import com.practica.finazapp.ui.Estilos.CustomSpinnerAdapter
import lecho.lib.hellocharts.model.*
import java.text.NumberFormat
import android.graphics.Color as Color1


class DashboardFragment : Fragment() {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var ingresoViewModel: IncomeViewModel
    private lateinit var alertaViewModel: AlertViewModel
    private lateinit var depositoViewModel: DepositoViewModel
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var sharedPreferences: SharedPreferences
    private val notificacionesEnviadas = mutableSetOf<Long>()
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

        val lottieCampana = view.findViewById<LottieAnimationView>(R.id.lottieCampana)

        lottieCampana.setOnClickListener {
                Recordatorios()
        }

        // Registrar un OnBackPressedCallback
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Cierra la aplicación
                requireActivity().finishAffinity()
            }
        }

        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        ingresoViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        alertaViewModel = ViewModelProvider(this)[AlertViewModel::class.java]
        depositoViewModel = ViewModelProvider(this)[DepositoViewModel::class.java]


        notificationHelper = NotificationHelper(requireContext())

        solicitarPermisoNotificaciones()

        // Inicializar SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("notificaciones", Context.MODE_PRIVATE)

        // Cargar los IDs ya notificados
        val ids = sharedPreferences.getStringSet("alertas_notificadas", emptySet()) ?: emptySet()
        notificacionesEnviadas.addAll(ids.mapNotNull { it.toLongOrNull() })

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentGastos", "id usuario: $usuarioId")

            usuarioId?.let {
                Log.d("FragmentGastos", "Usuario ID no es nulo: $it")
                this.usuarioId = it

                try {
                    cargarDatos()
                    verificarAlertasExcedidas()
                    Log.d("FragmentGastos", "Datos cargados correctamente")
                } catch (e: Exception) {
                    Log.e("FragmentGastos", "Error al cargar datos: ${e.message}", e)
                }

                val bloqueTransporte = binding.bloqueTransporte
                val bloqueGastosVarios = binding.bloqueGastosVarios
                val bloqueMercado = binding.bloqueMercado
                val bloqueServicios = binding.bloqueServicios
                val bloqueAlimentos = binding.bloqueAlimentos
                val bloqueDeudas = binding.bloqueDeudas
                val recyclrerViewTransporte = binding.recyclerViewTransporte
                val recyclrerViewGastosVarios = binding.recyclerViewGastosVarios
                val recyclrerViewMercado = binding.recyclerViewMercado
                val recyclrerViewServicios = binding.recyclerViewServicios
                val recyclrerViewAlimentos = binding.recyclerViewAlimentos
                val recyclrerViewDeudas = binding.recyclerViewDeudas

                Log.d("FragmentGastos", "Configurando listeners para los bloques de gastos")

                bloqueTransporte.setOnClickListener {
                    Log.d("FragmentGastos", "Clic en bloque Transporte")
                    mostrarListaDeGastos(recyclrerViewTransporte, "Transporte")
                }

                bloqueMercado.setOnClickListener {
                    Log.d("FragmentGastos", "Clic en bloque Mercado")
                    mostrarListaDeGastos(recyclrerViewMercado, "Mercado")
                }

                bloqueServicios.setOnClickListener {
                    Log.d("FragmentGastos", "Clic en bloque Servicios")
                    mostrarListaDeGastos(recyclrerViewServicios, "Servicios")
                }

                bloqueAlimentos.setOnClickListener {
                    Log.d("FragmentGastos", "Clic en bloque Alimentos")
                    mostrarListaDeGastos(recyclrerViewAlimentos, "Alimentos")
                }

                bloqueGastosVarios.setOnClickListener {
                    Log.d("FragmentGastos", "Clic en bloque Gastos Varios")
                    mostrarListaDeGastos(recyclrerViewGastosVarios, "Gastos Hormiga")
                }

                bloqueDeudas.setOnClickListener {
                    Log.d("FragmentGastos", "Clic en bloque Deudas")
                    mostrarListaDeGastos(recyclrerViewDeudas, "Deudas")
                }

            } ?: run {
                Log.e("FragmentGastos", "El ID de usuario es nulo")
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
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Aviso")
                        .setMessage("Debe poner ingresos antes de registrar un gasto.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                } else {
                    val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_gasto, null)
                    val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
                    val items = resources.getStringArray(R.array.categorias).toList()
                    val adapter = CustomSpinnerAdapter(requireContext(), items)
                    spinnerCategoria.adapter = adapter
                    val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
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
                        val categoria = spinnerCategoria.selectedItem.toString()
                        val cantidad = editTextCantidad.text.toString()
                        val fechaOriginal = editTextFecha.text.toString()
                        val descripcion = editTextDescripcion.text.toString()

                        if (categoria.isBlank() || cantidad.isBlank() || fechaOriginal.isBlank() || descripcion.isBlank()) {
                            // Mostrar alerta sin cerrar el diálogo principal
                            AlertDialog.Builder(requireContext())
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
                                val nuevoGasto = GastoDTO(
                                    id_gasto = 0,
                                    categoria = categoria,
                                    fecha = fecha,
                                    valor = valor,
                                    nombre_gasto = descripcion
                                )

                                gastosViewModel.registrarGasto(usuarioId, nuevoGasto)
                                verificarAlertasExcedidas()
                                dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
                            } catch (e: NumberFormatException) {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Cantidad inválida")
                                    .setMessage("Ingrese una cantidad numérica válida.")
                                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                                    .create()
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }




    private fun verificarAlertasExcedidas() {
        Log.d("DashboardFragment", "Llamando al método verificarAlertasExcedidas...")

        alertaViewModel.obtenerAlertaPorMes(usuarioId)

        alertaViewModel.alertasPorMesLiveData.observe(viewLifecycleOwner) { alertas ->
            Log.d("DashboardFragment", "Alertas: $alertas")

            if (alertas.isNullOrEmpty()) {
                Log.d("DashboardFragment", "No hay alertas configuradas para este mes.")
                return@observe
            }

            val categoriasSpinner = listOf("disponible", "Gastos Hormiga", "Alimentos", "Transporte", "Servicios", "Mercado")

            for (alerta in alertas) {
                Log.d("DashboardFragment", "Procesando alerta: ${alerta.nombre}")

                // Verificar si la alerta está en el Spinner
                if (!categoriasSpinner.contains(alerta.descripcion)) continue

                Log.d("DashboardFragment", "Alerta válida en Spinner: ${alerta.descripcion}")

                if (alerta.descripcion == "disponible") {
                    Log.d("DashboardFragment", "Obteniendo gasto total")

                    gastosViewModel.obtenerValorGastosMes(usuarioId)
                    gastosViewModel.valorGastosMesLiveData.observeOnce(viewLifecycleOwner) { totalGastos ->
                        if (totalGastos != null && totalGastos > alerta.valor) {
                            Log.d("DashboardFragment", "Gasto total excede la alerta (${alerta.valor}): $totalGastos")

                            if (!notificacionesEnviadas.contains(alerta.id_alerta)) {
                                val mensaje = "La alerta '${alerta.nombre}' para el gasto disponible ha sido excedida. Límite: ${alerta.valor}, Gasto total: $totalGastos"
                                notificationHelper.sendNotification("Gasto Excedido", mensaje)
                                notificacionesEnviadas.add(alerta.id_alerta)
                                Log.d("DashboardFragment", "Notificación enviada para alerta: ${alerta.nombre}")
                            }
                        }
                    }
                } else {
                    Log.d("DashboardFragment", "Obteniendo gasto por categoría: ${alerta.descripcion}")
                    gastosViewModel.obtenerValorGastosMesCategoria5(usuarioId, alerta.descripcion)
                    gastosViewModel.valorGastosMesCategoriaLiveData5.observe(viewLifecycleOwner) { gastoCategoria ->
                        Log.d("DashboardFragment", "Gasto por categoría: $gastoCategoria")
                        if (gastoCategoria != null && gastoCategoria > alerta.valor) {
                            Log.d("DashboardFragment", "Gasto en categoría '${alerta.descripcion}' excede la alerta (${alerta.valor}): $gastoCategoria")

                            if (!notificacionesEnviadas.contains(alerta.id_alerta)) {
                                val mensaje = "La alerta '${alerta.nombre}' para la categoría '${alerta.descripcion}' ha sido excedida. Límite: ${alerta.valor}, Gasto: $gastoCategoria"
                                notificationHelper.sendNotification("Gasto Excedido", mensaje)
                                notificacionesEnviadas.add(alerta.id_alerta)
                                Log.d("DashboardFragment", "Notificación enviada para alerta: ${alerta.nombre}")
                            }
                        }
                    }
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
        gastosViewModel.dineroDisponibleLiveData.observe(viewLifecycleOwner) { disp ->
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
            .observe(viewLifecycleOwner) { cantidad ->
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
            .observe(viewLifecycleOwner) { cantidad ->
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
            .observe(viewLifecycleOwner) { cantidad ->
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
            .observe(viewLifecycleOwner) { cantidad ->
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
            .observe(viewLifecycleOwner) { cantidad ->
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

        gastosViewModel.obtenerValorGastosMesCategoriaDeudas(usuarioId, "Deudas")
        gastosViewModel.valorGastosMesCategoriaLiveDataDeudas
            .observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val MercadoTextView = binding.cantidadDeudas
                    MercadoTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraMercado = binding.barraDeudas
                    cargarBarra(cantidadCategoria, barraMercado)
                }
            }


        gastosViewModel.obtenerValorGastosMes(usuarioId)
        depositoViewModel.obtenerValorGastosMesDeposito(usuarioId)

        // Usar MediatorLiveData para observar ambos valores al mismo tiempo
        val combinedLiveData = MediatorLiveData<Pair<Double?, Double?>>().apply {
            addSource(gastosViewModel.valorGastosMesLiveData) { gastosMes ->
                value = Pair(gastosMes, depositoViewModel.valorGastosMesDepositoLiveData.value)
            }
            addSource(depositoViewModel.valorGastosMesDepositoLiveData) { valorDeposito ->
                value = Pair(gastosViewModel.valorGastosMesLiveData.value, valorDeposito)
            }
        }

        combinedLiveData.observe(viewLifecycleOwner) { (gastosMes, valorDeposito) ->
            // Si ambos valores son nulos, los reemplazamos por 0.0
            val totalGastos = (gastosMes ?: 0.0) + (valorDeposito ?: 0.0)

            Log.d("DashboardComprobacion", "Total Gastado: $totalGastos")

            val numberFormat = NumberFormat.getInstance().apply {
                maximumFractionDigits = 2
            }
            binding.TxtGastoTotal.text = "${numberFormat.format(totalGastos)}$"

            // Se carga la dona sin importar si hay valores o no
            cargarDona()
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

        gastosViewModel.obtenerValorGastosMesCategoriaDeudas(usuarioId, "Deudas")
        gastosViewModel.valorGastosMesCategoriaLiveDataDeudas
            .observeOnce(viewLifecycleOwner) { cantidadDeuda ->
                cantidadDeuda?.let {
                    val porcentajeMercado = (cantidadDeuda / ingresoMensual) * 100
                    if (porcentajeMercado > obtenerLimitePorCategoria("Deudas", ingresoMensual)) {
                        mostrarAdvertencia("Deudas", porcentajeMercado)
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
        val intent = Intent(requireContext(), ListaGastos::class.java).apply {
            putExtra("usuarioId", usuarioId)
            putExtra("categoria", categoria)
        }
        startActivity(intent)
    }

    private fun Recordatorios() {

        val intent = Intent(requireContext(), Recordatorios_Usuario::class.java).apply {
            putExtra("usuarioId", usuarioId)
        }
        startActivity(intent)
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
        gastosViewModel.valorGastosMesLiveData.observe(viewLifecycleOwner) {gastosMes ->

            // Manejar el caso en que gastosMes sea nulo
            val gastosMesSeguro = gastosMes ?: 0.0

            val total = gastosMesSeguro + cantidad

            if (cantidad >= 0 && total > 0) { // Asegurarse de que haya algo que mostrar
                val barraGris = binding.barraGrisDisponible
                val cien = barraGris.width

                if (cien > 0) {
                    val layoutParams = barra.layoutParams as ConstraintLayout.LayoutParams

                    // Calcular el ancho de la barra proporcionalmente
                    val nuevoAncho = ((cien * cantidad) / total).toInt()

                    layoutParams.width = nuevoAncho
                    barra.layoutParams = layoutParams
                    barra.visibility = View.VISIBLE
                } else {
                    Log.w("cargarBarraDisp", "Ancho de barraGrisDisponible es 0. La barra no se actualizará.")
                }
            } else {
                // Si no hay nada que mostrar, asegurarse de que la barra esté oculta
                barra.visibility = View.INVISIBLE
            }
        }
    }


    // Variables globales para almacenar los valores a medida que se obtienen
    private var disponible1: Float = 0f
    private var gastosHormiga: Float = 0f
    private var alimentos: Float = 0f
    private var transporte: Float = 0f
    private var servicios: Float = 0f
    private var mercado: Float = 0f
    private var deudas: Float = 0f
    private var deposito: Float = 0f

    private fun cargarDona() {
        Log.d("CargarDona", "Método cargarDona() llamado")

        gastosViewModel.obtenerDineroDisponible(usuarioId)
        gastosViewModel.dineroDisponibleLiveData.observe(viewLifecycleOwner) { valor ->
            disponible1 = (valor ?: 0f).toFloat()
            actualizarGrafico() // Actualiza el gráfico con el nuevo dato
        }

        gastosViewModel.obtenerValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
        gastosViewModel.valorGastosMesCategoriaLiveData.observe(viewLifecycleOwner) { valor ->
            gastosHormiga = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }

        gastosViewModel.obtenerValorGastosMesCategoria1(usuarioId, "Alimentos")
        gastosViewModel.valorGastosMesCategoriaLiveData1.observe(viewLifecycleOwner) { valor ->
            alimentos = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }

        gastosViewModel.obtenerValorGastosMesCategoria2(usuarioId, "Transporte")
        gastosViewModel.valorGastosMesCategoriaLiveData2.observe(viewLifecycleOwner) { valor ->
            transporte = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }

        gastosViewModel.obtenerValorGastosMesCategoria3(usuarioId, "Servicios")
        gastosViewModel.valorGastosMesCategoriaLiveData3.observe(viewLifecycleOwner) { valor ->
            servicios = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }

        gastosViewModel.obtenerValorGastosMesCategoria4(usuarioId, "Mercado")
        gastosViewModel.valorGastosMesCategoriaLiveData4.observe(viewLifecycleOwner) { valor ->
            mercado = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }

        gastosViewModel.obtenerValorGastosMesCategoriaDeudas(usuarioId, "Deudas")
        gastosViewModel.valorGastosMesCategoriaLiveDataDeudas.observe(viewLifecycleOwner) { valor ->
            deudas = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }


        depositoViewModel.obtenerValorGastosMesDeposito(usuarioId)
        depositoViewModel.valorGastosMesDepositoLiveData.observe(viewLifecycleOwner) { valor ->
            deposito = (valor ?: 0f).toFloat()
            actualizarGrafico()
        }

    }

    // Función que se llama cada vez que un dato se actualiza
    private fun actualizarGrafico() {
        val pieChart = binding.dona1
        val pieData = mutableListOf<SliceValue>()

        if (alimentos > 0) pieData.add(SliceValue(alimentos, obtenerColorCategoria("Alimentos")))
        if (gastosHormiga > 0) pieData.add(SliceValue(gastosHormiga, obtenerColorCategoria("Gastos Hormiga")))
        if (transporte > 0) pieData.add(SliceValue(transporte, obtenerColorCategoria("Transporte")))
        if (servicios > 0) pieData.add(SliceValue(servicios, obtenerColorCategoria("Servicios")))
        if (mercado > 0) pieData.add(SliceValue(mercado, obtenerColorCategoria("Mercado")))
        if (deudas > 0) pieData.add(SliceValue(deudas, obtenerColorCategoria("Deudas")))
        if (disponible1 > 0) pieData.add(SliceValue(disponible1, obtenerColorCategoria("disponible")))
        if (deposito > 0) pieData.add(SliceValue(deposito, obtenerColorCategoria("Deposito")))

        // Si al menos hay un dato válido, se actualiza el gráfico
        if (pieData.isNotEmpty()) {
            val pieChartData = PieChartData(pieData)
            pieChartData.setHasCenterCircle(true)
            pieChartData.setCenterCircleScale(0.8f)
            pieChartData.setCenterCircleColor(Color1.WHITE)
            pieChart.pieChartData = pieChartData

            Log.d("CargarDona", "Gráfico de dona dibujado con datos: $pieData")
        } else {
            Log.d("CargarDona", "No hay datos suficientes para dibujar el gráfico.")
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
            "Mercado" to "#FD8435",
            "Deudas" to "#00B8B3",
            "Deposito" to "#001A41"
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


