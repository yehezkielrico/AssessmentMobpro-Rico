package com.d3if3082.ricocell.ui.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if3082.ricocell.model.HandPhone
import com.d3if3082.ricocell.network.ApiStatus
import com.d3if3082.ricocell.network.HandphoneApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<HandPhone>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set


    init {
        retrieveData()
    }

    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = HandphoneApi.service.getHandphone()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }

    fun addHanphone(handPhone: HandPhone) {
        viewModelScope.launch {
            try {
                HandphoneApi.service.addHandphone(handPhone)
                retrieveData()
            } catch (e: Exception) {
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun deleteData(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                HandphoneApi.service.deleteHanphone(id)
                retrieveData() // Update data after deletion
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
                status.value = ApiStatus.FAILED
            }
        }
    }
}