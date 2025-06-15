package com.project.samay.domain.repository

import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.domain.model.ResponseError
import com.project.samay.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class BackUpRepository() {
    suspend fun exportData(domains: List<DomainEntity>, history: List<HistoryEntity>): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val exportObject = BackupExport(domains, history)
                val jsonString = Json.encodeToString(exportObject)
                Resource.success(jsonString)
            } catch (e: Exception) {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = e.message
                })
            }
        }
    }

    suspend fun importData(jsonString: String): Resource<BackupExport> {
        return withContext(Dispatchers.IO) {
            try {
                val exportObject = Json.decodeFromString<BackupExport>(jsonString)
                Resource.success(exportObject)
            } catch (e: Exception) {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = e.message
                })
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class BackupExport(
    val domains: List<DomainEntity>,
    val history: List<HistoryEntity>
)
