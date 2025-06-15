package com.project.samay.domain.usecases

import com.project.samay.data.repository.DomainRepository
import com.project.samay.data.repository.HistoryRepository
import com.project.samay.domain.repository.BackUpRepository
import kotlinx.coroutines.flow.first

class BackupUseCases(
    private val backupRepository: BackUpRepository,
    private val historyRepository: HistoryRepository,
    private val domainRepository: DomainRepository
) {
    suspend fun exportData(): String? {
        val domains = domainRepository.allDomains.first()
        val history = historyRepository.getAllHistory().first()
        val result = backupRepository.exportData(domains, history)
        return result.data
    }

    suspend fun importData(json: String) {
        val result = backupRepository.importData(json)
        val export = result.data ?: return
        domainRepository.deleteAllDomains()
        historyRepository.deleteAllHistory()
        export.domains.forEach { domainRepository.upsertDomain(it) }
        export.history.forEach { historyRepository.upsertHistory(it) }
    }
}