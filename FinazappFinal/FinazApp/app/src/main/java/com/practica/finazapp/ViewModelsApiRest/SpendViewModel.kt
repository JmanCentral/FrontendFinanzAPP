package com.practica.finazapp.ViewModelsApiRest


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.practica.finazapp.RepositoriosApiRest.SpendRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practica.finazapp.Entidades.GastoDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

    class SpendViewModel (application: Application) : AndroidViewModel(application)   {

        private val repository: SpendRepository by lazy {
            SpendRepository(getApplication<Application>().applicationContext)
        }

        private val _dineroDisponibleFlow = MutableStateFlow<Double?>(null)
        val dineroDisponibleFlow: StateFlow<Double?> = _dineroDisponibleFlow.asStateFlow()

        private val _gastosMesCategoriaFlow = MutableStateFlow<List<GastoDTO>?>(null)
        val gastosMesCategoriaFlow: StateFlow<List<GastoDTO>?> = _gastosMesCategoriaFlow.asStateFlow()

        private val _valorGastosMesCategoriaFlow = MutableStateFlow<Double?>(null)
        val valorGastosMesCategoriaFlow: StateFlow<Double?> = _valorGastosMesCategoriaFlow.asStateFlow()

        private val _valorGastosMesCategoriaFlow1 = MutableStateFlow<Double?>(null)
        val valorGastosMesCategoriaFlow1: StateFlow<Double?> = _valorGastosMesCategoriaFlow1.asStateFlow()

        private val _valorGastosMesCategoriaFlow2 = MutableStateFlow<Double?>(null)
        val valorGastosMesCategoriaFlow2: StateFlow<Double?> = _valorGastosMesCategoriaFlow2.asStateFlow()

        private val _valorGastosMesCategoriaFlow3 = MutableStateFlow<Double?>(null)
        val valorGastosMesCategoriaFlow3: StateFlow<Double?> = _valorGastosMesCategoriaFlow3.asStateFlow()

        private val _valorGastosMesCategoriaFlow4 = MutableStateFlow<Double?>(null)
        val valorGastosMesCategoriaFlow4: StateFlow<Double?> = _valorGastosMesCategoriaFlow4.asStateFlow()

        private val _valorGastosMesCategoriaFlow5 = MutableStateFlow<Double?>(null)
        val valorGastosMesCategoriaFlow5: StateFlow<Double?> = _valorGastosMesCategoriaFlow5.asStateFlow()

        private val _valorGastosMesFlow = MutableStateFlow<Double?>(null)
        val valorGastosMesFlow: StateFlow<Double?> = _valorGastosMesFlow.asStateFlow()

        private val _gastosPorFechasFlow = MutableStateFlow<List<GastoDTO>?>(null)
        val gastosPorFechasFlow: StateFlow<List<GastoDTO>?> = _gastosPorFechasFlow.asStateFlow()

        private val _gastosAscendentesFlow = MutableStateFlow<List<GastoDTO>?>(null)
        val gastosAscendentesFlow: StateFlow<List<GastoDTO>?> = _gastosAscendentesFlow.asStateFlow()

        private val _gastosAltosFlow = MutableStateFlow<List<GastoDTO>?>(null)
        val gastosAltosFlow: StateFlow<List<GastoDTO>?> = _gastosAltosFlow.asStateFlow()

        private val _gastosBajosFlow = MutableStateFlow<List<GastoDTO>?>(null)
        val gastosBajosFlow: StateFlow<List<GastoDTO>?> = _gastosBajosFlow.asStateFlow()

        private val _gastosDescendentesFlow = MutableStateFlow<List<GastoDTO>?>(null)
        val gastosDescendentesFlow: StateFlow<List<GastoDTO>?> = _gastosDescendentesFlow.asStateFlow()

        private val _promedioGastosFlow = MutableStateFlow<Double?>(null)
        val promedioGastosFlow: StateFlow<Double?> = _promedioGastosFlow.asStateFlow()

        private val _gastoRecurrenteFlow = MutableStateFlow<String?>(null)
        val gastoRecurrenteFlow: StateFlow<String?> = _gastoRecurrenteFlow.asStateFlow()

        private val _porcentajeGastosFlow = MutableStateFlow<Double?>(null)
        val porcentajeGastosFlow: StateFlow<Double?> = _porcentajeGastosFlow.asStateFlow()

        private val _promedioDiarioFlow = MutableStateFlow<Double?>(null)
        val promedioDiarioFlow: StateFlow<Double?> = _promedioDiarioFlow.asStateFlow()

        private val _operacionCompletadaFlow = MutableStateFlow<Boolean>(false)
        val operacionCompletadaFlow: StateFlow<Boolean> = _operacionCompletadaFlow.asStateFlow()

        private val _errorFlow = MutableStateFlow<String?>(null)
        val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

        // Función para registrar un gasto
        fun registrarGasto(idUsuario: Long, gasto: GastoDTO) {
            viewModelScope.launch {
                repository.registrarGasto(idUsuario, gasto) { resultado, error ->
                    if (error == null) {
                        _operacionCompletadaFlow.value = true // Notificar que la operación se completó
                    } else {
                        _errorFlow.value = error
                    }
                }
            }
        }

        // Función para obtener el dinero disponible
        fun obtenerDineroDisponible(idUsuario: Long) {
            viewModelScope.launch {
                repository.obtenerDineroDisponible(idUsuario) { dineroDisponible, error ->
                    if (error == null) {
                        _dineroDisponibleFlow.value = dineroDisponible
                    } else {
                        _errorFlow.value = error
                    }

                }
            }
        }

        // Función para obtener gastos por mes y categoría
        fun obtenerGastosMesCategoria(idUsuario: Long, categoria: String) {
            viewModelScope.launch {
                repository.obtenerGastosMesCategoria(idUsuario, categoria) { gastos, error ->
                    if (error == null) {
                        _gastosMesCategoriaFlow.value = gastos
                    } else {
                        _errorFlow.value = error
            }
        }

        }
            }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria(idUsuario: Long, categoria: String) {
            viewModelScope.launch {
                repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                    if (error == null) {
                        _valorGastosMesCategoriaFlow.value = valor
                    } else {
                        _errorFlow.value = error
                    }
                }

            }
        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria1(idUsuario: Long, categoria: String) {
            viewModelScope.launch {
                repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                    if (error == null) {
                        _valorGastosMesCategoriaFlow1.value = valor
                    } else {
                        _errorFlow.value = error
                    }
                }
            }
        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria2(idUsuario: Long, categoria: String) {
            viewModelScope.launch {
                repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                    if (error == null) {
                        _valorGastosMesCategoriaFlow2.value = valor
                    } else {
                        _errorFlow.value = error
                    }
                }
            }
        }


        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria3(idUsuario: Long, categoria: String) {
            viewModelScope.launch {
                repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                    if (error == null) {
                        _valorGastosMesCategoriaFlow3.value = valor
                    } else {
                        _errorFlow.value = error
                    }
                }
            }
        }


        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria4(idUsuario: Long, categoria: String) {
            viewModelScope.launch {
                repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                    if (error == null) {
                        _valorGastosMesCategoriaFlow4.value = valor
                    }
                    else {
                        _errorFlow.value = error
                    }
                }
            }

        }

    // Función para obtener el valor general de gastos por mes y categoría
    fun obtenerValorGastosMesCategoria5(idUsuario: Long, categoria: String) {

        viewModelScope.launch {
            repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                if (error == null) {
                    _valorGastosMesCategoriaFlow5.value = valor
                } else {
                    _errorFlow.value = error
        }
                }
    }

    }



    // Función para obtener el valor general de gastos por mes
    fun obtenerValorGastosMes(idUsuario: Long) {
        viewModelScope.launch {
            repository.obtenerValorGastosMes(idUsuario) { valor, error ->
                if (error == null) {
                    _valorGastosMesFlow.value = valor
                } else {
                    _errorFlow.value = error
        }
                }
        }

    }

    // Función para obtener gastos por fechas
    fun listarGastosPorFechas(idUsuario: Long, fechaInicial: String, fechaFinal: String) {
        viewModelScope.launch {
            repository.listarGastosPorFechas(idUsuario, fechaInicial, fechaFinal) { gastos, error ->
                if (error == null) {
                    _gastosPorFechasFlow.value = gastos
                    } else {
                    _errorFlow.value = error
                }
            }
        }

    }

    // Función para obtener gastos ordenados ascendentemente
    fun listarGastosAscendentemente(idUsuario: Long) {

    }

    // Función para obtener gastos ordenados por valor alto
    fun listarGastosAlto(idUsuario: Long) {

    }

    // Función para obtener gastos ordenados por valor bajo
    fun listarGastosBajo(idUsuario: Long) {

    }

    // Función para obtener gastos ordenados descendentemente
    fun listarGastosDescendentemente(idUsuario: Long) {

    }

    // Función para obtener el promedio de gastos
    fun obtenerPromedioGastos(idUsuario: Long) {

    }

    // Función para obtener el gasto recurrente
    fun obtenerGastoRecurrente(idUsuario: Long) {

    }

    // Función para obtener el porcentaje de gastos sobre ingresos
    fun obtenerPorcentajeGastos(idUsuario: Long) {

    }

    // Función para obtener el promedio diario de gastos
    fun obtenerPromedioDiario(idUsuario: Long) {

    }

    // Función para modificar un gasto
    fun modificarGasto(idGasto: Long, gasto: GastoDTO) {
        viewModelScope.launch {
            repository.modificarGasto(idGasto, gasto) { resultado, error ->
                if (error == null) {
                    _operacionCompletadaFlow.value = true
                } else {
                    _errorFlow.value = error
                }
            }
        }
    }

    // Función para eliminar un gasto
    fun eliminarGasto(idGasto: Long) {
        viewModelScope.launch {
            repository.eliminarGasto(idGasto)
            { exito, error ->
                if (exito) {
                    _operacionCompletadaFlow.value = true
                } else {
                    _errorFlow.value = error
                    }
            }
        }

    }
}