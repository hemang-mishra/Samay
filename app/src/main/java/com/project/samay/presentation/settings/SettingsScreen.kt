package com.project.samay.presentation.settings

import ProductivityComposable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import com.project.samay.SamayApplication
import com.project.samay.domain.util.Preferences
import com.project.samay.presentation.calender.CalendarViewModel
import com.project.samay.presentation.calender.CalenderDialogue
import com.project.samay.presentation.components.ColorPickerDialog
import com.project.samay.presentation.monitor.MonitorViewModel
import com.project.samay.ui.theme.spacing
import com.project.samay.util.ProductivityColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(monitorViewModel: MonitorViewModel, calendarViewModel: CalendarViewModel, settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()){
    val calenders by calendarViewModel.calendarType
    val context = LocalContext.current.applicationContext as SamayApplication
    val selectedCalendarIndex by context.readGoalCalendarFromDataStore(context)
        .collectAsState(initial = null)
    val calendarUIState by calendarViewModel.calendarUIState
    val isEmergency by monitorViewModel.isEmergency
    var isSelectDialogueVisible by remember {
        mutableStateOf(false)
    }
    val uiState by settingsViewModel.uiState.collectAsState()
   val scope = rememberCoroutineScope()
    Scaffold() {
        it
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
                settingsViewModel.backupDomainsAndHistory()
            },
            onRestoreButtonClick = {
                settingsViewModel.fetchDomainsAndHistory()
            },
            onClickLevel = {
                settingsViewModel.onSelectLevel(it)
                settingsViewModel.onToggleVisibilityOfProductivityColorDialog()
            }
        )
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
    onClickLevel: (String)->Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
            .verticalScroll(scrollState)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.large)
        )

        // Emergency Mode
        SettingsItemWithSwitch(
            title = "Emergency Mode",
            isChecked = emergencyMode,
            onCheckedChange = onEmergencyModeChange
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        // Filter Results
        SettingsItemWithSwitch(
            title = "Filter Results",
            isChecked = filterResults,
            onCheckedChange = onFilterResultsChange
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        // Selected Calendar
        SettingsItemWithAction(
            title = "Selected Calendar",
            value = selectedCalendar,
            onClick = onSelectCalendarClick
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        SettingsItemWithButton("Backup Data") {
            onBackUpButtonClick()
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        SettingsItemWithButton("Restore Data") {
            onRestoreButtonClick()
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        ProductivityComposable(
            heading = "Edit Color Preferences",
            onClick = {level, _ ->
                onClickLevel(level)
            }
        )
    }
}

@Composable
fun SettingsItemWithSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsItemWithButton(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = onClick
        ) {
            Text(title)
        }
    }
}

@Composable
fun SettingsItemWithAction(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Extension for spacing to match Material Theme 3 spacing
