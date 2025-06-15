package com.project.samay.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.components.TopAppBarGoal
import com.project.samay.util.calculations.TimeUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainHistoryScreen(historyViewModel: HistoryViewModel = koinViewModel<HistoryViewModel>()) {
    val uiState by historyViewModel.historyUiState.collectAsState(HistoryScreenUIState())
    val history by uiState.historyItems.collectAsState(initial = emptyList())
    HistoryScreen(history, uiState.selectedHistory, {
        historyViewModel.deleteHistory(it)
    }) {
        historyViewModel.selectHistory(it)
    }
}

@Composable
private fun HistoryScreen(
    history: List<HistoryEntity>,
    selectedHistory: HistoryEntity?,
    deleteHistory: (HistoryEntity) -> Unit,
    onSelectHistory: (HistoryEntity) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                TopAppBarGoal("History")

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No History Yet",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your activity history will appear here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        itemsIndexed(history) { _, historyEntity ->
                            HistoryItem(
                                historyEntity = historyEntity,
                                isSelected = historyEntity == selectedHistory,
                                deleteHistory = deleteHistory,
                                onClick = onSelectHistory
                            )
                        }

                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    historyEntity: HistoryEntity,
    isSelected: Boolean,
    deleteHistory: (HistoryEntity) -> Unit,
    onClick: (HistoryEntity) -> Unit
) {
    val timeSpent = (historyEntity.end - historyEntity.start) / 60 / 1000
    val startTime = TimeUtils.convertMillisToString(historyEntity.start)
    val relativeTime = TimeUtils.getRelativeTimeDescription(historyEntity.start)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(
                elevation = if(isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick(historyEntity) },
        colors = CardDefaults.cardColors(
            containerColor = if(isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Category Color Indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(historyEntity.productivityColor))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Main Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Activity Name
                    Text(
                        text = historyEntity.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Domain Name
                    Text(
                        text = historyEntity.domainName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Time Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time Spent
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = timeSpent.toString(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " min spent",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Start Time and Relative Time
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = startTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = relativeTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isSelected,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Description: ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = historyEntity.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            onClick = { deleteHistory(historyEntity) },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Note: Replace with actual delete icon painter resource
                                // Image(
                                //     painter = painterResource(id = R.drawable.ic_delete),
                                //     contentDescription = "Delete",
                                //     modifier = Modifier.size(16.dp)
                                // )
                                // Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHistoryScreen() {
    // Uncomment and modify for preview
    // val history = listOf(
    //     HistoryEntity(
    //         1,
    //         "Work",
    //         "Work",
    //         TimeUtils.addMinutesToMillis(timeInMinutes = -180),
    //         TimeUtils.addMinutesToMillis(timeInMinutes = -120),
    //         1,
    //         CalendarColor.getRandomColor().color,
    //         "Work",
    //     )
    // )
    // HistoryScreen(history, history.get(0), {}) { }
}