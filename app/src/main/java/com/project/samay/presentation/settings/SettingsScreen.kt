package com.project.samay.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.project.samay.presentation.monitor.MonitorViewModel

@Composable
fun SettingsScreen(monitorViewModel: MonitorViewModel){
    val isEmergency by monitorViewModel.isEmergency
    Scaffold(
        modifier = Modifier.statusBarsPadding()
    ) {
        Box(
            Modifier.fillMaxSize()
                .padding(it)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Emergency mode", modifier = Modifier.weight(1f))
                Switch(isEmergency, onCheckedChange = monitorViewModel::toggleEmergency)
            }
        }
    }
}