package com.project.samay.domain.util

import com.project.samay.domain.model.CalendarEvent



object CalendarTrackerUtil {

    private const val START = 0
    private const val END = 1

    fun fetchEmptyTimeSlots(events: List<CalendarEvent>, currentTime: Long = System.currentTimeMillis()):List<Pair<Long,Long>> {
        val startRange = currentTime - Preferences.HOURS_TO_BE_TRACKED * 60 * 60 * 1000
        var filteredEvents = removeOutOfRangeEventsFromList(
            startRange = startRange,
            endRange = currentTime,
            events = events
        )
        filteredEvents = removeLongDurationEventsFromList(events = filteredEvents)
        val emptySlots = mutableListOf<Pair<Long, Long>>()
        val durationList: List<Pair<Long, Int>> = filteredEvents.map {
            Pair(it.dtstart, START)
        } + filteredEvents.map {
            Pair(it.dtend, END)
        }
        val sortedList = durationList.sortedBy { it.first }
        var count = 0
        var lastTime = startRange
        if(lastTime>sortedList[0].first){
            count = 1
            lastTime = sortedList[0].first
        }
        sortedList.forEach {
            if (it.second == START) {
                if (count == 0) {
                    emptySlots.add(Pair(lastTime, it.first))
                }
                count++
            } else {
                count--
                if (count == 0) {
                    lastTime = it.first
                }
            }
        }
        if (count == 0 && lastTime < currentTime) {
            emptySlots.add(Pair(lastTime, currentTime))
        }

        return emptySlots
        return splitEmptySlotsIntoDurationGreaterThan(
            Preferences.DURATION_OF_ONE_SLOT_IN_MINUTES,
            emptySlots
        )
    }

    private fun splitEmptySlotsIntoDurationGreaterThan(durationInMinutes:Int = Preferences.DURATION_OF_ONE_SLOT_IN_MINUTES, emptySlots: List<Pair<Long,Long>>):List<Pair<Long,Long>>{
        val newSlots = mutableListOf<Pair<Long,Long>>()
        emptySlots.forEach {
            if(((it.second - it.first) / 60000) >= durationInMinutes){
                val newSlotsNo = (it.second - it.first) / 60000 / durationInMinutes
                for(i in 0 until newSlotsNo){
                    newSlots.add(Pair(it.first + i * durationInMinutes * 60000, it.first + (i + 1) * durationInMinutes * 60000))
                }
            }
        }
        return newSlots
    }

    private fun removeLongDurationEventsFromList(durationInHours:Int = Preferences.DURATION_TO_BE_FLAGGED_AS_LONG_EVENT, events: List<CalendarEvent>): List<CalendarEvent> {
        return events.filter {
            ((it.dtend - it.dtstart) / 60000) <= durationInHours * 60
        }
    }

    private fun removeOutOfRangeEventsFromList(startRange: Long, endRange: Long, events: List<CalendarEvent>): List<CalendarEvent> {
        return events.filter {
            it.dtstart in startRange..endRange || it.dtend in startRange..endRange
        }
    }

}