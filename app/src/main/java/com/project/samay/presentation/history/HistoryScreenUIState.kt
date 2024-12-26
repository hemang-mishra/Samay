package com.project.samay.presentation.history

import com.project.samay.domain.model.HistoryEntity

data class HistoryScreenUIState(
    val historyItems: List<HistoryEntity> = emptyList(),
    val selectedHistory: HistoryEntity? = null


)
