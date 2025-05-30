package com.project.samay.domain.util

import android.util.Log
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.util.calculations.TimeUtils


object CalendarTrackerUtil {

    private const val START = 0
    private const val END = 1

    fun mergeContiguousTimeSlots(unsortedTimeSlots: List<Pair<Long, Long>>): List<Pair<Long, Long>> {
        val timeSlots = unsortedTimeSlots.sortedBy { it.first }
        val mergedSlots = mutableListOf<Pair<Long, Long>>()
        var start = timeSlots[0].first
        var end = timeSlots[0].second
        for (i in 1 until timeSlots.size) {
            if (timeSlots[i].first <= end) {
                end = timeSlots[i].second
            } else {
                mergedSlots.add(Pair(start, end))
                start = timeSlots[i].first
                end = timeSlots[i].second
            }
        }
        mergedSlots.add(Pair(start, end))
        return mergedSlots
    }

    private fun getEmptySlotsWithoutSplit(events: List<CalendarEvent>, currentTime: Long = System.currentTimeMillis()): List<Pair<Long,Long>>{
        val startRange = currentTime - Preferences.HOURS_TO_BE_TRACKED * 60 * 60 * 1000
        var filteredEvents = removeOutOfRangeEventsFromList(
            startRange = startRange,
            endRange = currentTime,
            events = events
        )
        filteredEvents = removeLongDurationEventsFromList(events = filteredEvents)
        var durationList: List<Pair<Long, Int>> = filteredEvents.map {
            Pair(it.dtstart, START)
        } + filteredEvents.map {
            Pair(it.dtend, END)
        }
        durationList = durationList + Pair(startRange, START) + Pair(startRange, END)
        durationList = durationList + Pair(currentTime, START) + Pair(currentTime, END)
        val sortedList = durationList.sortedBy { it.first + it.second}
        printTheSortedArray(sortedList)
        val emptySlots = mutableListOf<Pair<Long, Long>>()
        var count = 0
        var prev = 0L
        sortedList.forEach {
            if(it.second == START){
                if(count == 0 && prev != 0L){
                    emptySlots.add(Pair(prev, it.first))
                }
                count++
            }else{
                count--
            }
            prev = it.first
        }
        return emptySlots
    }

    fun fetchEmptyTimeSlots(events: List<CalendarEvent>, currentTime: Long = System.currentTimeMillis()):List<Pair<Long,Long>> {
        val emptySlots = getEmptySlotsWithoutSplit(events, currentTime)

        return splitEmptySlotsIntoDurationGreaterThan(
            Preferences.DURATION_OF_ONE_SLOT_IN_MINUTES,
            emptySlots
        )
    }

    fun isTimeSlotFree(start: Long, end: Long, events: List<CalendarEvent>, currentTime: Long = System.currentTimeMillis()): Boolean {
        val emptySlots = getEmptySlotsWithoutSplit(events, currentTime)
        Log.i("CalendarTrackerUtil", "Empty slots: ${emptySlots.map { "${TimeUtils.convertMillisToString(it.first)} ${TimeUtils.convertMillisToString(it.second)} Result: ${emptySlots.any { it.first <= start && it.second >= end }}" }}")
        return emptySlots.any { it.first <= start && it.second >= end }
    }

    private fun printTheSortedArray(arr: List<Pair<Long,Int>>){
        println("Printing the sorted array")
        arr.forEach {
            println(TimeUtils.getRelativeTimeDescription(it.first)+" "+TimeUtils.convertMillisToString(it.first) + " " + if(it.second == 0) "Start" else "End")
        }
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
            println("Checking for ${it.dtstart} and ${it.dtend} in $startRange and $endRange and result is ${(it.dtstart in startRange..endRange) || (it.dtend in startRange..endRange)}")
            (it.dtstart in startRange..endRange) || (it.dtend in startRange..endRange)
        }
    }

}