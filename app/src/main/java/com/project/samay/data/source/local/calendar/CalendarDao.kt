package com.project.samay.data.source.local.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.samay.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Upsert
    suspend fun upsertCalendarEvent(calendarEvent: CalendarEvent)

    @Query("SELECT * FROM CalendarEvent")
    fun getAllCalendarEvents(): Flow<List<CalendarEvent>>

    @Delete
    suspend fun deleteCalendarEvent(calendarEvent: CalendarEvent)
}