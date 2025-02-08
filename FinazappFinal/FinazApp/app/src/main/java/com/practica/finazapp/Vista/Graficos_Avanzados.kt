package com.practica.finazapp.Vista
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.practica.finazapp.databinding.FragmentGraficosAvanzadosBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import lecho.lib.hellocharts.view.LineChartView
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters



class Graficos_Avanzados : Fragment() {

    private var usuarioId: Long = -1
    private var _binding: FragmentGraficosAvanzadosBinding? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento utilizando el enlace de datos generado
        _binding = FragmentGraficosAvanzadosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Recuperar el ID del usuario del argumento
        usuarioId = arguments?.getLong("usuario_id", -1) ?: -1

        // Devolver la vista raíz del diseño inflado
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentGastos", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                Log.d("FragmentReporte", "id usuario: $usuarioId")
                val adelanteSem = binding.btonAdelanteSem
                val atrasSem = binding.btonAtrasSem
                val adelanteMes = binding.btonAdelanteMes
                val atrasMes = binding.btonAtrasMes
                val adelanteAn= binding.btonAdelanteAn
                val atrasAn = binding.btonAtrasAn
                var fechaActual = LocalDate.now()
                Log.d("FragmentReporte", "fecha de referencia: $fechaActual")
                listOf<String>("disponible", "Gastos hormiga", "Alimentos", "Transporte", "Servicios", "Mercado")

                adelanteSem.setOnClickListener {
                    fechaActual = fechaActual.plusWeeks(1)
                    actualizarGrafico(fechaActual, "Semanal")
                }
                atrasSem.setOnClickListener {
                    fechaActual = fechaActual.minusWeeks(1)
                    actualizarGrafico(fechaActual, "Semanal")
                }
                adelanteMes.setOnClickListener {
                    fechaActual = fechaActual.plusMonths(1)
                    actualizarGrafico(fechaActual, "Mensual")
                }
                atrasMes.setOnClickListener {
                    fechaActual = fechaActual.minusMonths(1)
                    actualizarGrafico(fechaActual, "Mensual")
                }
                adelanteAn.setOnClickListener {
                    fechaActual = fechaActual.plusYears(1)
                    actualizarGrafico(fechaActual, "Anual")
                }
                atrasAn.setOnClickListener {
                    fechaActual = fechaActual.minusYears(1)
                    actualizarGrafico(fechaActual, "Anual")
                }
                actualizarGrafico(fechaActual,"Semanal")
                actualizarGrafico(fechaActual, "Mensual")
                actualizarGrafico(fechaActual, "Anual")
            }

        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarGrafico(fechaActual: LocalDate, tiempo: String){
        Log.d("FragmentReporte", "fecha de referencia: $fechaActual, tiempo: $tiempo")
        lateinit var lineChartReporte: LineChart
        when (tiempo){
            "Semanal" ->{
                lineChartReporte = binding.graficoLineasSemanal
                val fechaInf = periodoDeTiempo(tiempo, fechaActual).first
                val fechaSup = periodoDeTiempo(tiempo, fechaActual).second
                val formato1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val fechaInfFormateada  = fechaInf.toString().format(formato1)
                val fechaSupFormateada = fechaSup.toString().format(formato1)
                val descSem = binding.descSem
                val diaInf = fechaInf.dayOfMonth
                val diaSup = fechaSup.dayOfMonth
                val mes = extraerMes(fechaSup)
                val anio = fechaActual.year
                descSem.setText("semana del $diaInf-$diaSup de $mes del $anio")
                Log.d("FragmentReporte", "rango: $fechaInfFormateada a $fechaSupFormateada")

                gastosViewModel.listarGastosPorFechas(usuarioId, fechaInfFormateada, fechaSupFormateada)
                gastosViewModel.gastosPorFechasLiveData.observe(viewLifecycleOwner) { listaSem ->
                    listaSem?.let {
                        cargarGraficoLineasSem(
                            lineChartReporte,
                            convertirMapaALista(getDatosPorCategoria(listaSem))
                        )
                        Log.d("FragmentReporte", "lista gastos: $listaSem")
                    }
                }
            }
            "Mensual" -> {
                lineChartReporte = binding.graficoLineasMensual
                val fechaInf = periodoDeTiempo(tiempo, fechaActual).first
                val fechaSup = periodoDeTiempo(tiempo, fechaActual).second
                val formato = DateTimeFormatter.ofPattern("yyyy-mm-dd")
                val fechaInfFormateada  = fechaInf.toString().format(formato)
                val fechaSupFormateada = fechaSup.toString().format(formato)
                val descSem = binding.descMes
                val mes = extraerMes(fechaActual)
                val anio = fechaActual.year
                descSem.setText("$mes de $anio")

                Log.d("FragmentReporte", "rango: $fechaInfFormateada a $fechaSupFormateada")
                gastosViewModel.listarGastosPorFechas(usuarioId, fechaInfFormateada, fechaSupFormateada)
                gastosViewModel.gastosPorFechasLiveData.observe(viewLifecycleOwner){listaMes ->
                    listaMes?.let {
                        cargarGraficoLineasMes(lineChartReporte, convertirMapaALista(getDatosPorCategoria(listaMes)))
                        Log.d("FragmentReporte", "lista gastos: $listaMes")
                    }
                }
            }
            "Anual" -> {
                lineChartReporte = binding.graficoLineasAnual
                val fechaInf = periodoDeTiempo(tiempo, fechaActual).first.toString()
                val fechaSup = periodoDeTiempo(tiempo, fechaActual).second.toString()
                val formato = DateTimeFormatter.ofPattern("yyyy-mm-dd")
                val fechaInfFormateada  = fechaInf.format(formato)
                val fechaSupFormateada = fechaSup.format(formato)
                val descSem = binding.descAn
                val anio = fechaActual.year
                descSem.setText("$anio")
                Log.d("FragmentReporte", "rango: $fechaInfFormateada a $fechaSupFormateada")

                gastosViewModel.listarGastosPorFechas(usuarioId, fechaInfFormateada, fechaSupFormateada)
                gastosViewModel.gastosPorFechasLiveData.observe(viewLifecycleOwner) { listaAn ->
                    listaAn?.let {
                        Log.d("FragmentReporte", "lista gastos: $listaAn")
                        cargarGraficoLineasAn(
                            lineChartReporte,
                            convertirMapaALista(getDatosPorCategoria(listaAn))
                        )
                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extraerMes(fecha: LocalDate): String{
        val mes = when(fecha.month){
            Month.JANUARY -> "Enero"
            Month.FEBRUARY -> "Febrero"
            Month.MARCH -> "Marzo"
            Month.APRIL -> "Abril"
            Month.MAY -> "Mayo"
            Month.JUNE -> "Junio"
            Month.JULY -> "Julio"
            Month.AUGUST -> "Agosto"
            Month.SEPTEMBER -> "Septiembre"
            Month.OCTOBER -> "Octubre"
            Month.NOVEMBER -> "Noviembre"
            Month.DECEMBER -> "Diciembre"
            null -> "null"
        }
        return mes
    }

    private fun convertirMapaALista(datosPorCategoria: Map<String, Map<String, Double>>): Map<String, List<Pair<String, Double>>> {
        val listaDatosPorCategoria = mutableMapOf<String, List<Pair<String,Double>>>()

        for ((categoria, datos) in datosPorCategoria) {
            val listaDatos = datos.toList()
            listaDatosPorCategoria.put(categoria, listaDatos)
        }
        return listaDatosPorCategoria
    }
    private fun getDatosPorCategoria(lista: List<GastoDTO>): Map<String, Map<String, Double>> {
        val datosPorCategoria: MutableMap<String, MutableMap<String, Double>> = mutableMapOf()

        for (dato in lista) {
            val categoria = dato.categoria
            val fecha = dato.fecha
            val valor = dato.valor

            // Si la categoría no existe en el mapa, crear una nueva entrada
            if (!datosPorCategoria.containsKey(categoria)) {
                datosPorCategoria[categoria] = mutableMapOf(fecha to valor)
            } else {
                // Si la categoría existe, verificar si la fecha ya existe
                if (datosPorCategoria[categoria]?.containsKey(fecha) == true) {
                    // Si la fecha ya existe, sumar el valor al valor existente
                    val valorExistente = datosPorCategoria[categoria]?.get(fecha) ?: 0.0
                    datosPorCategoria[categoria]?.put(fecha, valorExistente + valor)
                } else {
                    // Si la fecha no existe, agregar una nueva entrada
                    datosPorCategoria[categoria]?.put(fecha, valor)
                }
            }
        }

        return datosPorCategoria
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun periodoDeTiempo(tiempo: String, fechaActual: LocalDate): Pair<LocalDate, LocalDate> {
        return when (tiempo) {
            "Semanal" -> {
                val inicioSemana = fechaActual.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val finSemana = fechaActual.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                Pair(inicioSemana, finSemana)
            }
            "Mensual" -> {
                val inicioMes = fechaActual.with(TemporalAdjusters.firstDayOfMonth())
                val finMes = fechaActual.with(TemporalAdjusters.lastDayOfMonth())
                Pair(inicioMes, finMes)
            }
            "Anual" -> {
                val inicioAnio = fechaActual.with(TemporalAdjusters.firstDayOfYear())
                val finAnio = fechaActual.with(TemporalAdjusters.lastDayOfYear())
                Pair(inicioAnio, finAnio)
            }
            else -> throw IllegalArgumentException("Tipo de rango de fechas no válido")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarGraficoLineasSem(chartView: LineChart, datosPorCategoria: Map<String, List<Pair<String, Double>>>) {
        val lineDataSetList = mutableListOf<LineDataSet>()

        for ((categoria, datos) in datosPorCategoria) {
            val entries = datos.map { (fecha, valor) ->
                Entry(diaSemana(fecha), (valor / 1000).toFloat())
            }

            val dataSet = LineDataSet(entries, categoria).apply {
                color = getColorCategoria(categoria)
                valueTextSize = 10f
                lineWidth = 2f
                setDrawCircles(true)
                circleRadius = 4f
                setDrawValues(false)
                setDrawFilled(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER // Líneas suaves
            }
            lineDataSetList.add(dataSet)
        }

        val lineData = LineData(lineDataSetList as List<ILineDataSet>?)
        chartView.data = lineData

        // Configurar el eje X
        chartView.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        1 -> "Lun"
                        2 -> "Mar"
                        3 -> "Mier"
                        4 -> "Jue"
                        5 -> "Vier"
                        6 -> "Sab"
                        7 -> "Dom"
                        else -> "?"
                    }
                }
            }
        }

        // Configurar el eje Y
        chartView.axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
        }
        chartView.axisRight.isEnabled = false
        chartView.description.isEnabled = false
        chartView.invalidate() // Refrescar el gráfico
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun diaSemana(fecha: String): Float {
        val diaSem = LocalDate.parse(fecha).dayOfWeek.value
        Log.d("FragmentReporte", "fecha: $fecha, dia de la semana: $diaSem")
        return diaSem.toFloat()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarGraficoLineasMes(chartView: LineChart, datosPorCategoria: Map<String, List<Pair<String, Double>>>) {
        val lineDataSetList = mutableListOf<LineDataSet>()

        for ((categoria, datos) in datosPorCategoria) {
            val entries = organizar(datos).map { (fecha, valor) ->
                Entry(diaMes(fecha), (valor / 1000).toFloat())
            }

            val dataSet = LineDataSet(entries, categoria).apply {
                color = getColorCategoria(categoria)
                valueTextSize = 10f
                lineWidth = 2f
                setDrawCircles(true)
                circleRadius = 4f
                setDrawValues(false)
                setDrawFilled(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER // Líneas suaves
            }
            lineDataSetList.add(dataSet)
        }

        val lineData = LineData(lineDataSetList as List<ILineDataSet>?)
        chartView.data = lineData

        // Configurar el eje X
        chartView.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        // Configurar el eje Y
        chartView.axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
        }
        chartView.axisRight.isEnabled = false
        chartView.description.isEnabled = false
        chartView.invalidate() // Refrescar el gráfico
    }

    fun diaMes(fecha: String): Float {
        val partes = fecha.split("-")
        val dia = partes[2]
        return dia.toFloat()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarGraficoLineasAn(chartView: LineChart, datosPorCategoria: Map<String, List<Pair<String, Double>>>) {
        val lineDataSetList = mutableListOf<LineDataSet>()

        for ((categoria, datos) in datosPorCategoria) {
            val entries = generarPuntosAn(organizar(datos))
            val lineDataSet = LineDataSet(entries, categoria).apply {
                color = getColorCategoria(categoria)
                valueTextSize = 10f
                setCircleColor(getColorCategoria(categoria))
                circleRadius = 3f
                lineWidth = 2f
                setDrawValues(false)
            }
            lineDataSetList.add(lineDataSet)
        }

        val lineData = LineData(lineDataSetList as List<ILineDataSet>?)
        chartView.data = lineData
        chartView.description.isEnabled = false
        chartView.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 30f
            valueFormatter = IndexAxisValueFormatter(arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"))
        }
        chartView.axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
        }
        chartView.axisRight.isEnabled = false
        chartView.invalidate()

        val noData = binding.noDataAn
        if (chartView.data.entryCount == 0) {
            noData.visibility = View.VISIBLE
            chartView.visibility = View.INVISIBLE
        } else {
            noData.visibility = View.GONE
            chartView.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generarPuntosAn(datos: List<Pair<String, Double>>): List<Entry> {
        return datos.map { (fecha, valor) ->
            Entry(diaAnio(fecha), (valor / 1000).toFloat())
        }
    }

    fun diaAnio(fecha: String): Float {
        val partes = fecha.split("-")
        val meses = partes[1].toInt()
        val dias = partes[2].toInt()
        var diaDelAnio = dias + when (meses) {
            2 -> 31
            3 -> 59
            4 -> 90
            5 -> 120
            6 -> 151
            7 -> 181
            8 -> 212
            9 -> 243
            10 -> 273
            11 -> 304
            12 -> 334
            else -> 0
        }
        return diaDelAnio.toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun organizar(lista: List<Pair<String, Double>>): List<Pair<String, Double>> {
        return lista.sortedBy { LocalDate.parse(it.first, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
    }

    fun getColorCategoria(categoria: String): Int {
        val categoriasColores = mapOf(
            "disponible" to Color.parseColor("#87EE2B"),
            "Gastos Hormiga" to Color.parseColor("#F66B6B"),
            "Alimentos" to Color.parseColor("#FF66C1"),
            "Transporte" to Color.parseColor("#339AF0"),
            "Servicios" to Color.parseColor("#EEB62B"),
            "Mercado" to Color.parseColor("#FD8435")
        )
        return categoriasColores[categoria] ?: Color.BLACK
    }

    fun isChartEmpty(lineChartView: LineChartView): Boolean {
        val lineChartData = lineChartView.lineChartData
        if (lineChartData != null) {
            val lines = lineChartData.lines
            if (lines != null && lines.isNotEmpty()) {
                if(lines.size > 1) {
                    for (line in lines) {
                        if (line.values.size > 1) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}






