package com.project.samay.presentation.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.ResponseError
import com.project.samay.domain.usecases.BackupUseCases
import com.project.samay.presentation.BaseViewModel
import com.project.samay.util.ProductivityColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val backupUseCases: BackupUseCases
) : BaseViewModel<SettingsUIState>() {
    override val uiState = MutableStateFlow(SettingsUIState())

    override fun createUiStateFlow(): StateFlow<SettingsUIState> =
        MutableStateFlow(SettingsUIState())

    fun backupDomainsAndHistory() {
        viewModelScope.launch {
            backupUseCases.backupDomainsAndHistory()
        }
    }

    fun fetchDomainsAndHistory() {
        viewModelScope.launch {
            backupUseCases.fetchDomainsAndHistory()
        }
    }

    fun onToggleVisibilityOfProductivityColorDialog() {
        viewModelScope.launch {
            uiState.emit(
                uiState.value.copy(
                    isColorPickerForSettingsVisible = !uiState.value.isColorPickerForSettingsVisible
                )
            )
        }
    }

    fun onSelectLevel(level: String?) {
        viewModelScope.launch {
            uiState.emit(
                uiState.value.copy(
                    selectedProductivityLevel = level
                )
            )
        }
    }

    fun onSetColor(color: CalendarColor, context: Context) {
        viewModelScope.launch {
            if (uiState.value.selectedProductivityLevel == null) {
                uiState.emit(
                    uiState.value.copy(
                        error = ResponseError.UNKNOWN.apply {
                            actualResponse = "Please select a productivity level first."
                        }
                    )
                )
                return@launch
            }
            ProductivityColors.saveCalendarColor(context, uiState.value.selectedProductivityLevel!!, color)
        }
    }
}