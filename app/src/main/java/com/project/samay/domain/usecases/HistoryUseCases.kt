package com.project.samay.domain.usecases

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.project.samay.MainActivity
import com.project.samay.SamayApplication
import com.project.samay.data.repository.DomainRepository
import com.project.samay.data.repository.HistoryRepository
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.domain.model.TaskEntity
import com.project.samay.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.first

class HistoryUseCases(private val historyRepository: HistoryRepository,
                      private val calendarRepository: CalendarRepository,
                      private val domainRepository: DomainRepository) {

    fun getHistory() = historyRepository.getAllHistory()

    //Perform delete history operation in the history screen
    suspend fun deleteHistory(historyEntity: HistoryEntity){
        historyRepository.deleteHistory(historyEntity)
        var domainEntity = domainRepository.getDomainById(historyEntity.domainEntityId)
        val timeDiff = (historyEntity.end - historyEntity.start) / 1000 / 60
        if(domainEntity == null){
            Log.e("History Use case", "DomainEntity could not be fetched with id ${historyEntity.domainEntityId}")
            return
        }
        domainEntity = domainEntity.copy(timeSpent = domainEntity.timeSpent - timeDiff)
        domainRepository.upsertDomain(domainEntity)
        historyRepository.deleteHistory(historyEntity)
    }

    //Delete all history items
    suspend fun deleteAllHistory(){
        historyRepository.deleteAllHistory()
    }

    suspend fun useTime(
        context: Context,
        historyEntity: HistoryEntity
    ) {
        val contextApp = context.applicationContext as SamayApplication
        val time = (historyEntity.end - historyEntity.start) / 1000 / 60
        val id = contextApp.readGoalCalendarFromDataStore(context).first()
        val domainEntity = domainRepository.allDomains.first().find { it.id == historyEntity.domainEntityId }
        if (domainEntity != null) {
            val newDomainEntity = domainEntity.copy(timeSpent = domainEntity.timeSpent + time)
            domainRepository.upsertDomain(newDomainEntity)
            historyRepository.addHistory(
                historyEntity.start,
                historyEntity.end,
                historyEntity.name,
                historyEntity.description,
                newDomainEntity
            )
        }

//        Log.i("Adding in calender", "$id ${taskEntity.taskName} $start $end")
        if (id != null)
            calendarRepository.addEvent(
                context,
                id.toLong(),
                historyEntity.name,
                historyEntity.description,
                historyEntity.start,
                historyEntity.end,
                contextApp.calendarColors.find { it.color == domainEntity?.color } ?: CalendarColor.default
            )
        else
            Toast.makeText(context, "Please select calender in settings to add task to calendar", Toast.LENGTH_SHORT).show()

    }
}