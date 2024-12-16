package com.project.samay

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.project.samay.domain.service.StopwatchService
import com.project.samay.presentation.Destinations
import com.project.samay.presentation.HomeScreen
import com.project.samay.presentation.NavHomeScreen
import com.project.samay.presentation.backup.BackupScreen
import com.project.samay.presentation.backup.BackupScreenViewModel
import com.project.samay.presentation.calender.CalendarViewModel
import com.project.samay.presentation.calender.CalenderScreen
import com.project.samay.presentation.calender.NavCalenderScreen
import com.project.samay.presentation.domains.AddDomainScreen
import com.project.samay.presentation.domains.DomainViewModel
import com.project.samay.presentation.domains.NavAddDomainScreen
import com.project.samay.presentation.domains.NavUseDomainScreen
import com.project.samay.presentation.domains.UseDomainScreen
import com.project.samay.presentation.meditate.MeditateViewModel
import com.project.samay.presentation.meditate.MeditationMusicScreen
import com.project.samay.presentation.monitor.MonitorViewModel
import com.project.samay.presentation.settings.SettingsScreen
import com.project.samay.presentation.tasks.AddTaskScreen
import com.project.samay.presentation.tasks.NavAddTaskScreen
import com.project.samay.presentation.tasks.NavTargetScreen
import com.project.samay.presentation.tasks.NavUseTaskScreen
import com.project.samay.presentation.tasks.TargetScreen
import com.project.samay.presentation.tasks.TaskViewModel
import com.project.samay.presentation.tasks.UseTaskScreen
import com.project.samay.ui.theme.SamayTheme
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val usageViewModel by inject<MonitorViewModel>()
    private val domainViewModel by inject<DomainViewModel>()
    private val taskViewModel by inject<TaskViewModel>()
    private val calendarViewModel by inject<CalendarViewModel>()
    private val meditateViewModel by inject<MeditateViewModel>()
    private val backupViewModel by inject<BackupScreenViewModel>()
    private lateinit var tts: TextToSpeech


    private var isBound by mutableStateOf(false)
    private lateinit var stopwatchService: StopwatchService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as StopwatchService.StopwatchBinder
            stopwatchService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(
            this,
            StopwatchService::class.java,
        ).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
//
//        //Audio service setup:
//        val sessionToken = SessionToken(this, ComponentName(this, AudioService::class.java))
//        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
//        controllerFuture.addListener(

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts= TextToSpeech(this){status->
            if(status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
                tts.setPitch(1.3f)
            }

        }
        enableEdgeToEdge()
        setContent {
            if (isBound) {
                val navController = rememberNavController()
                calendarViewModel.fetchCalenders(this@MainActivity)
                SamayTheme(darkTheme = true) {

                    NavHost(
                        navController = navController,
                        startDestination = NavHomeScreen
                    ) {
                        composable<NavHomeScreen> {
                            HomeScreen(
                                domainViewModel,
                                taskViewModel,
                                calendarViewModel,
                                usageViewModel,
                                navController,
                                stopwatchService
                            )
                        }
                        composable<NavAddDomainScreen> {
                            val isUpdate = it.toRoute<NavAddDomainScreen>().isUpdate
                            AddDomainScreen(
                                viewModel = domainViewModel,
                                isUpdate = isUpdate,
                                navController = navController
                            )
                        }
                        composable<NavUseDomainScreen> {
                            UseDomainScreen(
                                viewModel = domainViewModel,
                                navController = navController
                            )
                        }
                        composable<NavTargetScreen> {
                            TargetScreen(navController = navController)
                        }
                        composable<NavAddTaskScreen> {
                            val isUpdate = it.toRoute<NavAddTaskScreen>().isUpdate
                            AddTaskScreen(
                                taskViewModel = taskViewModel,
                                isUpdate = isUpdate,
                                navController = navController
                            )
                        }

                        composable<NavUseTaskScreen> {
                            UseTaskScreen(
                                taskViewModel = taskViewModel,
                                navController = navController
                            )
                        }

                        composable<NavCalenderScreen> {
                            CalenderScreen(calendarViewModel)
                        }

                        composable<Destinations.MeditationScreen> {
                            MeditationMusicScreen(meditateViewModel = meditateViewModel)
                        }

                        composable<Destinations.BackupScreen> {
//                            BackupScreen(backupScreenViewModel = backupViewModel)
                            MyApp(tts)
                        }

                        composable<Destinations.SettingsScreen> {
                            SettingsScreen(monitorViewModel = usageViewModel)
                        }
                    }

                }

            }
        }
    }

    @Composable
    fun MyApp(tts: TextToSpeech) {
        var textToSpeak by remember { mutableStateOf("Hello, Jetpack Compose!") }

        Column {
            TextField(
                value = textToSpeak,
                onValueChange = { textToSpeak = it },
                label = { Text("Enter text to speak") }
            )
            Button(onClick = { speakText(tts, textToSpeak) }) {
                Text("Speak")
            }
        }
    }

    fun speakText(tts: TextToSpeech, text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onStop() {
        //Unbinding the created connection
        super.onStop()
        unbindService(connection)
        isBound = false
    }

    override fun onResume() {
        super.onResume()
        usageViewModel.getData()
        calendarViewModel.refresh(this)
//        backUpRepository.restoreDatabase(this)
    }

    override fun onPause() {
        super.onPause()
//        backUpRepository.backupDatabase(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

}
