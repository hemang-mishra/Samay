package com.project.samay.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

//import kotlinx.parcelize


@Serializable
@Parcelize
@Entity
data class DomainEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val timeSpent: Long,
    val rate: Float,
    val name: String,
    val description: String,
    val monthlyTarget: String,
    val expectedPercentage: Float,
    val presentPercentage: Float,
    val color: Long
): Parcelable
