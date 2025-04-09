package com.project.samay.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 2, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDAO

    companion object{
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}