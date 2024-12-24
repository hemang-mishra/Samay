package com.project.samay.util.calculations

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TimeUtils {
    companion object{
        fun getHourFromMillis(timeInMillis: Long): Int {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.timeInMillis = timeInMillis
            return calendar.get(Calendar.HOUR_OF_DAY)
        }

        fun getMinutesFromMillis(timeInMillis: Long): Int {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.timeInMillis = timeInMillis
            return calendar.get(Calendar.MINUTE)
        }

        fun getRelativeTimeDescription(timeInMillis: Long): String {
            val currentTimeMillis = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = currentTimeMillis

            val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val currentYear = calendar.get(Calendar.YEAR)

            calendar.timeInMillis = timeInMillis
            val eventDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val eventYear = calendar.get(Calendar.YEAR)

            return when {
                currentYear == eventYear && currentDayOfYear == eventDayOfYear -> "Today"
                currentYear == eventYear && currentDayOfYear - eventDayOfYear == 1 -> "Yesterday"
                else -> {
                    val daysAgo = (currentTimeMillis - timeInMillis) / (1000 * 60 * 60 * 24)
                    "$daysAgo days ago"
                }
            }
        }

        fun addMinutesToMillis(currentTimeMillis: Long, timeInMinutes: Int): Long {
            // Convert timeInMinutes to milliseconds
            val timeInMillis = timeInMinutes * 60 * 1000L
            // Add the timeInMillis to currentTimeMillis
            return currentTimeMillis + timeInMillis
        }

        fun convertToMillis(hourOfDay: Int, minuteOfHour: Int): Long {
            val calendar = Calendar.getInstance(TimeZone.getDefault())

            // Set the current time in the calendar
            calendar.timeInMillis = System.currentTimeMillis()

            // Update the hour and minute while keeping the rest the same
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minuteOfHour)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Return the time in milliseconds
            return calendar.timeInMillis
        }

        fun convertMillisToString(timeInMillis: Long): String {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.timeInMillis = timeInMillis
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            return String.format(null,"%02d:%02d", hour, minute)
        }

        fun getTimeInMillisOfDaysAgo(daysAgo: Int): Long{
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            return calendar.timeInMillis
        }

        fun covertMillisToNumberOfDaysAgo(timeInMillis: Long): Int {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - timeInMillis
            return (diff / (1000 * 60 * 60 * 24)).toInt()
        }

        fun convertMillisToDate(currentTimeMillis: Long): String {
            val date = Date(currentTimeMillis)
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            return dateFormat.format(date)
        }

        fun convertTimeDurationToHoursAndMinutes(timeInMillis: Long): String {
            val totalMinutes = timeInMillis / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return "${hours}h ${minutes}min"
        }
    }
}