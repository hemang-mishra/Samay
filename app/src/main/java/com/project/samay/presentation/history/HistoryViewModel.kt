package com.project.samay.presentation.history

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.domain.usecases.HistoryUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyUseCases: HistoryUseCases): ViewModel() {

    var historyUiState: MutableStateFlow<HistoryScreenUIState> = MutableStateFlow(HistoryScreenUIState())
        private set

    init {
        historyUiState.value = historyUiState.value.copy(historyItems = historyUseCases.getHistory())
    }

    fun selectHistory(historyEntity: HistoryEntity){
        if(historyUiState.value.selectedHistory == historyEntity){
            historyUiState.value = historyUiState.value.copy(selectedHistory = null)
            return
        }
        historyUiState.value = historyUiState.value.copy(selectedHistory = historyEntity)
    }

    fun deleteHistory(historyEntity: HistoryEntity){
        viewModelScope.launch {
            historyUiState.value = historyUiState.value.copy(selectedHistory = null)
            historyUseCases.deleteHistory(historyEntity)
        }
    }
}