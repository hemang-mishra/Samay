package com.project.samay.domain.usecases

import com.project.samay.domain.model.DomainEntity
import com.project.samay.data.repository.DomainRepository
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.flow.first

class DomainScreenUseCases(
    private val domainRepository: DomainRepository) {
    init {
        initialize()
    }
    val allDomains = domainRepository.allDomains

    suspend fun insertNewDomain(name: String, description: String, monthlyTarget: String, expectedPercent: Float, timeSpent: Long){
        val domainSize = allDomains.first().size+1
        val domainEntity = DomainEntity(
            id = 0,
            timeSpent = timeSpent,
            rate = 0.0f,
            name = name,
            description = description,
            monthlyTarget = monthlyTarget,
            expectedPercentage = expectedPercent,
            presentPercentage = 0.0f,
            color = Logic.getColor(domainSize)
        )
        domainRepository.upsertDomain(domainEntity)
    }

    suspend fun updateDomainDetails(name: String, monthlyTarget: String, expectedPercent: Float,
                                    oldDomainEntity: DomainEntity, description: String, timeSpent: Long
                                    ){
        val newDomainEntity = oldDomainEntity.copy(name = name, monthlyTarget = monthlyTarget, timeSpent = timeSpent, expectedPercentage = expectedPercent, description = description)
        domainRepository.upsertDomain(newDomainEntity)
    }

    fun initialize(){
        domainRepository.getAllDomains()
    }

    suspend fun addTimeInMin(time: Int, oldDomainEntity: DomainEntity){
        val newDomainEntity = oldDomainEntity.copy(timeSpent = oldDomainEntity.timeSpent + time)
        domainRepository.upsertDomain(newDomainEntity)
    }

    suspend fun deleteTask(domainEntity: DomainEntity){
        domainRepository.deleteEntity(domainEntity)
    }

}