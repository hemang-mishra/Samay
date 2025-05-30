package com.project.samay.presentation.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.SamayApplication
import com.project.samay.presentation.domains.DividerWithText
import com.project.samay.util.calculations.TimeUtils
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime

@Serializable
object NavUseTaskScreen

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UseTaskScreen(taskViewModel: TaskViewModel = koinViewModel<TaskViewModel>(), navController: NavController) {
    val context = LocalContext.current.applicationContext
    val target by (context as SamayApplication).readTargetFromDataStore(context).collectAsState(
        initial = 15
    )
    var currentTime by remember { mutableStateOf("") }
    val uiState by taskViewModel.uiState
    var isStartTimeDialgueVisible by remember { mutableStateOf(false) }
    var startTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    var endTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    val startTimeDialogueState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )
    val endTimeDialogueState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )
    var isEndTimeDialogueVisible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = currentTime) {
        if(currentTime.toIntOrNull() == null) {
            startTime = endTime
        }else{
            startTime = TimeUtils.addMinutesToMillis(endTime, -currentTime.toInt())
        }
    }
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add Time Spent",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = "Task is ${uiState.currentTask?.taskName}",
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(text = "Domain is ${uiState.currentTask?.domainName}")
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = "Select the time spent on this domain",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(onClick = {
                            isStartTimeDialgueVisible = true
                        }) {
                            Text(text = "Start Time")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = TimeUtils.convertMillisToString(startTime))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(onClick = {
                            isEndTimeDialogueVisible = true
                        }) {
                            Text(text = "End Time")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = TimeUtils.convertMillisToString(endTime))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 30, 45, 60, 90, 120, 150, 180).forEach { item ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { currentTime = item.toString() },
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentTime.toIntOrNull() == item) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(text = "$item min")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                DividerWithText(text = "or")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = currentTime, onValueChange = { currentTime = it },
                    label = {
                        Text(text = "Enter custom time")
                    })
                Button(onClick = {
                    if (taskViewModel.addTimeInMin(context, currentTime, startTime, endTime, target?:15)) {
                        navController.navigateUp()
                    }
                }) {
                    Text(text = "Save")
                }
            }

            if (isStartTimeDialgueVisible) {
                TimePickerDialog(timePickerState = startTimeDialogueState) {
                    startTime = TimeUtils.convertToMillis(startTimeDialogueState.hour, startTimeDialogueState.minute)
                    if(currentTime.toIntOrNull() == null){
                        endTime = startTime
                    }else{
                        endTime = TimeUtils.addMinutesToMillis(startTime, currentTime.toInt())
                    }
                    isStartTimeDialgueVisible = false
                }
            }

            if (isEndTimeDialogueVisible) {
                TimePickerDialog(timePickerState = endTimeDialogueState) {
                    endTime = TimeUtils.convertToMillis(endTimeDialogueState.hour, endTimeDialogueState.minute)
                    if(currentTime.toIntOrNull() == null) {
                        startTime = endTime
                    }else{
                        startTime = TimeUtils.addMinutesToMillis(endTime, -currentTime.toInt())
                    }
                    isEndTimeDialogueVisible = false
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    timePickerState: TimePickerState,
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = { onDismissRequest() }) {
        TimePicker(
            state = timePickerState
        )
    }
}