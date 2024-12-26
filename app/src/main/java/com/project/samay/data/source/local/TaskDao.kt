package com.project.samay.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.project.samay.domain.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM TaskEntity ORDER BY rate DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM TaskEntity WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("DELETE FROM TaskEntity WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
}