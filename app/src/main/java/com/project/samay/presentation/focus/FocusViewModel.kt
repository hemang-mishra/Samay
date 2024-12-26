package com.project.samay.presentation.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.domain.model.TaskEntity
import com.project.samay.domain.service.ServiceHelper
import com.project.samay.domain.usecases.FocusScreenUseCases
import com.project.samay.util.Constants
import com.project.samay.util.calculations.TimeUtils
import kotlinx.coroutines.launch

class FocusViewModel(private val focusScreenUseCases: FocusScreenUseCases): ViewModel() {
    val tasks = focusScreenUseCases.allTasks

    fun startButton(context: Context){
        ServiceHelper.triggerForegroundService(
            context,
            Constants.ACTION_SERVICE_START,
        )
    }

    fun stopButton(context: Context){
        ServiceHelper.triggerForegroundService(
            context,
            Constants.ACTION_SERVICE_STOP
        )
    }

    fun ignoreButtonWhileRunning(context: Context){
        ServiceHelper.triggerForegroundService(
            context,
            Constants.ACTION_SERVICE_CANCEL
        )
    }

    fun ignoreButtonWhileStopped(context: Context){
        ServiceHelper.triggerForegroundService(
            context,
            Constants.ACTION_SERVICE_CANCEL
        )
    }

    fun saveButton(context: Context, selectedTask: TaskEntity, hours: String, minutes: String, target: Long){
        ServiceHelper.triggerForegroundService(
            context,
            Constants.ACTION_SERVICE_CANCEL)
        val hrs = hours.toIntOrNull()
        val min = minutes.toIntOrNull()
        if(hrs == null || min == null) return
        val time = hrs*60+min
        viewModelScope.launch {
            focusScreenUseCases.useTime(context, time, selectedTask,
                TimeUtils.addMinutesToMillis(System.currentTimeMillis(), -time),System.currentTimeMillis(),
                target)
        }
    }
}