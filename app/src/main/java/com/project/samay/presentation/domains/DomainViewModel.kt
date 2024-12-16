package com.project.samay.presentation.domains

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.data.model.DomainEntity
import com.project.samay.domain.usecases.DomainScreenUseCases
import kotlinx.coroutines.launch

class DomainViewModel(private val domainScreenUseCases: DomainScreenUseCases): ViewModel() {
    val allDomains = domainScreenUseCases.allDomains
    private var uiState = mutableStateOf(DomainUiState())
    val uiStateValue: State<DomainUiState> = uiState


    fun addTimeInMin(context: Context,timeStr: String): Boolean{
        val time = timeStr.toIntOrNull()
        val domainEntity = uiState.value.selectedDomain
        if(domainEntity == null){
            showToast(context, "Select a domain to add time")
            return false
        }
        if(time == null){
            showToast(context, "Time should be a number")
            return false
        }
        viewModelScope.launch {
            domainScreenUseCases.addTimeInMin(time, domainEntity)
        }
        return true
    }

    fun deleteDomain(domainEntity: DomainEntity){
        viewModelScope.launch {
            domainScreenUseCases.deleteTask(domainEntity)
        }
    }

    fun getTotalExpectedPercentSum(domains: List<DomainEntity>, time: Float?): Float{
        val oldDomainPercent = uiState.value.selectedDomain?.expectedPercentage?:0.0f
        var totalPercent = 0.0f
        domains.forEach {
            if(it.id != 404)
            totalPercent += it.expectedPercentage
        }
        return totalPercent + (time?:0.0f) - oldDomainPercent
    }

    fun selectDomain(domainEntity: DomainEntity){
        if(uiState.value.selectedDomain == domainEntity){
            uiState.value = uiState.value.copy(selectedDomain = null)
            return
        }
        uiState.value = uiState.value.copy(selectedDomain = domainEntity)
    }

    fun saveNewDomain(context: Context, name: String, description: String, monthlyTarget: String, expectedPercent: String, timeSpent: String): Boolean{
        if(!validateDetails(context, name, description, monthlyTarget, expectedPercent, timeSpent)){
            return false
        }
        viewModelScope.launch {
            domainScreenUseCases.insertNewDomain(
                name = name,
                description = description,
                monthlyTarget = monthlyTarget,
                expectedPercent = expectedPercent.toFloat(),
                timeSpent=timeSpent.toLong()
            )
        }
        return true
    }

    fun updateDomain(context: Context, name: String, description: String, monthlyTarget: String, expectedPercent: String, timeSpent: String
    ): Boolean{
        val oldDomain = uiState.value.selectedDomain ?: return false
        if(!validateDetails(context, name, description, monthlyTarget, expectedPercent, timeSpent)){
            return false
        }
        viewModelScope.launch {
            domainScreenUseCases.updateDomainDetails(
                name = name,
                description = description,
                monthlyTarget = monthlyTarget,
                expectedPercent = expectedPercent.toFloat(),
                oldDomainEntity = oldDomain,
                timeSpent = timeSpent.toLong()
            )
        }
        return true
    }


    private fun validateDetails(context: Context, name: String, description: String, monthlyTarget: String, expectedPercent: String, timeSpent: String): Boolean{
        if(name.isEmpty()){
            showToast(context, "Name cannot be empty")
            return false
        }
        if(description.isEmpty()){
            showToast(context, "Description cannot be empty")
            return false
        }
        if(monthlyTarget.isEmpty()){
            showToast(context, "Monthly target cannot be empty")
            return false
        }
        if(name.length > 15) {
            showToast(context, "Name cannot be more than 15 characters")
            return false
        }
        if(description.length > 50) {
            showToast(context, "Description cannot be more than 50 characters")
            return false
        }
        if(monthlyTarget.length > 100) {
            showToast(context, "Monthly target cannot be more than 100 characters")
            return false
        }
        if(expectedPercent.toFloatOrNull() == null){
            showToast(context, "Expected percentage should be a number")
            return false
        }
        if(timeSpent.toLongOrNull() == null)
        {
            showToast(context, "Time spent should be a number")
            return false
        }
        return true
    }

    fun showToast(context: Context, text: String){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}