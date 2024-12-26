package com.project.samay.presentation.calender

import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.CalendarEvent

data class CalendarUIState(
    val selectedEvent: CalendarEvent? = null,
    val isFilterApplied: Boolean = true,
    val events: List<CalendarEvent> = emptyList(),
    val savedEvents: List<CalendarEvent> = emptyList(),
    val selectedDomain: DomainEntity? = null,
    val isDomainDialogueVisible: Boolean = false
)
