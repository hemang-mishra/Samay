package com.project.samay.data.source.local.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.samay.domain.model.CalendarEvent

@Database(entities = [CalendarEvent::class], version = 2, exportSchema = false)
abstract class CalendarDatabase : RoomDatabase(){
    abstract fun calendarDao(): CalendarDao

    companion object{
        @Volatile
        private var INSTANCE: CalendarDatabase? = null

        fun getDatabase(context: Context): CalendarDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalendarDatabase::class.java,
                    "calendar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}