package com.project.samay.domain.usecases

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.project.samay.SamayApplication
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.TaskEntity
import com.project.samay.data.repository.DomainRepository
import com.project.samay.data.repository.HistoryRepository
import com.project.samay.data.repository.TaskRepository
import com.project.samay.domain.repository.CalendarRepository
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.flow.first

class TaskScreenUseCases(
    private val taskRepository: TaskRepository,
    private val domainRepository: DomainRepository,
    private val calenderRepository: CalendarRepository,
    private val historyRepository: HistoryRepository
) {
    val allTasks = taskRepository.allTasks
    val allDomains = domainRepository.allDomains

    suspend fun insertNewTask(
        name: String,
        description: String,
        weight: Int,
        selectedDomain: DomainEntity
    ) {
        val newTaskEntity = TaskEntity(
            id = 0,
            taskName = name,
            taskDescription = description,
            percentageExpected = 0.0f,
            weight = weight,
            percentagePresent = 0.0f,
            timeSpentInMin = 0,
            domainId = selectedDomain.id,
            domainName = selectedDomain.name,
            domainColor = selectedDomain.color,
            rate = 750.0f
        )
        taskRepository.upsertTask(newTaskEntity)
    }

    suspend fun updateTask(
        name: String,
        description: String,
        weight: Int,
        selectedDomain: DomainEntity,
        oldtaskEntity: TaskEntity,
    ) {
        val newTaskEntity = oldtaskEntity.copy(
            domainId = selectedDomain.id,
            domainName = selectedDomain.name,
            domainColor = selectedDomain.color,
            taskName = name,
            taskDescription = description,
            weight = weight
        )
        taskRepository.upsertTask(newTaskEntity)
    }

    suspend fun useTimeInTask(
        context: Context,
        time: Int,
        taskEntity: TaskEntity,
        start: Long,
        end: Long,
        target: Long
    ) {
        val contextApp = context as SamayApplication
        val id = contextApp.readGoalCalendarFromDataStore(context).first()
        val newTaskEntity = taskEntity.copy(timeSpentInMin = taskEntity.timeSpentInMin + time)
        taskRepository.upsertTask(newTaskEntity)
        val domainEntity = allDomains.first().find { it.id == taskEntity.domainId }
        if (domainEntity != null) {
            val newDomainEntity = domainEntity.copy(timeSpent = domainEntity.timeSpent + time)
            domainRepository.upsertDomain(newDomainEntity)
            historyRepository.addHistory(
                start,
                end,
                taskEntity.taskName,
                taskEntity.taskDescription,
                newDomainEntity
            )
        }

//        Log.i("Adding in calender", "$id ${taskEntity.taskName} $start $end")
        if (id != null)
            calenderRepository.addEvent(
                context,
                id.toLong(),
                taskEntity.taskName,
                taskEntity.taskDescription,
                start,
                end
            )
        else
            Toast.makeText(context, "Please select calender in settings to add task to calendar", Toast.LENGTH_SHORT).show()

        resetAfterGoalAchieved(target)
    }

    private suspend fun resetAfterGoalAchieved(target: Long){
        val allTasks = allTasks.first()
        if(isGoalAchieved(tasks = allTasks,target)){
            allTasks.forEach { task->
                val newTask = task.copy(timeSpentInMin = 0)
                taskRepository.upsertTask(newTask)
            }
        }
    }

    private fun isGoalAchieved(tasks: List<TaskEntity>, target: Long): Boolean{
        var achieved = true
        tasks.forEach {task->
            val t = Logic.calculateTargetTime(target, task.percentageExpected)
            if(t > task.timeSpentInMin) {
                achieved =false
                Log.i("TaskScreenUseCases", "Task ${task.taskName} is not achieved")
            }
        }
        Log.i("TaskScreenUseCases", "Goal is achieved: $achieved")
        return achieved
    }

    suspend fun taskCompleted(application: SamayApplication,taskEntity: TaskEntity){
        val presentTarget = application.readTargetFromDataStore(application).first() ?: return
        Log.i("TaskScreenUseCases", "Present target: $presentTarget ")
        val newTarget = presentTarget - Logic.calculateTargetTime(presentTarget, taskEntity.percentageExpected)
        Log.i("TaskScreenUseCases", "New target: $newTarget")
        application.saveTargetToDataStore(application,newTarget)
        taskRepository.deleteTaskById(taskEntity.id)
    }

    suspend fun deleteTask(taskEntity: TaskEntity) {
        taskRepository.deleteTaskById(taskEntity.id)
    }

    suspend fun reset(){
        allTasks.first().forEach { task->
            val newTask = task.copy(timeSpentInMin = 0)
            taskRepository.upsertTask(newTask)
        }
    }
}