package com.project.samay.domain.usecases

import com.project.samay.data.repository.TaskRepository
import com.project.samay.domain.model.MonitoredApps
import com.project.samay.domain.repository.UsageRepository
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

const val ONE_SESSION = 60*3*60

class MonitorAppsScreenUseCases(private val usageRepository: UsageRepository, private val taskRepository: TaskRepository) {
//    val mapUsage = flowOf(emptyMap<MonitoredApps, Long>())
//
//    private suspend fun getLastTimeUsed(packageName: String): Long{
//        return usageRepository.getLastTimeUsed(packageName)
//    }
//
    suspend fun areAppsMonitored(): Boolean{
        val tasks = taskRepository.allTasks.first()
        return taskRepository.calculateTotalTimeSpent(tasks) != 0L
    }
//
//    fun getProgress(packageName: String): Float {
//        val lastTimeUsed = getLastTimeUsed(packageName)
//        val currentTime = System.currentTimeMillis()
//        val timeDifference = currentTime - lastTimeUsed
//        return if (timeDifference > ONE_SESSION * 1000) 1.0f else timeDifference.toFloat() / (ONE_SESSION * 1000)
//    }
//
//    fun getActivationTime(packageName: String): String{
//        val time = getLastTimeUsed(packageName)+ONE_SESSION*1000
//        if(time == 0L)
//            return "Already Activated"
//        return Logic.formatInHrsAndMins(time)
//    }

    fun getData() = usageRepository.getData()

    fun navigateToApp(packageName: String){
        usageRepository.navigateToApp(packageName)
    }
}