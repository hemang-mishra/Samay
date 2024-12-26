package com.project.samay.domain.usecases

import android.content.Context
import android.util.Log
import com.project.samay.domain.model.TaskEntity
import com.project.samay.util.calculations.TimeUtils

class FocusScreenUseCases(
    private val taskScreenUseCases: TaskScreenUseCases
) {
    val allTasks = taskScreenUseCases.allTasks

    suspend fun useTime(
        context: Context,
        time: Int,
        taskEntity: TaskEntity,
        start: Long,
        end: Long,
        target: Long
    ){
        Log.i("FocusScreenUseCases", "start: ${TimeUtils.convertMillisToString(start)} end: ${TimeUtils.convertMillisToString(end)}")
        taskScreenUseCases.useTimeInTask(
            context,
            time,
            taskEntity,
            start,
            end,
            target
        )
    }
}