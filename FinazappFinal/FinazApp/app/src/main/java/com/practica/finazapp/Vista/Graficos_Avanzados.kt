package com.practica.finazapp.Vista
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.databinding.FragmentGraficosAvanzadosBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class Graficos_Avanzados : Fragment() {
    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var ingresoViewModel: IncomeViewModel
    private var disponible: Double = 0.0
    private var _binding: FragmentGraficosAvanzadosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraficosAvanzadosBinding.inflate(inflater, container, false)
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
                cargarLineChart()




            }
        }
    }

    private fun cargarDatos() {
        val categorias = listOf("Gastos Hormiga", "Alimentos", "Transporte", "Servicios", "Mercado")
        val datosGrafico = mutableMapOf<String, Float>()

        // Obtener y almacenar el valor disponible
        gastosViewModel.obtenerDineroDisponible(usuarioId)
        gastosViewModel.dineroDisponibleLiveData.observe(viewLifecycleOwner) { disp ->
            if (disp != null) {
                disponible = disp.toFloat().toDouble()
                datosGrafico["Disponible"] = disponible.toFloat()
                cargarGraficoAvanzado(datosGrafico)
                cargarGraficoRadar(datosGrafico)
                cargarLineChart()
            }
        }

        // Recorrer las categorías para agregar sus valores al gráfico
        for (categoria in categorias) {
            gastosViewModel.obtenerValorGastosMesCategoria(usuarioId, categoria)
            gastosViewModel.valorGastosMesCategoriaLiveData.observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    datosGrafico[categoria] = cantidad.toFloat() // Guarda los valores en el mapa
                    cargarGraficoAvanzado(datosGrafico) // Recargar gráfico combinado
                    cargarGraficoRadar(datosGrafico) // Recargar gráfico radar
                    cargarLineChart()
                }
            }
        }

        // Obtener el valor total de gastos del mes
        gastosViewModel.obtenerValorGastosMes(usuarioId)
        gastosViewModel.valorGastosMesLiveData.observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes != null) {
                cargarGraficoAvanzado(datosGrafico)
                cargarGraficoRadar(datosGrafico)
                cargarLineChart()
            }
        }
    }


    private fun cargarGraficoAvanzado(datos: Map<String, Float>) {

        val combinedChart = binding.dona2

        val categories = mutableListOf<String>()
        var index = 0f

        val colorMap = mapOf(
            "Gastos Hormiga" to Color.parseColor("#F66B6B"),
            "Alimentos" to Color.parseColor("#FF66C1"),
            "Transporte" to Color.parseColor("#339AF0"),
            "Servicios" to Color.parseColor("#EEB62B"),
            "Mercado" to Color.parseColor("#FD8435"),
            "Disponible" to Color.parseColor("#87EE2B")
        )

        val barDataSets = mutableListOf<BarDataSet>()

        for ((categoria, cantidad) in datos) {
            val barEntries = listOf(BarEntry(index, cantidad))
            val barDataSet = BarDataSet(barEntries, categoria)
            barDataSet.color = colorMap[categoria] ?: Color.WHITE
            barDataSet.setDrawValues(true)
            barDataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("$%.2f", value)
                }
            }
            barDataSet.valueTextColor = Color.WHITE
            barDataSet.valueTextSize = 12f
            barDataSet.barBorderWidth = 0.9f

            barDataSets.add(barDataSet)
            categories.add(categoria)
            index += 1f
        }

        val barData = BarData(barDataSets as List<IBarDataSet>?)
        barData.barWidth = 0.9f

        val combinedData = CombinedData()
        combinedData.setData(barData)

        // Configurar el eje X
        val xAxis = combinedChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(categories)
        xAxis.labelCount = categories.size
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f
        xAxis.setDrawGridLines(false)


        xAxis.setDrawLabels(false)
        xAxis.setDrawAxisLine(false)

        val yAxis = combinedChart.axisLeft
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE


        val yAxisRight = combinedChart.axisRight
        yAxisRight.isEnabled = false

        combinedChart.data = combinedData
        combinedChart.description.isEnabled = false


        // Configurar la leyenda
        val legend = combinedChart.legend
        legend.isEnabled = true
        legend.form = Legend.LegendForm.SQUARE
        legend.textSize = 12f
        legend.textColor = Color.WHITE
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.setDrawInside(false)


        combinedChart.animateY(1000)
        combinedChart.invalidate()
    }


    private fun cargarGraficoRadar(datos: Map<String, Float>) {
        val radarChart = binding.radarChart

        val radarEntries = mutableListOf<RadarEntry>()
        val labels = mutableListOf<String>()

        for ((categoria, cantidad) in datos) {
            radarEntries.add(RadarEntry(cantidad))
            labels.add(categoria)
        }

        val radarDataSet = RadarDataSet(radarEntries, "Gastos por Categoría")
        radarDataSet.color = Color.BLUE
        radarDataSet.fillColor = Color.WHITE
        radarDataSet.setDrawFilled(true)
        radarDataSet.lineWidth = 2f
        radarDataSet.valueTextColor = Color.WHITE
        radarDataSet.valueTextSize = 12f

        val radarData = RadarData(radarDataSet)
        radarChart.data = radarData
        radarChart.description.isEnabled = false
        // Configurar el eje X
        val xAxis = radarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE // Cambiar el color del texto del eje X
        xAxis.axisLineColor = Color.WHITE // Cambiar el color del eje X

        // Configurar el eje Y
        val yAxis = radarChart.yAxis
        yAxis.axisMinimum = 0f
        yAxis.axisLineColor = Color.WHITE // Cambiar el color del eje Y
        yAxis.textColor = Color.WHITE // Cambiar el color del texto del eje Y

        // Eliminar la leyenda
        radarChart.legend.isEnabled = false

        // Actualizar el gráfico
        radarChart.invalidate()

    }



    private fun cargarLineChart() {

        val lineChart = binding.lineChart
        val fechaInicio = "2024-01-01"
        val fechaFin = "2024-12-31"

        gastosViewModel.listarGastosPorFechas(usuarioId, fechaInicio, fechaFin)
        gastosViewModel.gastosPorFechasLiveData.observe(viewLifecycleOwner) { gastos ->

            gastos?.let {

            val entries = mutableListOf<Entry>()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Añadir entradas al gráfico
            for (gasto in gastos) {
                val fecha = dateFormat.parse(gasto.fecha)?.time ?: 0L
                entries.add(Entry(fecha.toFloat(), gasto.valor.toFloat()))
            }

            val lineDataSet = LineDataSet(entries, "Gastos")

            // Configuración de estilo
            lineDataSet.color = Color.BLUE  // Color de la línea
            lineDataSet.valueTextColor = Color.WHITE  // Color de los valores

            // Configuración de los puntos
            lineDataSet.setDrawCircles(true)  // Mostrar los puntos
            lineDataSet.setCircleColor(Color.RED)  // Puntos rojos
            lineDataSet.setCircleHoleColor(Color.RED)  // Puntos sólidos sin huecos
            lineDataSet.setDrawValues(true)  // Mostrar los valores en los puntos
            lineDataSet.circleRadius = 5f  // Tamaño de los puntos
            lineDataSet.lineWidth = 2f  // Grosor de la línea
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER  // Línea suave entre puntos

            val lineData = LineData(lineDataSet)

            // Asignar datos al gráfico
            lineChart.data = lineData

            lineChart.description.isEnabled = false
            // Animación
            lineChart.animateXY(1000, 1000)  // Animar en X y Y (1 segundo)

            // Configuración del eje X (fechas)
            val xAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return dateFormat.format(Date(value.toLong()))
                }
            }
            xAxis.setDrawGridLines(false)  // Quitar líneas de la cuadrícula
            xAxis.textColor = Color.WHITE  // Color del texto en el eje X
            xAxis.labelRotationAngle = -45f  // Rotar etiquetas de fecha si es necesario

            // Configuración del eje Y (valores)
            val yAxis = lineChart.axisLeft
            yAxis.textColor = Color.WHITE  // Cambiar el color de los números en el eje Y

            // Configuración adicional del gráfico
            lineChart.description.text = "Gastos por Fecha"
            lineChart.description.textColor = Color.WHITE
            lineChart.axisRight.isEnabled = false  // Deshabilitar eje derecho

            // Refrescar el gráfico
            lineChart.invalidate()

        }
        }
    }


    private fun Ingresoschart() {


    }


}






