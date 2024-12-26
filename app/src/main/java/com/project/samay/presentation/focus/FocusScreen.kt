package com.project.samay.presentation.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.samay.R
import com.project.samay.SamayApplication
import com.project.samay.domain.model.TaskEntity
import com.project.samay.domain.model.DEFAULT_TARGET
import com.project.samay.domain.service.StopwatchService
import org.koin.compose.koinInject


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FocusScreen(stopwatchService: StopwatchService) {
    val context = LocalContext.current.applicationContext as SamayApplication
    val hours by stopwatchService.hours
    val minutes by stopwatchService.minutes
    val seconds by stopwatchService.seconds
    val selectedTask by stopwatchService.selectedTaskId
    val currentState by stopwatchService.currentState
    val viewModel: FocusViewModel = koinInject()
    var isDialogueVisible by remember {
        mutableStateOf(false)
    }
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
//    var selectedTask by remember {
//        mutableStateOf<TaskEntity?>(null)
//    }
    val target by context.readTargetFromDataStore(context).collectAsState(initial = 15)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.clickable {
                    isDialogueVisible = true
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Label,
                    contentDescription = stringResource(
                        R.string.focus_label_icon,
                    ),
                    tint = LocalContentColor.current.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    selectedTask?.taskName ?: "Label",
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = getHeadlineText(currentState))

            Row(
                modifier = Modifier
            ) {
                AnimatedContent(targetState = hours, transitionSpec = { addAnimation() }) {
                    TimerText(text = "$it:")
                }
                AnimatedContent(targetState = minutes, transitionSpec = { addAnimation() }) {
                    TimerText(text = "$it:")
                }
                AnimatedContent(targetState = seconds, transitionSpec = { addAnimation() }) {
                    TimerText(text = it)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(targetState = currentState, label = "") {
                Row {
                    if (it == StopwatchState.STARTED) {
                        OutlinedButton(onClick = {
                            viewModel.stopButton(context)
                        }) {
                            Text(text = "Pause")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.ignoreButtonWhileRunning(context)
                            },
                        ) {
                            Text(text = "Ignore")
                        }
                    } else if (it == StopwatchState.CANCELED || it == StopwatchState.IDLE) {
                        OutlinedButton(onClick = {
                            if (selectedTask != null)
                                viewModel.startButton(context)
                            else
                                isDialogueVisible = true
                        }) {
                            if (selectedTask == null) {
                                Text(text = "Select Task")
                            } else {
                                Text(text = "Start")
                            }
                        }
                    } else {
                        OutlinedButton(onClick = {
                            viewModel.startButton(context)
                        }) {
                            Text(text = "Resume")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedButton(onClick = {
                            viewModel.ignoreButtonWhileStopped(context)
                        }) {
                            Text(text = "Ignore")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(
                onClick = {

                    viewModel.saveButton(
                        context,
                        selectedTask ?: return@FilledTonalButton,
                        hours,
                        minutes,
                        target ?: DEFAULT_TARGET
                    )

                },
                enabled = currentState == StopwatchState.STARTED || currentState == StopwatchState.STOPPED
            ) {
                Text(text = "Save")
            }

        }
        if (isDialogueVisible) {
            SelectTaskDialogue(tasks = tasks) {
                isDialogueVisible = false
                stopwatchService.selectedTaskId.value = it
            }
        }
    }
}

@Composable
private fun TimerText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

fun getHeadlineText(state: StopwatchState): String {
    return when (state) {
        StopwatchState.IDLE -> "Let's start working"
        StopwatchState.STARTED -> "Beat Distractions!"
        StopwatchState.STOPPED -> "Work paused!"
        StopwatchState.CANCELED -> "Session completed!"
    }
}

@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return (slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    )).togetherWith(slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    ))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTaskDialogue(tasks: List<TaskEntity>, onSelect: (TaskEntity?) -> Unit) {
    var selectedDialog by remember {
        mutableStateOf<TaskEntity?>(null)
    }
    BasicAlertDialog(
        onDismissRequest = { onSelect(selectedDialog) },
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            LazyColumn {
                items(tasks) { task ->
                    Text(
                        text = task.taskName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedDialog = task
                                onSelect(selectedDialog)
                            }
                            .padding(16.dp)

                    )
                }
            }
        }
    }
}