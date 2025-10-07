package com.project.samay.presentation.calender

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.DomainEntity
import com.project.samay.util.calculations.TimeUtils

@Composable
fun SleepTrackingDialog(
    sleepSlots: List<Pair<Long, Long>>,
    domains: List<DomainEntity>,
    onDismiss: () -> Unit,
    onSave: (domainId: Int, domainName: String, selectedSleepSlots: List<Pair<Long, Long>>, color: CalendarColor) -> Unit
) {
    var selectedDomainId by remember { mutableStateOf<Int?>(404) } // Default to unallocated domain
    var selectedDomainName by remember { mutableStateOf("Unallocated") }
    var selectedColor by remember { mutableStateOf(CalendarColor.default) }
    var selectedSleepSlots by remember { mutableStateOf(sleepSlots) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Track Sleep",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sleep Detected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sleep slots list with total duration
                val totalSleepDuration = selectedSleepSlots.sumOf { it.second - it.first }
                val totalHours = totalSleepDuration / (1000 * 60 * 60)
                val totalMinutes = (totalSleepDuration % (1000 * 60 * 60)) / (1000 * 60)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Total Sleep: ${totalHours}h ${totalMinutes}m",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.height(100.dp)
                        ) {
                            items(selectedSleepSlots) { slot ->
                                val startTime = TimeUtils.convertMillisToString(slot.first)
                                val endTime = TimeUtils.convertMillisToString(slot.second)
                                val date = TimeUtils.convertMillisToDate(slot.first)
                                val duration = TimeUtils.convertTimeDurationToHoursAndMinutes(slot.second - slot.first)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$startTime - $endTime ($date)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = duration,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Domain selection
                Text(
                    text = "Select Domain",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                ) {
                    items(domains) { domain ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedDomainId == domain.id,
                                    onClick = {
                                        selectedDomainId = domain.id
                                        selectedDomainName = domain.name
                                    }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDomainId == domain.id,
                                onClick = {
                                    selectedDomainId = domain.id
                                    selectedDomainName = domain.name
                                }
                            )

                            Text(
                                text = domain.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (selectedDomainId != null) {
                                onSave(selectedDomainId!!, selectedDomainName, selectedSleepSlots, selectedColor)
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Save Sleep Data")
                    }
                }
            }
        }
    }
}
