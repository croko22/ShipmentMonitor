package com.kevinchambi.shipmentmonitor.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinchambi.shipmentmonitor.data.model.Notification
import com.kevinchambi.shipmentmonitor.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    private val notificationRepository = NotificationRepository()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadNotifications() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = notificationRepository.getNotifications()
            _isLoading.value = false
            if (result.isSuccess) {
                _notifications.value = result.getOrNull() ?: emptyList()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun removeNotification(id: Int) {
        val current = _notifications.value?.toMutableList() ?: return
        current.removeAll { it.id == id }
        _notifications.value = current
    }
}
