package com.project.samay.presentation.domains

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object NavUseDomainScreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UseDomainScreen(viewModel: DomainViewModel = koinViewModel<DomainViewModel>(), navController: NavController) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf("") }
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
                    text = "Domain is ${viewModel.uiStateValue.value.selectedDomain?.name}",
                )
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = "Select the time spent on this domain",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 30, 45, 60, 90, 120, 150, 180).forEach { item ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { currentTime = item.toString() }
                            ,
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
                    if (viewModel.addTimeInMin(context, currentTime)) {
                        navController.navigateUp()
                    }
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}


@Composable
fun DividerWithText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
        Text(
            text = text,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

