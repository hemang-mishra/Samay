package com.project.samay.data.repository


import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.project.samay.domain.model.DomainEntity
import com.project.samay.data.source.local.DomainDao
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DomainRepository(private val domainDao: DomainDao) {
    val allDomains: Flow<List<DomainEntity>> = domainDao.getAllDomains()

    suspend fun upsertDomain(domainEntity: DomainEntity) = withContext(Dispatchers.IO) {
        domainDao.upsertDomain(domainEntity)
        updateRateAndPercent()
    }

    fun getAllDomains(){
        CoroutineScope(Dispatchers.IO).launch {
            updateRateAndPercent()
        }
    }

    suspend fun deleteEntity(domainEntity: DomainEntity) = withContext(Dispatchers.IO) {
        domainDao.deleteEntity(domainEntity)
        updateRateAndPercent()
    }

    suspend fun getDomainById(domainId: Int): DomainEntity? = withContext(Dispatchers.IO) {
        domainDao.getDomainById(domainId)
    }

    fun calculateTotalTimeSpentInMin(domains: List<DomainEntity>): Long{
        var totalTime = 0L
        domains.forEach {
            totalTime += it.timeSpent.toInt()
        }
        return totalTime
    }

    private suspend fun updateRateAndPercent(){
        addDefaultDomain()
        val allDomains = allDomains.first()
        Log.i("DomainRepository", "updateRateAndPercent: $allDomains")
        val totalTime = calculateTotalTimeSpentInMin(allDomains)
        allDomains.forEach {
            val presentPercent = if(totalTime != 0L)(it.timeSpent.toInt() * 100.0f) / totalTime
            else 0.0f
            val rate = Logic.rateCalculator(presentPercent, it.expectedPercentage)
            val newDomainEntity = it.copy(rate = rate, presentPercentage = presentPercent)
            Log.i("DomainRepository", "updateRateAndPercent: $newDomainEntity")
            domainDao.upsertDomain(newDomainEntity)
        }
    }

    private suspend fun addDefaultDomain(){
        val domains = allDomains.first()
        var sum = 0.0f
        var oldDefault: DomainEntity? = null
        domains.forEach {
            if(it.id != 404)
            sum += it.expectedPercentage
            else
                oldDefault = it
        }
        Log.i("Sum", "addDefaultDomain: $sum")
        if(sum<=100) {
            val domainEntity = DomainEntity(
                id = 404,
                timeSpent = oldDefault?.timeSpent?:0,
                rate = oldDefault?.rate?:0.0f,
                name = "Unallocated",
                description = "Default",
                monthlyTarget = "This is a default deck",
                expectedPercentage = (100.0f-sum),
                presentPercentage = 0.0f,
                color = Color.Gray.toArgb().toLong()
            )
            Log.i("upserting", "addDefaultDomain: $domainEntity")
            domainDao.upsertDomain(domainEntity)
        }
    }

    suspend fun deleteAllDomains() = withContext(Dispatchers.IO) {
        domainDao.deleteAllDomains()
    }
}