package com.project.samay.domain.usecases

import android.util.Log
import com.project.samay.data.repository.DomainRepository
import com.project.samay.data.repository.HistoryRepository
import com.project.samay.domain.model.HistoryEntity

class HistoryUseCases(private val historyRepository: HistoryRepository,
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
}