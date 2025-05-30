package com.project.samay.presentation.tasks

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.SamayApplication
import com.project.samay.domain.model.DomainEntity
import com.project.samay.domain.model.DEFAULT_TARGET
import com.project.samay.util.calculations.Logic
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class NavAddTaskScreen(val isUpdate: Boolean = false)


@Composable
fun AddTaskScreen(taskViewModel: TaskViewModel = koinViewModel<TaskViewModel>(), isUpdate: Boolean, navController: NavController) {
    val context = LocalContext.current.applicationContext as SamayApplication
    val target by context.readTargetFromDataStore(context).collectAsState(initial = DEFAULT_TARGET.toLong())
    val task = if (isUpdate) taskViewModel.uiState.value.currentTask else null
    val allDomain by taskViewModel.domains.collectAsState(initial = emptyList())
    val allTasks by taskViewModel.tasks.collectAsState(initial = emptyList())
//    val isUpdate = domain != null
    var name by remember { mutableStateOf(task?.taskName ?: "") }
    var description by remember { mutableStateOf(task?.taskDescription ?: "") }
    val domain = allDomain.find {
        it.id == task?.domainId
    }
    Log.i("AddTaskScreen", "domain: $domain")
    var selectedDomain by remember {
        mutableStateOf<DomainEntity?>(allDomain.find {
            it.id == task?.domainId
        })
    }
    if(selectedDomain == null){
        selectedDomain = domain
    }
    var weight by remember {
        mutableStateOf<String>(
            task?.weight?.toString()
                ?: "1"
        )
    }
    var isDialogueVisible by remember { mutableStateOf(false) }
    Scaffold(
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = if (isUpdate) "Update your task" else "Insert new task",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(36.dp))
                Text("Enter the name of the task:")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name of task") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(36.dp))

                Text("What you aim to achieve in this task:")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(36.dp))

                Text("Enter your vision you want to achieve:")

                Box(modifier = Modifier.clickable {
                    isDialogueVisible = true
                }) {
                    Text(
                        text = selectedDomain?.name ?: "Select Domain",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(36.dp))
                Text(text = "Total weight till now: ${taskViewModel.getTotalWeightSum(allTasks, weight.toIntOrNull())}")
                Text(
                    text = "You will have to spend: ${
                        Logic.formatInHrsAndMins(
                            taskViewModel.getTimeFromWeight(
                                allTasks,
                                weight.toIntOrNull(),
                                target ?: DEFAULT_TARGET.toLong()
                            )
                        )
                    }"
                )

                Spacer(modifier = Modifier.height(36.dp))

                Text("Enter priority for time allocation to this task:")
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = {
                        if (selectedDomain == null) {
                            selectedDomain = allDomain.find {
                                it.id == 404
                            } ?: return@Button
                        }
                        // Handle save action
                        if (!isUpdate) {
                            if (taskViewModel.saveNewTask(
                                    name = name,
                                    description = description,
                                    weight = weight,
                                    context = context,
                                    domainEntity = selectedDomain!!
                                )
                            ) {
                                navController.navigateUp()
                            }
                        } else {
                            if (taskViewModel.update(
                                    name = name,
                                    description = description,
                                    weight = weight,
                                    context = context,
                                    domainEntity = selectedDomain!!
                                )
                            ) {
                                navController.navigateUp()
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Save")
                }
            }
        }
        AnimatedVisibility(visible = isDialogueVisible) {
            SelectDomainDialogue(domains = allDomain) { domainEntity ->
                selectedDomain = domainEntity
                isDialogueVisible = false
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDomainDialogue(domains: List<DomainEntity>, onSelect: (DomainEntity?) -> Unit) {
    var selectedDialog by remember {
        mutableStateOf<DomainEntity?>(null)
    }
    BasicAlertDialog(onDismissRequest = { onSelect(selectedDialog) },
        modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
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
                items(domains) { domain ->
                    Text(
                        text = domain.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedDialog = domain
                                onSelect(selectedDialog)
                            }
                            .padding(16.dp)

                    )
                }
            }
        }
    }
}