package com.practica.finazapp.ViewModelsApiRest


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.practica.finazapp.RepositoriosApiRest.SpendRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.practica.finazapp.Entidades.GastoDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.Entidades.CategoriaTotalDTO
import com.practica.finazapp.Entidades.ProyeccionDTO
import kotlinx.coroutines.launch

    class SpendViewModel (application: Application) : AndroidViewModel(application)   {

        private val repository: SpendRepository by lazy {
            SpendRepository(getApplication<Application>().applicationContext)
        }

        // LiveData para el dinero disponible
        private val _dineroDisponibleLiveData = MutableLiveData<Double?>()
        val dineroDisponibleLiveData: LiveData<Double?> = _dineroDisponibleLiveData.distinctUntilChanged()

        private val _obtenerfrecuentestedegastos = MutableLiveData<List<ProyeccionDTO>?>()
        val obtenerfrecuentestedegastos: LiveData<List<ProyeccionDTO>?> = _obtenerfrecuentestedegastos.distinctUntilChanged()

        // LiveData para la lista de gastos por mes y categoría
        private val _gastosMesCategoriaLiveData = MutableLiveData<List<GastoDTO>?>()
        val gastosMesCategoriaLiveData: LiveData<List<GastoDTO>?> = _gastosMesCategoriaLiveData.distinctUntilChanged()

        // LiveData para el valor general de gastos por mes y categoría
        private val _valorGastosMesCategoriaLiveData = MutableLiveData<Double?>()
        val valorGastosMesCategoriaLiveData: LiveData<Double?> = _valorGastosMesCategoriaLiveData.distinctUntilChanged()

        private val _valorGastosMesCategoriaLiveData1 = MutableLiveData<Double?>()
        val valorGastosMesCategoriaLiveData1: LiveData<Double?> = _valorGastosMesCategoriaLiveData1.distinctUntilChanged()

        private val _valorGastosMesCategoriaLiveData2 = MutableLiveData<Double?>()
        val valorGastosMesCategoriaLiveData2: LiveData<Double?> = _valorGastosMesCategoriaLiveData2.distinctUntilChanged()

        private val _valorGastosMesCategoriaLiveData3 = MutableLiveData<Double?>()
        val valorGastosMesCategoriaLiveData3: LiveData<Double?> = _valorGastosMesCategoriaLiveData3.distinctUntilChanged()

        private val _valorGastosMesCategoriaLiveData4 = MutableLiveData<Double?>()
        val valorGastosMesCategoriaLiveData4: LiveData<Double?> = _valorGastosMesCategoriaLiveData4.distinctUntilChanged()

        private val _valorGastosMesCategoriaLiveData5 = MutableLiveData<Double?>()
        val valorGastosMesCategoriaLiveData5: LiveData<Double?> = _valorGastosMesCategoriaLiveData5

        // LiveData para el valor general de gastos por mes
        private val _valorGastosMesLiveData = MutableLiveData<Double?>()
        val valorGastosMesLiveData: LiveData<Double?> = _valorGastosMesLiveData

        // LiveData para la lista de gastos por fechas
        private val _gastosPorFechasLiveData = MutableLiveData<List<GastoDTO>?>()
        val gastosPorFechasLiveData: LiveData<List<GastoDTO>?> = _gastosPorFechasLiveData

        // LiveData para la lista de gastos ordenados ascendentemente
        private val _gastosAscendentesLiveData = MutableLiveData<List<GastoDTO>?>()
        val gastosAscendentesLiveData: LiveData<List<GastoDTO>?> = _gastosAscendentesLiveData

        // LiveData para la lista de gastos ordenados por valor alto
        private val _gastosAltosLiveData = MutableLiveData <GastoDTO?>()
        val gastosAltosLiveData: LiveData<GastoDTO?> = _gastosAltosLiveData

        // LiveData para la lista de gastos ordenados por valor bajo
        private val _gastosBajosLiveData = MutableLiveData<GastoDTO?>()
        val gastosBajosLiveData: LiveData<GastoDTO?> = _gastosBajosLiveData

        // LiveData para la lista de gastos ordenados descendentemente
        private val _gastosDescendentesLiveData = MutableLiveData<List<GastoDTO>?>()
        val gastosDescendentesLiveData: LiveData<List<GastoDTO>?> = _gastosDescendentesLiveData

        // LiveData para el promedio de gastos
        private val _promedioGastosLiveData = MutableLiveData<Double?>()
        val promedioGastosLiveData: LiveData<Double?> = _promedioGastosLiveData

        // LiveData para el gasto recurrente
        private val _gastoRecurrenteLiveData = MutableLiveData<String?>()
        val gastoRecurrenteLiveData: LiveData<String?> = _gastoRecurrenteLiveData

        // LiveData para el porcentaje de gastos sobre ingresos
        private val _porcentajeGastosLiveData = MutableLiveData<Double?>()
        val porcentajeGastosLiveData: LiveData<Double?> = _porcentajeGastosLiveData

        // LiveData para el promedio diario de gastos
        private val _promedioDiarioLiveData = MutableLiveData<Double?>()
        val promedioDiarioLiveData: LiveData<Double?> = _promedioDiarioLiveData

        // LiveData para el porcentaje de gastos sobre ingresos
        private val _CategoriaMasAlta = MutableLiveData<List<CategoriaTotalDTO>?>()
        val CategoriaMasAlta: LiveData<List<CategoriaTotalDTO>?> = _CategoriaMasAlta

        // LiveData para indicar si una operación ha sido completada
        private val _operacionCompletada = MutableLiveData<Boolean>()
        val operacionCompletada: LiveData<Boolean> = _operacionCompletada

        private val _listarhastospornombre = MutableLiveData<List<GastoDTO>?>()
        val listarhastospornombre: LiveData<List<GastoDTO>?> = _listarhastospornombre


        // LiveData para manejar errores
        private val _errorLiveData = MutableLiveData<String?>()
        val errorLiveData: LiveData<String?> = _errorLiveData

        // Función para registrar un gasto
        fun registrarGasto(idUsuario: Long, gasto: GastoDTO) {
            viewModelScope.launch {
                repository.registrarGasto(idUsuario, gasto) { resultado, error ->
                    if (error == null) {
                        _operacionCompletada.postValue(true) // Notificar que la operación se completó
                    } else {
                        _errorLiveData.postValue(error)
                    }
                }
            }
        }

        // Función para obtener el dinero disponible
        fun obtenerDineroDisponible(idUsuario: Long) {

                repository.obtenerDineroDisponible(idUsuario) { dinero, error ->
                    if (error == null) {
                        _dineroDisponibleLiveData.postValue(dinero)
                    } else {
                        _errorLiveData.postValue(error)
                    }
                }

        }

        fun obtenerFrecuentesTedeGastos(idUsuario: Long) {
            repository.obtenerGastosFrecuentes(idUsuario) {frecuentes, error ->
                if (error == null) {
                    _obtenerfrecuentestedegastos.postValue(frecuentes)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }

        // Función para obtener gastos por mes y categoría
        fun obtenerGastosMesCategoria(idUsuario: Long, categoria: String) {

                repository.obtenerGastosMesCategoria(idUsuario, categoria) { gastos, error ->
                    if (error == null) {
                        _gastosMesCategoriaLiveData.postValue(gastos)
                    } else {
                        _errorLiveData.postValue(error)
                    }
                }

        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria(idUsuario: Long, categoria: String) {

                repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                    if (error == null) {
                        _valorGastosMesCategoriaLiveData.postValue(valor)
                    } else {
                        _errorLiveData.postValue(error)
                    }
                }

        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria1(idUsuario: Long, categoria: String) {

            repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                if (error == null) {
                    _valorGastosMesCategoriaLiveData1.postValue(valor)
                } else {
                    _errorLiveData.postValue(error)
                }
            }

        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria2(idUsuario: Long, categoria: String) {

            repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                if (error == null) {
                    _valorGastosMesCategoriaLiveData2.postValue(valor)
                } else {
                    _errorLiveData.postValue(error)
                }
            }

        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria3(idUsuario: Long, categoria: String) {

            repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                if (error == null) {
                    _valorGastosMesCategoriaLiveData3.postValue(valor)
                } else {
                    _errorLiveData.postValue(error)
                }
            }

        }

        // Función para obtener el valor general de gastos por mes y categoría
        fun obtenerValorGastosMesCategoria4(idUsuario: Long, categoria: String) {

            repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
                if (error == null) {
                    _valorGastosMesCategoriaLiveData4.postValue(valor)
                } else {
                    _errorLiveData.postValue(error)
                }
            }

        }

    // Función para obtener el valor general de gastos por mes y categoría
    fun obtenerValorGastosMesCategoria5(idUsuario: Long, categoria: String) {

        repository.obtenerValorGastosMesCategoria(idUsuario, categoria) { valor, error ->
            if (error == null) {
                _valorGastosMesCategoriaLiveData5.postValue(valor)
            } else {
                _errorLiveData.postValue(error)
            }
        }

    }



    // Función para obtener el valor general de gastos por mes
    fun obtenerValorGastosMes(idUsuario: Long) {
            repository.obtenerValorGastosMes(idUsuario) { valor, error ->
                if (error == null) {
                    _valorGastosMesLiveData.postValue(valor)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
    }

    // Función para obtener gastos por fechas
    fun listarGastosPorFechas(idUsuario: Long, fechaInicial: String, fechaFinal: String) {
        viewModelScope.launch {
            repository.listarGastosPorFechas(idUsuario, fechaInicial, fechaFinal) { gastos, error ->
                if (error == null) {
                    _gastosPorFechasLiveData.postValue(gastos)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener gastos ordenados ascendentemente
    fun listarGastosAscendentemente(idUsuario: Long) {
        viewModelScope.launch {
            repository.listarGastosAscendentemente(idUsuario) { gastos, error ->
                if (error == null) {
                    _gastosAscendentesLiveData.postValue(gastos)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener gastos ordenados por valor alto
    fun listarGastosAlto(idUsuario: Long) {
        viewModelScope.launch {
            repository.listarGastosAlto(idUsuario) { gastos, error ->
                if (error == null) {
                    _gastosAltosLiveData.postValue(gastos)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener gastos ordenados por valor bajo
    fun listarGastosBajo(idUsuario: Long) {
        viewModelScope.launch {
            repository.listarGastosBajo(idUsuario) { gastos, error ->
                if (error == null) {
                    _gastosBajosLiveData.postValue(gastos)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener gastos ordenados descendentemente
    fun listarGastosDescendentemente(idUsuario: Long) {
        viewModelScope.launch {
            repository.listarGastosDescendentemente(idUsuario) { gastos, error ->
                if (error == null) {
                    _gastosDescendentesLiveData.postValue(gastos)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener el promedio de gastos
    fun obtenerPromedioGastos(idUsuario: Long) {
        viewModelScope.launch {
            repository.obtenerPromedioGastos(idUsuario) { promedio, error ->
                if (error == null) {
                    _promedioGastosLiveData.postValue(promedio)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener el gasto recurrente
    fun obtenerGastoRecurrente(idUsuario: Long) {
        viewModelScope.launch {
            repository.obtenerGastoRecurrente(idUsuario) { gastoRecurrente, error ->
                Log.d("GastoRecurrente", "Gasto recurrente: $gastoRecurrente")
                if (error == null) {
                    _gastoRecurrenteLiveData.postValue(gastoRecurrente)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener el porcentaje de gastos sobre ingresos
    fun obtenerPorcentajeGastos(idUsuario: Long) {
        viewModelScope.launch {
            repository.obtenerPorcentajeGastos(idUsuario) { porcentaje, error ->
                if (error == null) {
                    _porcentajeGastosLiveData.postValue(porcentaje)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para obtener el promedio diario de gastos
    fun obtenerPromedioDiario(idUsuario: Long) {
        viewModelScope.launch {
            repository.obtenerPromedioDiario(idUsuario) { promedioDiario, error ->
                if (error == null) {
                    _promedioDiarioLiveData.postValue(promedioDiario)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    fun obtenerCategoriasConMasGastos(idUsuario: Long) {
        viewModelScope.launch {
            repository.obtenerCategoriasConMasGastos(idUsuario)
            { categorias, error ->
                if (error == null) {
                    _CategoriaMasAlta.postValue(categorias)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    fun listarGastosPorNombre(idUsuario: Long, nombre: String , categoria: String) {
        viewModelScope.launch {
            repository.listarPorNombres(idUsuario, nombre, categoria)
            { gastos, error ->
                if (error == null) {
                    _listarhastospornombre.postValue(gastos)
                } else {
                    _errorLiveData.postValue(error)
                    }
            }
        }
    }

    fun eliminarGastosPorNombre (idUsuario: Long, categoria: String) {
        viewModelScope.launch {
            repository.eliminarGastos(idUsuario, categoria)
            { gastos, error ->
                if (error == null) {
                    _operacionCompletada.postValue(true)
                } else {
                    _errorLiveData.postValue(error)
                    }
            }
        }
    }


    // Función para modificar un gasto
    fun modificarGasto(idGasto: Long, gasto: GastoDTO) {
        viewModelScope.launch {
            repository.modificarGasto(idGasto, gasto) { resultado, error ->
                if (error == null) {
                    _operacionCompletada.postValue(true)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para eliminar un gasto
    fun eliminarGasto(idGasto: Long) {
        viewModelScope.launch {
            repository.eliminarGasto(idGasto) { resultado, error ->
                if (error == null) {
                    _operacionCompletada.postValue(true)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }
}