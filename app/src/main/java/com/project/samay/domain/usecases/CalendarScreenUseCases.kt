package com.project.samay.domain.usecases

import android.content.Context
import android.util.Log
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.CalendarEventStatus
import com.project.samay.domain.repository.CalendarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CalendarScreenUseCases(
    private val calendarRepository: CalendarRepository,
    private val domainScreenUseCases: DomainScreenUseCases
) {
    val savedEntries = calendarRepository.allSavedRoomEntries
    val allDomain = domainScreenUseCases.allDomains


    suspend fun fetchFilteredEventsOFLastWeek(context: Context): List<CalendarEvent> {
        val saved = savedEntries.first()
        val events = calendarRepository.fetchEventsOFLastWeek(context)
        return events.map { event ->
            val savedEvent = saved.find { it.id == event.id && it.dtstart == event.dtstart }
            if (savedEvent != null) {
                event.copy(status = savedEvent.status)
            } else {
                event
            }
        }
    }

    suspend fun refreshEntries(context: Context) {
        withContext(Dispatchers.IO) {
            val events = calendarRepository.fetchEventsOFLastWeek(context)
            val saved = savedEntries.first()
            val allEventIds = events.map {
                it.id
            }
            saved.forEach { saved ->
                if (saved.id !in allEventIds) {
                    calendarRepository.deleteEventFromDatabase(saved)
                }
            }
        }
    }

    suspend fun rejectEvent(calendarEvent: CalendarEvent) {
        val newEvent = calendarEvent.copy(status = CalendarEventStatus.REJECTED)
        calendarRepository.addEventToDatabase(newEvent)
    }

    suspend fun approveEvent(calendarEvent: CalendarEvent, domainEntity: DomainEntity) {
        Log.i("CalendarScreenUseCases", "approveEvent: $calendarEvent ${(calendarEvent.dtend - calendarEvent.dtstart) / 60000}")
        domainScreenUseCases.addTimeInMin(
            ((calendarEvent.dtend - calendarEvent.dtstart) / 60000).toInt(),
            domainEntity
        )
        val newEvent = calendarEvent.copy(status = CalendarEventStatus.ADDED)
        calendarRepository.addEventToDatabase(newEvent)
    }
}