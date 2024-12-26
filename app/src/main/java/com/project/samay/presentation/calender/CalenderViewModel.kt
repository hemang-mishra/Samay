package com.project.samay.presentation.calender

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.CalendarEventStatus
import com.project.samay.domain.model.CalendarType
import com.project.samay.domain.repository.CalendarRepository
import com.project.samay.domain.usecases.CalendarScreenUseCases
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val calendarScreenUseCases: CalendarScreenUseCases
) : ViewModel() {
    private var _calendarsList = mutableStateOf(emptyList<CalendarType>())
    val calendarType: State<List<CalendarType>> = _calendarsList
    val allDomains = calendarScreenUseCases.allDomain


    private var _calendarUIState = mutableStateOf(CalendarUIState())
    val calendarUIState: State<CalendarUIState> = _calendarUIState

    fun switchVisibilityOfDialogue() {
        _calendarUIState.value =
            _calendarUIState.value.copy(isDomainDialogueVisible = !_calendarUIState.value.isDomainDialogueVisible)
    }

    fun refresh(context: Context) {
        viewModelScope.launch {
            _calendarUIState.value = _calendarUIState.value.copy(
                events = calendarScreenUseCases.fetchFilteredEventsOFLastWeek(context),
                savedEvents = calendarScreenUseCases.savedEntries.first()
            )
        }
    }

    fun rejectEvent(context: Context) {
        if (_calendarUIState.value.selectedEvent == null) {
            Toast.makeText(context, "Select an event to reject", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            calendarScreenUseCases.rejectEvent(_calendarUIState.value.selectedEvent!!)
        }
        selectEvent(null)
        refresh(context)
    }

    fun approveEvent(context: Context) {
        if (_calendarUIState.value.selectedEvent == null || _calendarUIState.value.selectedDomain == null) {
            Toast.makeText(context, "Select an event and a domain to approve", Toast.LENGTH_SHORT)
                .show()
            return
        }
        viewModelScope.launch {
            calendarScreenUseCases.approveEvent(
                _calendarUIState.value.selectedEvent!!,
                _calendarUIState.value.selectedDomain!!
            )
            selectEvent(null)
            selectDomain(null)
            refresh(context)
        }
    }

    fun selectDomain(domainEntity: DomainEntity?) {
        _calendarUIState.value = _calendarUIState.value.copy(selectedDomain = domainEntity)
    }

    fun selectEvent(calendarEvent: CalendarEvent?) {
        _calendarUIState.value = _calendarUIState.value.copy(selectedEvent = calendarEvent)
    }

    fun toggleFilter(boolean: Boolean) {
        _calendarUIState.value = _calendarUIState.value.copy(isFilterApplied = boolean)
    }

    fun validate(calendarEvent: CalendarEvent, calId: Long?): Boolean {
        if (calendarEvent.calendarId == calId && _calendarUIState.value.isFilterApplied) {
            return false
        }
        if (calendarEvent.status == CalendarEventStatus.REJECTED || calendarEvent.status == CalendarEventStatus.ADDED) {
            return false
        }
        val saved = _calendarUIState.value.savedEvents
        if (saved.find { it.id == calendarEvent.id && it.dtstart == calendarEvent.dtstart } != null) {
            return false
        }
        return true
    }

    fun getCalenderAtIndex(index: Int?): CalendarType? {
        if (index == null)
            return null
        if (index < _calendarsList.value.size) {
            return _calendarsList.value[index - 1]
        }
        return null
    }

    fun fetchCalenders(context: Context) {
        viewModelScope.launch {
            _calendarsList.value = calendarRepository.fetchCalendars(context)
        }
    }
}