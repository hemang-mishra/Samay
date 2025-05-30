package com.project.samay.domain.util

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.samay.domain.model.CalendarColor

object Preferences {
    const val NO_OF_DAYS_BEFORE = 5
    const val DURATION_TO_BE_FLAGGED_AS_LONG_EVENT = 12
    const val DURATION_OF_ONE_SLOT_IN_MINUTES = 20
    const val HOURS_TO_BE_TRACKED = 30

    val TARGET_KEY = longPreferencesKey("target_key_v2")
    val GOAL_CALENDER_KEY = intPreferencesKey("goal_calender_key")
    fun keyForLevel(level: Int) = stringPreferencesKey("calendar_color_$level")
    val PRODUCTIVITY_LEVELS: List<String> = listOf(
        "Interested & Happy",
        "Productive",
        "Neutral",
        "Distracted",
        "Wasted",
        "Unallocated"
    )

    fun getProductivityColors(): List<CalendarColor> {
        val list = mutableListOf<CalendarColor>()
        repeat(PRODUCTIVITY_LEVELS.size) {
            list.add(CalendarColor.default)
        }
        return list
    }

}