package com.project.samay.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DomainEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("domainId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val taskName: String,
    val taskDescription: String,
    val rate: Float,
    val weight: Int,
    val percentageExpected: Float,
    val percentagePresent: Float,
    val timeSpentInMin: Long,
    val domainId: Int,
    val domainName: String,
    val domainColor : Long// Foreign key reference to DomainEntity
): Parcelable
