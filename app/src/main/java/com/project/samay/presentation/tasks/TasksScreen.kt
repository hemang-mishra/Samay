package com.project.samay.presentation.tasks

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.SamayApplication
import com.project.samay.domain.model.TaskEntity
import com.project.samay.domain.model.DEFAULT_TARGET
import com.project.samay.presentation.domains.BoldItalicText
import com.project.samay.presentation.domains.TopAppBarGoal
import com.project.samay.util.calculations.Logic

@Composable
fun TasksScreen(taskViewModel: TaskViewModel, navController: NavController) {
    val context = LocalContext.current.applicationContext as SamayApplication
    val target by context.readTargetFromDataStore(context).collectAsState(initial = DEFAULT_TARGET.toLong())
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())
    val uiState by taskViewModel.uiState
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarGoal(text = "Tasks")
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(tasks) { _, it ->
                    TaskItem(task = it, isSelected = uiState.currentTask == it, viewModel = taskViewModel, target = target?: DEFAULT_TARGET.toLong(), navController = navController){
                        taskViewModel.selectTask(it)
                    }
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth(),
                         horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = "Total Weight is ${taskViewModel.getTotalWeightSum(tasks, null)}")
                        BoldItalicText(text = "You worked for ${taskViewModel.getPresentTimeSpentSum(tasks)}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Present target is ${Logic.formatInHrsAndMins(target?: DEFAULT_TARGET)}")
                        Row {
                            TextButton(onClick = {
                                navController.navigate(NavTargetScreen)
                            }) {
                                Text(text = "Click Here to edit")
                            }
                        }
                        TextButton(onClick = taskViewModel::reset) {
                            Text("Reset")
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                navController.navigate(NavAddTaskScreen(false))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = null,
            )
        }
    }
}

@Composable
fun TaskItem(task: TaskEntity, isSelected: Boolean, viewModel: TaskViewModel, navController: NavController, target: Long, onClick: ()->Unit) {
    val context = LocalContext.current.applicationContext as SamayApplication
    Column(
        modifier = Modifier
            .animateContentSize()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.TaskAlt, contentDescription = null,
                tint = Color(task.domainColor),
                modifier = Modifier
                    .align(Alignment.Top)
                    .width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                BoldItalicText(text = task.taskName, fontSize = 24)
                Text(text = task.domainName)
            }
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier) {
                    Text(text = "${task.weight}",
                        style = MaterialTheme.typography.bodyMedium
                        )
                }
            }
            Column(modifier = Modifier
                .weight(0.3f)) {
                Row {
                    Text(text = Logic.formatInHrsAndMins(task.timeSpentInMin))
//                    BoldItalicText(
//                        text = " of",
//                        modifier = Modifier.align(Alignment.CenterVertically)
//                    )
                }
                HorizontalDivider(modifier = Modifier.width(36.dp))
                Text(text = Logic.formatInHrsAndMins(Logic.calculateTargetTime(target,task.percentageExpected)))
            }

            IconButton(onClick = {
                viewModel.selectTask(task)
                navController.navigate(NavUseTaskScreen)
            }) {
                Icon(imageVector = Icons.Filled.AlarmAdd, contentDescription = null)
            }
        }
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                BoldItalicText(
                    text = "Description: ",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(text = task.taskDescription)
            }
            Row(modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = {
                    viewModel.taskCompleted(application = context, task)
                }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null)
                }
                IconButton(onClick = {
                    navController.navigate(NavAddTaskScreen(true))
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = {
                    viewModel.deleteTask(task)
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
    HorizontalDivider()

}