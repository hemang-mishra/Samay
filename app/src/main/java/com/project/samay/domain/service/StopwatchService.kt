package com.project.samay.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.project.samay.domain.model.TaskEntity
import com.project.samay.presentation.focus.StopwatchState
import com.project.samay.util.Constants.ACTION_SERVICE_CANCEL
import com.project.samay.util.Constants.ACTION_SERVICE_START
import com.project.samay.util.Constants.ACTION_SERVICE_STOP
import com.project.samay.util.Constants.NOTIFICATION_CHANNEL_ID
import com.project.samay.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.project.samay.util.Constants.NOTIFICATION_ID
import com.project.samay.util.Constants.STOPWATCH_STATE
import com.project.samay.util.formatTimeToStopwatchType
import com.project.samay.util.pad
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class StopwatchService(): Service() {
    private val binder = StopwatchBinder()
    private val notificationManager: NotificationManager by inject<NotificationManager>()
    private val notificationBuilder by inject<NotificationCompat.Builder>()
    private var duration: Duration = Duration.ZERO//What is the use of this duration class
    private lateinit var timer: Timer

    var seconds = mutableStateOf("00")
        private set

    var minutes = mutableStateOf("00")
        private set

    var hours = mutableStateOf("00")
        private set

    var currentState = mutableStateOf(StopwatchState.IDLE)
        private set

    var selectedTaskId = mutableStateOf<TaskEntity?>(null)

    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.getStringExtra(STOPWATCH_STATE)){
            StopwatchState.STARTED.name ->{
                setStopButton()
                startForegroundService()
                startStopwatch { h, m, s ->
                    updateNotifications(h, m, s)
                }
            }
            StopwatchState.CANCELED.name ->{
                stopStopwatch()
                cancelStopwatch()
                stopForegroundService()
            }
            StopwatchState.STOPPED.name ->{
                stopStopwatch()
                setResumeButton()
            }
        }
        intent?.action.let {
            when(it){
                ACTION_SERVICE_START->{
                    setStopButton()
                    startForegroundService()
                    startStopwatch { h, m, s ->
                        updateNotifications(h, m, s)
                    }
                }
                ACTION_SERVICE_STOP->{
                    stopStopwatch()
                    setResumeButton()
                }
                ACTION_SERVICE_CANCEL->{
                    stopStopwatch()
                    cancelStopwatch()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setStopButton(){
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Stop",
                ServiceHelper.stopPendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun setResumeButton(){
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Resume",
                ServiceHelper.stopPendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun updateNotifications(hours: String, minutes: String, seconds: String){
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTimeToStopwatchType(
                    seconds,
                    hours,
                    minutes
                )
            ).build()
        )
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService(){
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService(){
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit){
        currentState.value = StopwatchState.STARTED
        val t = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
        timer = t
    }

    private fun stopStopwatch(){
        if(this::timer.isInitialized){
            timer.cancel()
        }
        currentState.value = StopwatchState.STOPPED
    }

    private fun cancelStopwatch(){
        duration = Duration.ZERO
        updateTimeUnits()
        currentState.value = StopwatchState.IDLE
    }

    private fun updateTimeUnits(){
        duration.toComponents{ hours, minutes, seconds, _ ->
            this@StopwatchService.hours.value = hours.toInt().pad()
            this@StopwatchService.minutes.value = minutes.toInt().pad()
            this@StopwatchService.seconds.value = seconds.toInt().pad()
        }
        Log.i("StopwatchService", "Hours: ${hours.value}, Minutes: ${minutes.value}, Seconds: ${seconds.value}")
    }

    inner class StopwatchBinder: Binder(){
        fun getService() = this@StopwatchService
    }

}