package com.project.samay.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val hId: Int,
    val name: String,
    val description: String,
    val start: Long,
    val end: Long,
    val domainEntityId: Int,
    val domainColor: Long,
    val domainName: String
)
