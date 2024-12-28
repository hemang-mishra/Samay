package com.project.samay.domain.util

import com.project.samay.domain.model.CalendarEvent
import com.project.samay.util.calculations.TimeUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarTrackerUtilTest {

    @Test
    fun testMergeContiguousTimeSlots() {
        val timeSlots = listOf(
            Pair(1L, 5L),
            Pair(5L, 10L),
            Pair(14L, 15L)
        )

        val expectedMergedSlots = listOf(
            Pair(1L, 10L),
            Pair(14L, 15L)
        )

        val actualMergedSlots = CalendarTrackerUtil.mergeContiguousTimeSlots(timeSlots)

        assertEquals(expectedMergedSlots, actualMergedSlots)
    }


    @Test
    fun testFetchEmptyTimeSlots() {
        val currentTime = System.currentTimeMillis()
        val events = listOf(
            CalendarEvent(id = 1, color = "red", title = "Event 1", description = "Description 1", dtstart = TimeUtils.addMinutesToMillis(currentTime,-3*60), dtend = TimeUtils.addMinutesToMillis(currentTime,-1*60), calendarId = 1),
            CalendarEvent(id = 1, color = "red", title = "Event 1", description = "Description 1", dtstart = TimeUtils.addMinutesToMillis(currentTime,-2*60), dtend = TimeUtils.addMinutesToMillis(currentTime,-1*60), calendarId = 1),
            CalendarEvent(id = 1, color = "red", title = "Event 1", description = "Description 1", dtstart = TimeUtils.addMinutesToMillis(currentTime,-3*60), dtend = TimeUtils.addMinutesToMillis(currentTime,-2*60), calendarId = 1),
            CalendarEvent(id = 1, color = "red", title = "Event 1", description = "Description 1", dtstart = TimeUtils.addMinutesToMillis(currentTime,-40*60), dtend = TimeUtils.addMinutesToMillis(currentTime,-5*60-1), calendarId = 1),
            CalendarEvent(id = 2, color = "blue", title = "Event 2", description = "Description 2", dtstart = currentTime + 1800000, dtend = currentTime + 3600000, calendarId = 1)
        )

        val expectedEmptySlots = listOf(
//            Pair(TimeUtils.addMinutesToMillis(currentTime,-Preferences.HOURS_TO_BE_TRACKED*60), TimeUtils.addMinutesToMillis(currentTime,-15*60)),
            Pair(TimeUtils.addMinutesToMillis(currentTime,-12*60), TimeUtils.addMinutesToMillis(currentTime,-3*60)),
            Pair(TimeUtils.addMinutesToMillis(currentTime,-1*60), currentTime)
        )

        val actualEmptySlots = CalendarTrackerUtil.fetchEmptyTimeSlots(events,currentTime)

        assertEquals(expectedEmptySlots.map {
            Pair(TimeUtils.convertMillisToDate(it.first)+" "+TimeUtils.convertMillisToString(it.first),TimeUtils.convertMillisToDate(it.second)+" "+ TimeUtils.convertMillisToString(it.second))
        }, actualEmptySlots.map {
            Pair(TimeUtils.convertMillisToDate(it.first)+" "+TimeUtils.convertMillisToString(it.first),TimeUtils.convertMillisToDate(it.second)+" "+ TimeUtils.convertMillisToString(it.second))
        })
    }
}