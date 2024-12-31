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
    suspend fun backupDomainsAndHistory(){
        backupRepository.deleteAllDomainsFromFirebase()
        backupRepository.deleteAllHistoryFromFirebase()
        val domains = domainRepository.allDomains.first()
        val history = historyRepository.getAllHistory().first()
        backupRepository.uploadDomains(domains)
        backupRepository.uploadHistory(history)
    }

    suspend fun fetchDomainsAndHistory(){
        val domains = backupRepository.fetchDomains()
        val history = backupRepository.fetchHistory()
        domainRepository.deleteAllDomains()
        val historyData = historyRepository.deleteAllHistory()
        domains.data?.let {
            it.forEach {
                domainRepository.upsertDomain(it)
            }
        }
        history.data?.let {
            it.forEach {
                historyRepository.upsertHistory(it)
            }
        }
    }
}