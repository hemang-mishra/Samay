package com.project.samay.presentation.domains

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.SamayApplication
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.usecases.DomainScreenUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Define AddDomainScreenState data class
data class AddDomainScreenState(
    val name: String = "",
    val description: String = "None",
    val monthlyTarget: String = "None",
    val expectedPercentage: String = "0",
    val timeSpent: String = "0",
    val totalPercentage: Float = 0f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val domainId: Int? = null
)

class DomainViewModel(private val domainScreenUseCases: DomainScreenUseCases) : ViewModel() {
    val allDomains = domainScreenUseCases.allDomains
    private var uiState = mutableStateOf(DomainUiState())
    val uiStateValue: State<DomainUiState> = uiState

    // Add domain screen state
    private val _addDomainScreenState = MutableStateFlow(AddDomainScreenState())
    val addDomainScreenState: StateFlow<AddDomainScreenState> = _addDomainScreenState

    // Initialize or update addDomainScreenState based on domainId
    fun initializeAddDomainScreen(domainId: Int?, domains: List<DomainEntity>) {
        viewModelScope.launch {
            val domain = domains.find { it.id == domainId }
            _addDomainScreenState.value = AddDomainScreenState(
                name = domain?.name ?: "",
                description = domain?.description ?: "None",
                monthlyTarget = domain?.monthlyTarget ?: "None",
                expectedPercentage = domain?.expectedPercentage?.toString() ?: "0",
                timeSpent = domain?.timeSpent?.toString() ?: "0",
                domainId = domainId
            )

            // Also update selectedColor in uiState
            domain?.let {
                uiState.value = uiState.value.copy(
                    selectedDomain = domain,
//                    selectedColor = CalendarColor(domain.color)
                )
            }

            calculateTotalPercentage(domains)
        }
    }

    // Update functions for each field
    fun updateName(name: String) {
        _addDomainScreenState.value = _addDomainScreenState.value.copy(name = name)
    }

    fun updateDescription(description: String) {
        _addDomainScreenState.value = _addDomainScreenState.value.copy(description = description)
    }

    fun updateMonthlyTarget(monthlyTarget: String) {
        _addDomainScreenState.value = _addDomainScreenState.value.copy(monthlyTarget = monthlyTarget)
    }

    fun updateExpectedPercentage(percentage: String, domains: List<DomainEntity>) {
        _addDomainScreenState.value = _addDomainScreenState.value.copy(expectedPercentage = percentage)
        calculateTotalPercentage(domains)
    }

    fun updateTimeSpent(timeSpent: String) {
        _addDomainScreenState.value = _addDomainScreenState.value.copy(timeSpent = timeSpent)
    }

    private fun calculateTotalPercentage(domains: List<DomainEntity>) {
        val currentState = _addDomainScreenState.value
        val expectedPercent = currentState.expectedPercentage.toFloatOrNull() ?: 0f
        val totalPercent = getTotalExpectedPercentSum(domains, expectedPercent)
        _addDomainScreenState.value = currentState.copy(totalPercentage = totalPercent)
    }

    fun saveDomain(context: Context): Boolean {
        val state = _addDomainScreenState.value

        if (!validateDetails(
                context,
                state.name,
                state.description,
                state.monthlyTarget,
                state.expectedPercentage,
                state.timeSpent
            )
        ) {
            return false
        }

        viewModelScope.launch {
            val domainId = state.domainId
            if (domainId == null) {
                // Add new domain
                domainScreenUseCases.insertNewDomain(
                    name = state.name,
                    description = state.description,
                    monthlyTarget = state.monthlyTarget,
                    expectedPercent = state.expectedPercentage.toFloat(),
                    timeSpent = state.timeSpent.toLong(),
                    color = uiState.value.selectedColor.color
                )
            } else {
                // Update existing domain
                val oldDomain = uiState.value.selectedDomain ?: return@launch
                domainScreenUseCases.updateDomainDetails(
                    name = state.name,
                    description = state.description,
                    monthlyTarget = state.monthlyTarget,
                    expectedPercent = state.expectedPercentage.toFloat(),
                    oldDomainEntity = oldDomain,
                    timeSpent = state.timeSpent.toLong(),
                    color = uiState.value.selectedColor.color
                )
            }
        }
        return true
    }

    fun addTimeInMin(context: Context, timeStr: String): Boolean {
        val time = timeStr.toIntOrNull()
        val domainEntity = uiState.value.selectedDomain
        if (domainEntity == null) {
            showToast(context, "Select a domain to add time")
            return false
        }
        if (time == null) {
            showToast(context, "Time should be a number")
            return false
        }
        viewModelScope.launch {
            domainScreenUseCases.addTimeInMin(time, domainEntity)
        }
        return true
    }

    fun deleteDomain(domainEntity: DomainEntity) {
        viewModelScope.launch {
            domainScreenUseCases.deleteTask(domainEntity)
        }
    }

    fun getTotalExpectedPercentSum(domains: List<DomainEntity>, time: Float?): Float {
        val oldDomainPercent = uiState.value.selectedDomain?.expectedPercentage ?: 0.0f
        var totalPercent = 0.0f
        domains.forEach {
            if (it.id != 404)
                totalPercent += it.expectedPercentage
        }
        return totalPercent + (time ?: 0.0f) - oldDomainPercent
    }

    fun selectDomain(context: Context, domainEntity: DomainEntity) {
        if (uiState.value.selectedDomain == domainEntity) {
            uiState.value = uiState.value.copy(selectedDomain = null)
            return
        }
        uiState.value = uiState.value.copy(selectedDomain = domainEntity, selectedColor = (context.applicationContext as SamayApplication).calendarColors.find { it.color == domainEntity.color } ?: CalendarColor.default)
    }

    // Legacy methods kept for compatibility
    fun saveNewDomain(
        context: Context,
        name: String,
        description: String,
        monthlyTarget: String,
        expectedPercent: String,
        timeSpent: String
    ): Boolean {
        if (!validateDetails(
                context,
                name,
                description,
                monthlyTarget,
                expectedPercent,
                timeSpent
            )
        ) {
            return false
        }
        viewModelScope.launch {
            domainScreenUseCases.insertNewDomain(
                name = name,
                description = description,
                monthlyTarget = monthlyTarget,
                expectedPercent = expectedPercent.toFloat(),
                timeSpent = timeSpent.toLong(),
                color = uiState.value.selectedColor.color
            )
        }
        return true
    }

    fun updateDomain(
        context: Context,
        name: String,
        description: String,
        monthlyTarget: String,
        expectedPercent: String,
        timeSpent: String
    ): Boolean {
        val oldDomain = uiState.value.selectedDomain ?: return false
        if (!validateDetails(
                context,
                name,
                description,
                monthlyTarget,
                expectedPercent,
                timeSpent
            )
        ) {
            return false
        }
        viewModelScope.launch {
            domainScreenUseCases.updateDomainDetails(
                name = name,
                description = description,
                monthlyTarget = monthlyTarget,
                expectedPercent = expectedPercent.toFloat(),
                oldDomainEntity = oldDomain,
                timeSpent = timeSpent.toLong(),
                color = uiState.value.selectedColor.color
            )
        }
        return true
    }


    private fun validateDetails(
        context: Context,
        name: String,
        description: String,
        monthlyTarget: String,
        expectedPercent: String,
        timeSpent: String
    ): Boolean {
        if (name.isEmpty()) {
            showToast(context, "Name cannot be empty")
            return false
        }
        if (description.isEmpty()) {
            showToast(context, "Description cannot be empty")
            return false
        }
        if (monthlyTarget.isEmpty()) {
            showToast(context, "Monthly target cannot be empty")
            return false
        }
        if (name.length > 15) {
            showToast(context, "Name cannot be more than 15 characters")
            return false
        }
        if (description.length > 50) {
            showToast(context, "Description cannot be more than 50 characters")
            return false
        }
        if (monthlyTarget.length > 100) {
            showToast(context, "Monthly target cannot be more than 100 characters")
            return false
        }
        if (expectedPercent.toFloatOrNull() == null) {
            showToast(context, "Expected percentage should be a number")
            return false
        }
        if (timeSpent.toLongOrNull() == null) {
            showToast(context, "Time spent should be a number")
            return false
        }
        return true
    }

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun changeSelectedColor(calendarColor: CalendarColor) {
        uiState.value = uiState.value.copy(selectedColor = calendarColor)
    }

    fun switchVisibilityOfColorPicker() {
        uiState.value = uiState.value.copy(isColorPickerDialogVisible = !uiState.value.isColorPickerDialogVisible)
    }

    fun resetAllDomains(){
        viewModelScope.launch {
            allDomains.first().forEach {dom->
                domainScreenUseCases.updateDomainDetails(
                    name = dom.name,
                    monthlyTarget = dom.monthlyTarget,
                    description = dom.description,
                    expectedPercent = dom.expectedPercentage,
                    oldDomainEntity = dom,
                    timeSpent = 0L,
                    color = dom.color
                )
            }
        }
    }
}