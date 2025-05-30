package com.project.samay.presentation.domains

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.presentation.components.ColorItem
import com.project.samay.presentation.components.ColorPickerDialog
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel


@Serializable
data class NavAddDomainScreen(val isUpdate: Boolean = false)

@Composable
fun AddDomainScreen(viewModel: DomainViewModel = koinViewModel<DomainViewModel>(), isUpdate: Boolean, navController: NavController) {
    val uiState by viewModel.uiStateValue
    val domain = if (isUpdate) uiState.selectedDomain else null
    val allDomains by viewModel.allDomains.collectAsState(initial = emptyList())
    val context = LocalContext.current
//    val isUpdate = domain != null
    var name by remember { mutableStateOf(domain?.name ?: "") }
    var description by remember { mutableStateOf(domain?.description ?: "None") }
    var monthlyTarget by remember { mutableStateOf(domain?.monthlyTarget ?: "None") }
    var expectedPercentage by remember {
        mutableStateOf<String>(
            domain?.expectedPercentage?.toString()
                ?: "0"
        )
    }
    var timeSpent by remember {
        mutableStateOf(domain?.timeSpent?.toString() ?: "0")
    }

    Scaffold(
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                item {
                    Text(
                        text = if (isUpdate) "Update your domain" else "Insert new domain",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                }
                item {
                    Text("Enter the name of the domain:")
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name of domain") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                }
                item {
                    Text("What parameters should be fulfilled by a task of this domain:")
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                }

                item {
                    Text("Enter your vision you want to achieve:")
                    OutlinedTextField(
                        value = monthlyTarget,
                        onValueChange = { monthlyTarget = it },
                        label = { Text("Monthly Target") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                }
                item {
                    Text("Enter the expected percentage:")
                    OutlinedTextField(
                        value = expectedPercentage,
                        onValueChange = { expectedPercentage = it },
                        label = { Text("Expected Percentage") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(36.dp))
                }

                item {
                    Text("Enter the time spent:")
                    OutlinedTextField(
                        value = timeSpent,
                        onValueChange = { timeSpent = it },
                        label = { Text("Time Spent") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(36.dp))
                    Text(
                        text = "Current total percent is: ${
                            viewModel.getTotalExpectedPercentSum(
                                allDomains,
                                expectedPercentage.toFloatOrNull()
                            )
                        }"
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected color"
                        )
                        ColorItem(Color(uiState.selectedColor.color)) {
                            viewModel.switchVisibilityOfColorPicker()
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))
                }

                item {
                    Button(
                        onClick = {
                            if (viewModel.getTotalExpectedPercentSum(
                                    allDomains,
                                    expectedPercentage.toFloatOrNull()
                                ) > 100
                            ) {
                                viewModel.showToast(
                                    context,
                                    "Total percentage cannot be more than 100"
                                )
                                return@Button
                            }
                            // Handle save action
                            if (!isUpdate) {
                                if (viewModel.saveNewDomain(
                                        name = name,
                                        description = description,
                                        monthlyTarget = monthlyTarget,
                                        expectedPercent = expectedPercentage,
                                        context = context,
                                        timeSpent = timeSpent
                                    )
                                ) {
                                    navController.navigateUp()
                                }
                            } else {
                                if (viewModel.updateDomain(
                                        name = name,
                                        description = description,
                                        monthlyTarget = monthlyTarget,
                                        expectedPercent = expectedPercentage,
                                        context = context,
                                        timeSpent = timeSpent
                                    )
                                ) {
                                    navController.navigateUp()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        AnimatedVisibility(
            uiState.isColorPickerDialogVisible
        ) {
            ColorPickerDialog(onColorSelected = {
                viewModel.changeSelectedColor(it)
                viewModel.switchVisibilityOfColorPicker()
            }) {
                viewModel.switchVisibilityOfColorPicker()
            }
        }
    }
}