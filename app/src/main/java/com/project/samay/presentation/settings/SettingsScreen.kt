package com.project.samay.presentation.settings

import ProductivityComposable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.samay.SamayApplication
import com.project.samay.domain.util.Preferences
import com.project.samay.presentation.calender.CalendarViewModel
import com.project.samay.presentation.calender.CalenderDialogue
import com.project.samay.presentation.components.ColorPickerDialog
import com.project.samay.presentation.components.TopAppBarGoal
import com.project.samay.presentation.monitor.MonitorViewModel
import com.project.samay.ui.theme.spacing
import com.project.samay.util.ProductivityColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    monitorViewModel: MonitorViewModel,
    calendarViewModel: CalendarViewModel,
    settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
) {
    val calenders by calendarViewModel.calendarType
    val context = LocalContext.current.applicationContext as SamayApplication
    val selectedCalendarIndex by context.readGoalCalendarFromDataStore(context)
        .collectAsState(initial = null)
    val calendarUIState by calendarViewModel.calendarUIState
    val isEmergency by monitorViewModel.isEmergency
    var isSelectDialogueVisible by remember { mutableStateOf(false) }
    val uiState by settingsViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

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
                TopAppBarGoal("Settings")

                PrimarySettingsScreen(
                    emergencyMode = isEmergency,
                    onEmergencyModeChange = monitorViewModel::toggleEmergency,
                    filterResults = calendarUIState.isFilterApplied,
                    onFilterResultsChange = calendarViewModel::toggleFilter,
                    selectedCalendar = calendarViewModel.getCalenderAtIndex(
                        selectedCalendarIndex
                    )?.displayName ?: "None",
                    onSelectCalendarClick = {
                        isSelectDialogueVisible = true
                    },
                    onBackUpButtonClick = {
                        settingsViewModel.exportData()
                    },
                    onRestoreButtonClick = {
                        settingsViewModel.showImportDialog()
                    },
                    onClickLevel = {
                        settingsViewModel.onSelectLevel(it)
                        settingsViewModel.onToggleVisibilityOfProductivityColorDialog()
                    }
                )
            }
        }

        // Dialogs
        AnimatedVisibility(visible = isSelectDialogueVisible) {
            CalenderDialogue(list = calenders) {
                Toast.makeText(context, it?.displayName, Toast.LENGTH_SHORT).show()
                if (it != null) {
                    scope.launch {
                        context.saveGoalCalendarToDataStore(context, it.id.toInt())
                    }
                }
                isSelectDialogueVisible = false
            }
        }

        AnimatedVisibility(visible = uiState.isColorPickerForSettingsVisible) {
            ColorPickerDialog(
                onColorSelected = {
                    settingsViewModel.onSetColor(it, context)
                    settingsViewModel.onToggleVisibilityOfProductivityColorDialog()
                    settingsViewModel.onSelectLevel(null)
                },
                onDismissRequest = {
                    settingsViewModel.onToggleVisibilityOfProductivityColorDialog()
                    settingsViewModel.onSelectLevel(null)
                }
            )
        }

        AnimatedVisibility(visible = uiState.isExportDialogVisible) {
            ExportDialog(
                json = uiState.exportedJson ?: "",
                onDismiss = { settingsViewModel.hideExportDialog() },
                onCopy = { text ->
                    val clipboard = android.content.Context.CLIPBOARD_SERVICE
                    val clip = android.content.ClipData.newPlainText("Backup Data", text)
                    (context.getSystemService(clipboard) as android.content.ClipboardManager).setPrimaryClip(clip)
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            )
        }

        AnimatedVisibility(visible = uiState.isImportDialogVisible) {
            ImportDialog(
                onImport = { json -> settingsViewModel.importData(json) },
                onDismiss = { settingsViewModel.hideImportDialog() }
            )
        }
    }
}

@Composable
private fun PrimarySettingsScreen(
    emergencyMode: Boolean,
    onEmergencyModeChange: (Boolean) -> Unit,
    filterResults: Boolean,
    onFilterResultsChange: (Boolean) -> Unit,
    selectedCalendar: String,
    onSelectCalendarClick: () -> Unit,
    onBackUpButtonClick: () -> Unit,
    onRestoreButtonClick: () -> Unit,
    onClickLevel: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // General Settings Section
        SettingsSection(title = "General") {
            SettingsItemWithSwitch(
                title = "Emergency Mode",
                subtitle = "Override app restrictions in urgent situations",
                isChecked = emergencyMode,
                onCheckedChange = onEmergencyModeChange
            )

            SettingsItemWithSwitch(
                title = "Filter Results",
                subtitle = "Show only relevant calendar entries",
                isChecked = filterResults,
                onCheckedChange = onFilterResultsChange
            )

            SettingsItemWithAction(
                title = "Selected Calendar",
                subtitle = "Choose your primary calendar source",
                value = selectedCalendar,
                onClick = onSelectCalendarClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Data Management Section
        SettingsSection(title = "Data Management") {
            SettingsItemWithButton(
                title = "Backup Data",
                subtitle = "Export your settings and data",
                buttonText = "Export",
                onClick = onBackUpButtonClick
            )

            SettingsItemWithButton(
                title = "Restore Data",
                subtitle = "Import previously exported data",
                buttonText = "Import",
                onClick = onRestoreButtonClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Customization Section
        SettingsSection(title = "Customization") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                ProductivityComposable(
                    heading = "Edit Color Preferences",
                    onClick = { level, _ ->
                        onClickLevel(level)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItemWithSwitch(
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsItemWithButton(
    title: String,
    subtitle: String? = null,
    buttonText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        FilledTonalButton(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(buttonText)
        }
    }
}

@Composable
fun SettingsItemWithAction(
    title: String,
    subtitle: String? = null,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun ExportDialog(json: String, onDismiss: () -> Unit, onCopy: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Export Data",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Copy this backup data to restore your settings later:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = json,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Backup JSON") },
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCopy(json) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Copy to Clipboard")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun ImportDialog(onImport: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Import Data",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Paste your backup JSON data below to restore your settings:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Paste Backup JSON here") },
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            "Paste your backup data here...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onImport(text) },
                enabled = text.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}