package com.project.samay.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.samay.dataStore
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.util.Preferences.PRODUCTIVITY_LEVELS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ProductivityColors {

    /**
     * Saves a calendar color associated with a level name.
     * Format stored: "key:color" (e.g., "2:4283215696")
     */
    suspend fun saveCalendarColor(context: Context, levelName: String, color: CalendarColor) {
        val key = stringPreferencesKey(levelName)
        context.dataStore.edit { prefs ->
            prefs[key] = "${color.key}:${color.color}"
            Log.d("ProductivityColors", "Saved color for $levelName: ${color.key}:${color.color}")
        }
    }

    /**
     * Reads all productivity level colors from DataStore.
     * It loops over all PRODUCTIVITY_LEVELS and rebuilds [CalendarColor] using saved string.
     */
    fun readCalendarColors(context: Context): Flow<List<CalendarColor>> {
        val lst = context.dataStore.data.map { prefs ->
            PRODUCTIVITY_LEVELS.map { levelName ->
                val key = stringPreferencesKey(levelName)
                val str = prefs[key]
                Log.d("ProductivityColors", "Read color for $levelName: $str")
                if (str != null) {
                    val parts = str.split(":")
                    if (parts.size == 2) {
                        val k = parts[0].toIntOrNull()
                        val c = parts[1].toLongOrNull()
                        if (k != null && c != null) CalendarColor(k, c) else CalendarColor.default
                    } else CalendarColor.default
                }else
                    CalendarColor.default
            }
        }
        return lst
    }
}
