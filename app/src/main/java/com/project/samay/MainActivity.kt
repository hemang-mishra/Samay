package com.project.samay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
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
import com.project.samay.presentation.onboarding.OnboardingScreen
import com.project.samay.presentation.onboarding.OnboardingViewModel
import com.project.samay.presentation.onboarding.PermissionsRequired
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
    private val onboardingViewModel by inject<OnboardingViewModel>()

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
        Log.i("check", "On start")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("check", "On create")
        enableEdgeToEdge()
        setContent {
            if (isBound) {
                val navController = rememberNavController()
                calendarViewModel.fetchCalenders(this@MainActivity)
                SamayTheme(darkTheme = true) {

                    NavHost(
                        navController = navController,
                        startDestination = Destinations.OnboardingScreen
                    ) {
                        composable<Destinations.OnboardingScreen> {
                            OnboardingScreen(onboardingViewModel, navController)
                        }
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

                        composable<Destinations.SettingsScreen> {
                            SettingsScreen(monitorViewModel = usageViewModel)
                        }
                    }

                }

            }
        }
    }


    override fun onStop() {
        //Unbinding the created connection
        super.onStop()
        Log.i("check", "On stop")
        unbindService(connection)
        isBound = false
    }

    override fun onResume() {
        super.onResume()
        Log.i("check", "On resume")
        refreshPermissionsStatus(onboardingViewModel)
        if(onboardingViewModel.areAllPermissionsGranted()) {
            usageViewModel.getData()
        calendarViewModel.refresh(this)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("check", "On pause")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i("check", "On destroy")

    }


    fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

    private fun refreshPermissionsStatus(onboardingViewModel: OnboardingViewModel){
        PermissionsRequired.entries.forEach {
            if(ContextCompat.checkSelfPermission(this, it.permission) == PackageManager.PERMISSION_GRANTED){
                onboardingViewModel.visiblePermissionDialogQueue.remove(it)

        }else{
            if(!onboardingViewModel.visiblePermissionDialogQueue.contains(it)){
                onboardingViewModel.visiblePermissionDialogQueue.add(it)
            }
            }
        }
    }

}
