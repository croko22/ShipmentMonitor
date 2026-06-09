package com.kevinchambi.shipmentmonitor.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinchambi.shipmentmonitor.data.model.Vehicle
import com.kevinchambi.shipmentmonitor.data.repository.VehicleRepository
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val vehicleRepository = VehicleRepository()

    private val _vehicles = MutableLiveData<List<Vehicle>>()
    val vehicles: LiveData<List<Vehicle>> = _vehicles

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadVehicles() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = vehicleRepository.getVehicles()
            _isLoading.value = false
            if (result.isSuccess) {
                _vehicles.value = result.getOrNull() ?: emptyList()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }
}
