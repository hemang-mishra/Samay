package com.project.samay.domain.util

import android.app.usage.UsageEvents
import android.util.Log
import com.project.samay.util.calculations.TimeUtils

object UsageUtils {

    const val MASK_HOUR = 4
    const val LAST_N_DAYS = 7

    fun getEmptySleepSlots(list: List<UsageEvents.Event>): List<Pair<Long, Long>>{
        val sleepSlots = mutableListOf<Pair<Long, Long>>()
        for(i in 0 until list.size - 2){
//            Log.i("Usage utils", "$i Checking sleep slot between ${TimeUtils.convertMillisToString(list[i].timeStamp)} and ${TimeUtils.convertMillisToString(list[i + 1].timeStamp)}")
//            Log.i("Usage utils", "$i Checking sleep slot between ${list[i].timeStamp} and ${list[i + 1].timeStamp}")
            val timeList = TimeUtils.getListOfMillisOfAParticularHourOfLastNDays(MASK_HOUR, LAST_N_DAYS)
            if(timeList.any { millis->
                    millis in list[i].timeStamp..list[i + 1].timeStamp
                }) {
                // This is a sleep slot
                val startTime = TimeUtils.convertMillisToString(list[i].timeStamp)
                val endTime = TimeUtils.convertMillisToString(list[i + 1].timeStamp)
                Log.i("Usage utils", "Sleep slot found from $startTime to $endTime")
                sleepSlots.add(Pair(list[i].timeStamp, list[i + 1].timeStamp))
            }
        }
        return sleepSlots
    }
}