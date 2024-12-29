package com.project.samay

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.project.samay.data.source.local.PreferencesKeys
import com.project.samay.domain.model.CalendarColor
import com.project.samay.presentation.di.appModules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SamayApplication: Application() {
    val Context.dataStore by preferencesDataStore(name = "target")
    val Context.goalDataStore by preferencesDataStore(name= "goal_calendar")
    var calendarColors: List<CalendarColor> = emptyList()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SamayApplication)
            modules(appModules)
        }
    }

    fun readTargetFromDataStore(context: Context): Flow<Long?> {
        return context.dataStore.data.map{
            it[PreferencesKeys.TARGET_KEY]
        }
    }

    suspend fun saveTargetToDataStore(context: Context, target: Long){
        context.dataStore.edit {
            it[PreferencesKeys.TARGET_KEY] = target
        }
    }

    fun readGoalCalendarFromDataStore(context: Context): Flow<Int?>{
        return context.goalDataStore.data.map {
            it[PreferencesKeys.GOAL_CALENDER_KEY]
        }
    }

    suspend fun saveGoalCalendarToDataStore(context: Context, goalCalendar: Int){
        context.goalDataStore.edit {
            it[PreferencesKeys.GOAL_CALENDER_KEY] = goalCalendar
        }
    }
}