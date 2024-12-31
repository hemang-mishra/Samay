package com.project.samay.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.samay.domain.model.DomainEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DomainDao {
    @Upsert
    suspend fun upsertDomain(domainEntity: DomainEntity)

    @Query("SELECT * FROM DomainEntity ORDER BY rate DESC")
    fun getAllDomains(): Flow<List<DomainEntity>>

    @Delete
    suspend fun deleteEntity(domainEntity: DomainEntity)

    @Query("SELECT * FROM DomainEntity WHERE id = :domainId")
    suspend fun getDomainById(domainId: Int): DomainEntity?

    @Query("DELETE FROM DomainEntity")
    suspend fun deleteAllDomains()
}