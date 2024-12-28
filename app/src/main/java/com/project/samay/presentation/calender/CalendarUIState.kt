package com.project.samay.presentation.calender

import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.DistinctNames
import com.project.samay.domain.model.HistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

data class CalendarUIState(
    val selectedEvent: CalendarEvent? = null,
    val isFilterApplied: Boolean = true,
    val events: List<CalendarEvent> = emptyList(),
    val savedEvents: List<CalendarEvent> = emptyList(),
    val selectedDomain: DomainEntity? = null,
    val isDomainDialogueVisible: Boolean = false,

    val isSearchComposableVisible: Boolean = false,
    val allEmptySlots: List<Pair<Long, Long>> = emptyList(),
    val selectedEmptySlots: List<Pair<Long, Long>> = emptyList(),

    val queryText: String = "",
    val allDistinctNames: Flow<List<DistinctNames>> = flowOf(emptyList()),
    val matchingNames: List<DistinctNames> = emptyList(),
    val selectedName: DistinctNames? = null,

    val generatedHistoryToBeSaved: MutableList<HistoryEntity> = mutableListOf<HistoryEntity>(),
    val isConfirmModeActive: Boolean = false
)
