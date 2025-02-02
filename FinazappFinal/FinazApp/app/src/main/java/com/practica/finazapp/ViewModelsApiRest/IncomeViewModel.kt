package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.Entidades.IngresoDTO
import com.practica.finazapp.RepositoriosApiRest.IncomeRepository
import kotlinx.coroutines.launch

class IncomeViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: IncomeRepository by lazy {
        IncomeRepository(getApplication<Application>().applicationContext)
    }

    private val _ingresosCasualesLiveData = MutableLiveData<List<IngresoDTO>?>()
    val ingresosCasualesLiveData: LiveData<List<IngresoDTO>?> = _ingresosCasualesLiveData

    private val _ingresosMensualesLiveData = MutableLiveData<List<IngresoDTO>?>()
    val ingresosMensualesLiveData: LiveData<List<IngresoDTO>?> = _ingresosMensualesLiveData

    private val _totalIngresosLiveData = MutableLiveData<Double?>()
    val totalIngresosLiveData: LiveData<Double?> = _totalIngresosLiveData

    private val _ingresosLiveData = MutableLiveData<List<IngresoDTO>?>()
    val ingresosLiveData: LiveData<List<IngresoDTO>?> = _ingresosLiveData

    private val _proyeccionIngresosLiveData = MutableLiveData<Double?>()
    val proyeccionIngresosLiveData: LiveData<Double?> = _proyeccionIngresosLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData

    private val _operacionCompletada = MutableLiveData<Boolean>()
    val operacionCompletada: LiveData<Boolean> = _operacionCompletada

    // **Registrar un ingreso**
    fun registrarIngreso(idUsuario: Long, ingreso: IngresoDTO) {
        viewModelScope.launch {
            repository.registrarIngreso(idUsuario, ingreso) { resultado, error ->
                if (error == null) {
                    _operacionCompletada.postValue(true) // Notificar que la operación se completó
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // **Listar ingresos casuales por año**
    fun listarIngresosCasualesPorAnio(idUsuario: Long) {
        repository.listarIngresosCasualesPorAnio(idUsuario) { ingresos, error ->
            if (error == null) {
                _ingresosCasualesLiveData.postValue(ingresos)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Listar ingresos mensuales**
    fun listarIngresosMensuales(idUsuario: Long) {
        repository.listarIngresosMensuales(idUsuario) { ingresos, error ->
            if (error == null) {
                _ingresosMensualesLiveData.postValue(ingresos)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Obtener total de ingresos**
    fun obtenerTotalIngresos(idUsuario: Long) {
        repository.obtenerTotalIngresos(idUsuario) { total, error ->
            if (error == null) {
                _totalIngresosLiveData.postValue(total)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Obtener ingresos mensuales por año y mes**
    fun getIngresosMensuales(idUsuario: Long, anio: Int, mes: Int) {
        repository.getIngresosMensuales(idUsuario, anio, mes) { ingresos, error ->
            if (error == null) {
                _ingresosLiveData.postValue(ingresos)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Obtener proyección de ingresos**
    fun obtenerProyeccionIngresos(idUsuario: Long) {
        repository.obtenerProyeccionIngresos(idUsuario) { proyeccion, error ->
            if (error == null) {
                _proyeccionIngresosLiveData.postValue(proyeccion)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Modificar ingreso**
    fun modificarIngreso(idIngreso: Long, ingresoDTO: IngresoDTO) {
        repository.modificarIngreso(idIngreso, ingresoDTO) { resultado, error ->
            if (error == null) {
                _operacionCompletada.postValue(true) // Notificar que la operación se completó
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Eliminar ingreso**
    fun eliminarIngreso(idIngreso: Long) {
        repository.eliminarIngreso(idIngreso) { exito, error ->
            if (exito) {
                _operacionCompletada.postValue(true) // Notificar que la operación se completó
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    // **Cargar ingresos generales**
    fun obtenerIngresos(idUsuario: Long) {
        repository.listarIngresosCasuales(idUsuario) { ingresos, error ->
            if (error == null) {
                _ingresosLiveData.postValue(ingresos)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

}