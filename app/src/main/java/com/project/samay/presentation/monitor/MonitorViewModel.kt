package com.project.samay.presentation.monitor

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.domain.model.MonitoredApps
import com.project.samay.domain.usecases.MonitorAppsScreenUseCases
import com.project.samay.domain.usecases.ONE_SESSION
import com.project.samay.util.calculations.Logic
import com.project.samay.util.calculations.TimeUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MonitorViewModel(private val monitorAppsScreenUseCases: MonitorAppsScreenUseCases) :
    ViewModel() {

    private var _areAppsMonitored = mutableStateOf(false)
    val areAppsMonitored: State<Boolean> = _areAppsMonitored

    private val _isEmergency = mutableStateOf(false)
    val isEmergency: State<Boolean> = _isEmergency

    private val _data = mutableStateOf(emptyMap<MonitoredApps, Long>())
    val data: State<Map<MonitoredApps, Long>> = _data

    val appData = monitorAppsScreenUseCases.getData()


    init {
        getData()
        areAppsMonitored()
    }
    fun areAppsMonitored() {
        viewModelScope.launch {
            _areAppsMonitored.value = monitorAppsScreenUseCases.areAppsMonitored()
        }
    }

    fun getData() {
        viewModelScope.launch {
            _data.value = monitorAppsScreenUseCases.getData().first()
        }
    }


    fun getProgress(lastUsed: Long): Float{
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - lastUsed
        return if (timeDifference > ONE_SESSION * 1000) 1.0f else timeDifference.toFloat() / (ONE_SESSION * 1000)
    }


    fun getActivationTime(lastUsed: Long): String{
        val time = lastUsed+ONE_SESSION*1000
        if(time == 0L)
            return "Already Activated"
        else
            return TimeUtils.convertMillisToString(time)
    }

    fun navigateToApp(packageName: String) {
        monitorAppsScreenUseCases.navigateToApp(packageName)
    }

    fun toggleEmergency(value: Boolean) {
        _isEmergency.value = value
    }

}