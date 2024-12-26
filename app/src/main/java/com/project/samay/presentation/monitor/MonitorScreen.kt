package com.project.samay.presentation.monitor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.samay.domain.model.MonitoredApps
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.util.calculations.TimeUtils

@Composable
fun MonitorScreen(viewModel: MonitorViewModel) {
//    val viewModel: MonitorViewModel = viewModel()
    val isEmergency by viewModel.isEmergency
    val isMonitored by viewModel.areAppsMonitored
    val data by viewModel.appData.collectAsState(initial = emptyMap())
    Log.i("MonitorScreen", "isEmergency: $isEmergency, isMonitored: $isMonitored, data: ${data.map { it.key to TimeUtils.convertMillisToString(it.value) }}")
    val coroutineScope = rememberCoroutineScope()
    Column {
        TopBarMonitor(viewModel = viewModel)
        LazyColumn {
            items(MonitoredApps.entries.size) { index ->
                val app = MonitoredApps.entries[index]
                AppItem(
                    app = app,
                    progress = viewModel.getProgress(data[app] ?: 0),
                    activationTime = viewModel.getActivationTime(data[app] ?: 0),
                    isActive = isEmergency || viewModel.getProgress(data[app] ?: 0) == 1f
                ) {
                    viewModel.navigateToApp(app.packageName)
                }
                HorizontalDivider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
            }
        }
    }
}

@Composable
fun AppItem(
    app: MonitoredApps,
    progress: Float,
    activationTime: String,
    isActive: Boolean,
    onAppClick: (MonitoredApps) -> Unit
) {
    Row(
        modifier = Modifier
            .height(80.dp)
            .alpha(if (isActive) 1f else 0.3f)
            .fillMaxWidth()
            .clickable { if (isActive) onAppClick(app) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = app.iconsId), contentDescription = null,
            modifier = Modifier.size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if(app == MonitoredApps.TWITTER)Color.White else Color.Transparent)
        )
        Spacer(modifier = Modifier.width(8.dp))
        BoldItalicText(text = app.displayName, modifier = Modifier.weight(1f)
            .padding(top = 4.dp))
        if (!isActive) {
            Column(modifier = Modifier.weight(0.5f)) {
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.width(100.dp))
                BoldItalicText(text = "Available at: $activationTime", modifier = Modifier.width(100.dp))
            }
        }
    }
}

@Composable
fun TopBarMonitor(viewModel: MonitorViewModel) {
    val isEmergency by viewModel.isEmergency
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .padding(8.dp)
    ) {
//        Column(modifier = Modifier.align(Alignment.TopEnd)) {
//            BoldItalicText(text = "Emergency Mode")
//            Switch(checked = isEmergency, onCheckedChange = viewModel::toggleEmergency)
//        }
        Text(text = "Monitor Apps", modifier = Modifier.align(Alignment.BottomStart),
            style = MaterialTheme.typography.headlineLarge)
    }
}