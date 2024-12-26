package com.project.samay.presentation.history

import com.project.samay.domain.model.HistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class HistoryScreenUIState(
    val historyItems: Flow<List<HistoryEntity>> = flowOf(emptyList()),
    val selectedHistory: HistoryEntity? = null


)
