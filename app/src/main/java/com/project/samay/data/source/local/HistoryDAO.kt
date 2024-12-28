package com.project.samay.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.samay.domain.model.DistinctNames
import com.project.samay.domain.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDAO {

    @Upsert
    suspend fun upsertHistory(historyEntity: HistoryEntity)

    @Delete
    suspend fun deleteHistory(historyEntity: HistoryEntity)

    @Query("SELECT * FROM HistoryEntity ORDER BY start DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT DISTINCT name,description,domainEntityId,domainColor,domainName FROM HistoryEntity")
    fun getDistinctNames(): Flow<List<DistinctNames>>
}
