package com.project.samay.presentation.history

import androidx.lifecycle.ViewModel
import com.project.samay.domain.usecases.HistoryUseCases
import kotlinx.coroutines.flow.flowOf

class HistoryViewModel: ViewModel() {

    var historyUiState = flowOf(HistoryScreenUIState())
        private set
}