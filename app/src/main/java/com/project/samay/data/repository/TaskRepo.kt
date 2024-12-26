package com.project.samay.data.repository


import com.project.samay.domain.model.TaskEntity
import com.project.samay.data.source.local.TaskDao
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun upsertTask(taskEntity: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.upsertTask(taskEntity)
        updateRateAndPercent()
    }


    suspend fun getTaskById(taskId: Int): TaskEntity? = withContext(Dispatchers.IO) {
        taskDao.getTaskById(taskId)
    }

    suspend fun deleteTaskById(taskId: Int) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(taskId)
        updateRateAndPercent()
    }

    fun calculateTotalTimeSpent(tasks: List<TaskEntity>): Long{
        var totalTime = 0L
        tasks.forEach {
            totalTime += it.timeSpentInMin
        }
        return totalTime
    }

    private suspend fun assignPercentages(){
        val allTasks = allTasks.first()
        val totalWeight = allTasks.sumOf { it.weight }
        allTasks.forEach {
            val newTaskEntity = it.copy(percentageExpected = (it.weight * 100.0f) / totalWeight)
            taskDao.upsertTask(newTaskEntity)
        }
    }

    private suspend fun updateRateAndPercent(){
        assignPercentages()
        val allTasks = allTasks.first()
        val totalTime = calculateTotalTimeSpent(allTasks)
        allTasks.forEach {
            val presentPercent = if(totalTime != 0L) (it.timeSpentInMin * 100.0f) / totalTime else 0.0f
            val rate = Logic.rateCalculator(presentPercent, it.percentageExpected)
            val newTaskEntity = it.copy(percentagePresent = presentPercent, rate = rate)
            taskDao.upsertTask(newTaskEntity)
        }
    }
}