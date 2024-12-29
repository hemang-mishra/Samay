package com.project.samay.domain.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import androidx.core.graphics.toColorLong
import com.project.samay.SamayApplication
import com.project.samay.data.source.local.calendar.CalendarDao
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.CalendarType
import com.project.samay.domain.util.Preferences.NO_OF_DAYS_BEFORE
import com.project.samay.util.calculations.TimeUtils
import kotlinx.coroutines.flow.first
import java.util.TimeZone

class CalendarRepository(private val calendarDao: CalendarDao) {
    val allSavedRoomEntries = calendarDao.getAllCalendarEvents()

    fun addEvent(context: Context,calenderId: Long, title: String, description: String, startTime: Long, endTime: Long, calendarColor: CalendarColor) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calenderId)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.EVENT_COLOR, calendarColor.color.toString())
            put(CalendarContract.Events.EVENT_COLOR_KEY, calendarColor.key)

        }
        Log.i("CalendarRepository", "addEvent: $values")
        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }
//    CalendarContract.Events

    suspend fun fetchColors(context: Context): List<CalendarColor>{
        val currentCalendar = (context.applicationContext as SamayApplication).readGoalCalendarFromDataStore(context)

        if(currentCalendar.first() == null){
            Log.i("CalendarRepository", "fetchColors: No calendar found")
            return emptyList()
        }
        val accountName = getAccountNameFromCalendarId(context, currentCalendar.first()!!.toLong())
        if(accountName == null){
            Log.i("CalendarRepository", "fetchColors: No account name found")
            return emptyList()
        }
        Log.i("CalendarRepository", "accountName: $accountName")
        val cursor = context.contentResolver.query(
            CalendarContract.Colors.CONTENT_URI,
            arrayOf(
                CalendarContract.Colors.COLOR_KEY,
                CalendarContract.Colors.COLOR
            ),
            "${CalendarContract.Colors.ACCOUNT_NAME} = ? AND ${CalendarContract.Colors.COLOR_TYPE} = ?",
            arrayOf(accountName,1.toString()),
            null
        )
        cursor?.use {
            val colors = mutableListOf<CalendarColor>()
            while (it.moveToNext()) {
                colors.add(
                    CalendarColor(
                        it.getInt(it.getColumnIndexOrThrow(CalendarContract.Colors.COLOR_KEY)),
                        it.getInt(it.getColumnIndexOrThrow(CalendarContract.Colors.COLOR)).toLong()
                    )
                )
            }
                Log.i("CalendarRepository", "fetchColors: $colors")
            return colors
        }
        return emptyList()
    }

    fun fetchCalendars(context: Context): List<CalendarType>{

        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE
            ),
            null,
            null,
            null
        )
        cursor?.use {
            val calenders = mutableListOf<CalendarType>()
            while (it.moveToNext()) {
                calenders.add(
                    CalendarType(
                        it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID)),
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)),
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)),
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE))
                    )
                )

            }
            return calenders
        }
        return emptyList()
    }

    fun fetchEventsOFLastWeek(context: Context, noOfDaysBefore: Int = NO_OF_DAYS_BEFORE): List<CalendarEvent> {
        val timeInMillisOfDaysAgo = TimeUtils.getTimeInMillisOfDaysAgo(noOfDaysBefore)
        val endTimeInMillis = System.currentTimeMillis()

        // Define the URI for the Instances table and specify the time range
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, timeInMillisOfDaysAgo)
        ContentUris.appendId(builder, endTimeInMillis)
        val instancesUri = builder.build()

        val selection = "${CalendarContract.Instances.BEGIN} >= ? AND ${CalendarContract.Instances.END} <= ?"
        val selectionArgs = arrayOf(timeInMillisOfDaysAgo.toString(), endTimeInMillis.toString())

        // Query the Instances table to get both normal and recurring events
        val cursor = context.contentResolver.query(
            instancesUri,
            arrayOf(
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DESCRIPTION,
                CalendarContract.Instances.CALENDAR_COLOR,
                CalendarContract.Instances.CALENDAR_ID
            ),
            selection,
            selectionArgs,
            "${CalendarContract.Instances.END} DESC"
        )

        cursor?.use {
            val events = mutableListOf<CalendarEvent>()
            while (it.moveToNext()) {
                events.add(
                    CalendarEvent(
                        it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)),
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_COLOR)) ?: "", // Handle null
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)) ?: "", // Handle null
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)) ?: "", // Handle null
                        it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)),
                        it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.END)),
                        it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID))
                    )
                )
            }
            Log.i("CalendarRepository", "fetchEventsOfLastWeek: $events")
            return events
        }

        return emptyList()
    }


    private fun getAccountNameFromCalendarId(context: Context, calendarId: Long): String? {
        val projection = arrayOf(CalendarContract.Calendars.ACCOUNT_NAME)
        val selection = "${CalendarContract.Calendars._ID} = ?"
        val selectionArgs = arrayOf(calendarId.toString())

        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME))
            }
        }
        return null
    }


    suspend fun addEventToDatabase(calendarEvent: CalendarEvent){
        calendarDao.upsertCalendarEvent(calendarEvent)
    }

    suspend fun deleteEventFromDatabase(calendarEvent: CalendarEvent){
        calendarDao.deleteCalendarEvent(calendarEvent)
    }

}