package com.project.samay.data.repository

import com.project.samay.data.source.local.HistoryDAO
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.HistoryEntity

class HistoryRepository(private val dao: HistoryDAO) {
    suspend fun addHistory(start: Long, end: Long, name: String, description: String, domainEntity: DomainEntity){
        dao.upsertHistory(HistoryEntity(
            0,
            name,
            description,
            start,
            end,
            domainEntity.id,
            domainEntity.color,
            domainEntity.name
        ))
    }

    suspend fun upsertHistory(historyEntity: HistoryEntity){
        dao.upsertHistory(historyEntity)
    }

    suspend fun deleteHistory(historyEntity: HistoryEntity){
        dao.deleteHistory(historyEntity = historyEntity)
    }

    suspend fun updateEntity(historyEntity: HistoryEntity){
        dao.upsertHistory(historyEntity)
    }

    fun getAllHistory()=dao.getAllHistory()

    fun getDistinctNames()=dao.getDistinctNames()

    suspend fun deleteAllHistory(){
        dao.deleteAllHistory()
    }
}