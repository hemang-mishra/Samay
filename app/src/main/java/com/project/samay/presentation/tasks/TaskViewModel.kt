package com.project.samay.presentation.tasks

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.SamayApplication
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.TaskEntity
import com.project.samay.domain.usecases.TaskScreenUseCases
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.launch

class TaskViewModel(private val taskScreenUseCases: TaskScreenUseCases): ViewModel() {
    val tasks = taskScreenUseCases.allTasks
    val domains = taskScreenUseCases.allDomains
    private val _uiState = mutableStateOf(TaskUIState())
    val uiState: State<TaskUIState> = _uiState

    fun addTimeInMin(context: Context, timeStr: String, start: Long, end: Long, target: Long): Boolean{
        val time = timeStr.toIntOrNull()
        val taskEntity = uiState.value.currentTask
        if(taskEntity == null){
            showToast(context, "Select a task to add time")
            return false
        }
        if(time == null){
            showToast(context, "Time should be a number")
            return false
        }
        viewModelScope.launch {
            taskScreenUseCases.useTimeInTask(context,time, taskEntity, start, end, target)
        }
        return true
    }


    fun getPresentTimeSpentSum(tasks: List<TaskEntity>): String{
        var totalPresentTime = 0L
        tasks.forEach {
            totalPresentTime+= it.timeSpentInMin
        }
        return Logic.formatInHrsAndMins(totalPresentTime)
    }

    fun deleteTask(taskEntity: TaskEntity){
        viewModelScope.launch {
            taskScreenUseCases.deleteTask(taskEntity)
        }
    }

    fun getTotalExpectedPercentSum(tasks: List<TaskEntity>, time: Float?): Float{
        val oldTaskPercent = uiState.value.currentTask?.percentageExpected?:0.0f
        var totalPercent = 0.0f
        tasks.forEach {
            totalPercent+= it.percentageExpected
        }
        return totalPercent+(time?:0.0f)-oldTaskPercent
    }

    fun getTimeFromWeight(tasks: List<TaskEntity>, weight: Int?, target: Long): Long{
        val weightSum = getTotalWeightSum(tasks, weight)
        if(weight == null)
            return 0L
        val expectedPercent = (weight.toFloat()*100.0f)/weightSum
        return Logic.calculateTargetTime(target, expectedPercent)
    }

    fun taskCompleted(application: SamayApplication, taskEntity: TaskEntity){
        viewModelScope.launch {
            taskScreenUseCases.taskCompleted(application, taskEntity)
        }
        _uiState.value = uiState.value.copy(null)
    }

    fun getTotalWeightSum(tasks: List<TaskEntity>, weight: Int?): Int{
        val oldTaskWeight = uiState.value.currentTask?.weight?:0
        var totalWeight = 0
        tasks.forEach {
            totalWeight+= it.weight
        }
        return totalWeight+(weight?:0)-oldTaskWeight
    }

    fun selectTask(taskEntity: TaskEntity){
        if(uiState.value.currentTask == taskEntity){
            _uiState.value = _uiState.value.copy(currentTask = null)
            return
        }
        _uiState.value = _uiState.value.copy(currentTask = taskEntity)
    }

    fun saveNewTask(context: Context, name: String, description: String, weight: String, domainEntity: DomainEntity): Boolean{
        if(!validateDetails(context, name, description, weight)){
            return false
        }
        viewModelScope.launch {
            taskScreenUseCases.insertNewTask(
                name = name,
                description = description,
                weight=weight.toInt(),
                selectedDomain = domainEntity
            )
        }
        return true
    }

    fun update(context: Context, name: String, description: String, weight: String, domainEntity: DomainEntity): Boolean{
        val oldTask = uiState.value.currentTask?:return false
        if(!validateDetails(context, name, description, weight)){
            return false
        }
        viewModelScope.launch {
            taskScreenUseCases.updateTask(
                name = name,
                description = description,
                weight = weight.toInt(),
                selectedDomain = domainEntity,
                oldtaskEntity = oldTask
            )
        }
        return true
    }

    private fun validateDetails(context: Context, name: String, description: String,  weight: String): Boolean{
        if(name.isEmpty()){
            showToast(context, "Name cannot be empty")
            return false
        }
        if(description.isEmpty()){
            showToast(context, "Description cannot be empty")
            return false
        }
        if(name.length > 15) {
            showToast(context, "Name cannot be more than 15 characters")
            return false
        }
        if(description.length > 100) {
            showToast(context, "Description cannot be more than 50 characters")
            return false
        }
        if(weight.toIntOrNull() == null){
            showToast(context, "Weight should be a number")
            return false
        }
        return true
    }

    fun reset(){
        viewModelScope.launch {
            taskScreenUseCases.reset()
        }
    }

    fun showToast(context: Context, text: String){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}

data class TaskUIState(
    val currentTask: TaskEntity? = null
)