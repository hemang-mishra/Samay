package com.project.samay.presentation.domains

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.R
import com.project.samay.domain.model.CalendarColor
import com.project.samay.presentation.components.ColorItem
import com.project.samay.presentation.components.ColorPickerDialog
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class NavAddDomainScreen(val domainId: Int? = null)

/**
 * Main AddDomainScreen composable with ViewModel integration
 * Handles state management and business logic
 */
@Composable
fun AddDomainScreen(
    viewModel: DomainViewModel = koinViewModel(),
    domainId: Int? = null,
    navController: NavController
) {

    val context = LocalContext.current
    val domainUiState by viewModel.uiStateValue
    val allDomains by viewModel.allDomains.collectAsState(initial = emptyList())
    val addDomainState by viewModel.addDomainScreenState.collectAsState()

    // Initialize the screen data based on domainId
    LaunchedEffect(domainId, allDomains) {
        viewModel.initializeAddDomainScreen(domainId, allDomains)
    }

    // The stateless layout composable with all required parameters
    AddDomainScreenLayout(
        title = if (addDomainState.domainId != null) "Update your domain" else "Create new domain",
        name = addDomainState.name,
        onNameChange = { viewModel.updateName(it) },
        description = addDomainState.description,
        onDescriptionChange = { viewModel.updateDescription(it) },
        monthlyTarget = addDomainState.monthlyTarget,
        onMonthlyTargetChange = { viewModel.updateMonthlyTarget(it) },
        expectedPercentage = addDomainState.expectedPercentage,
        onExpectedPercentageChange = { viewModel.updateExpectedPercentage(it, allDomains) },
        timeSpent = addDomainState.timeSpent,
        onTimeSpentChange = { viewModel.updateTimeSpent(it) },
        totalPercentage = addDomainState.totalPercentage,
        selectedColor = domainUiState.selectedColor,
        isColorPickerVisible = domainUiState.isColorPickerDialogVisible,
        onColorClicked = { viewModel.switchVisibilityOfColorPicker() },
        onColorSelected = {it->
            viewModel.changeSelectedColor(it)
            viewModel.switchVisibilityOfColorPicker()
        },
        onColorPickerDismiss = { viewModel.switchVisibilityOfColorPicker() },
        onSaveClicked = {
            if (addDomainState.totalPercentage > 100) {
                viewModel.showToast(context, "Total percentage cannot be more than 100")
                return@AddDomainScreenLayout
            }

            if (viewModel.saveDomain(context)) {
                navController.navigateUp()
            }
        }
    )
}

/**
 * Stateless layout composable for AddDomainScreen
 * All data and callbacks provided through parameters
 */
@Composable
fun AddDomainScreenLayout(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    monthlyTarget: String,
    onMonthlyTargetChange: (String) -> Unit,
    expectedPercentage: String,
    onExpectedPercentageChange: (String) -> Unit,
    timeSpent: String,
    onTimeSpentChange: (String) -> Unit,
    totalPercentage: Float,
    selectedColor: CalendarColor,
    isColorPickerVisible: Boolean,
    onColorClicked: () -> Unit,
    onColorSelected: (CalendarColor) -> Unit,
    onColorPickerDismiss: () -> Unit,
    onSaveClicked: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header with title
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_category_24), // Add your domain icon
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Domain name field
                item {
                    InputSection(
                        title = "Domain Name",
                        description = "Give your domain a meaningful name",
                        icon = R.drawable.baseline_title_24, // Add your title icon
                        value = name,
                        onValueChange = onNameChange,
                        placeholder = "e.g., Work, Health, Learning"
                    )
                }

                // Description field
                item {
                    InputSection(
                        title = "Description",
                        description = "Define the scope and criteria for this domain",
                        icon = R.drawable.outline_description_24, // Add your description icon
                        value = description,
                        onValueChange = onDescriptionChange,
                        placeholder = "Describe what tasks belong to this domain",
                        maxLines = 3
                    )
                }

                // Monthly target field
                item {
                    InputSection(
                        title = "Monthly Vision",
                        description = "What do you want to achieve this month?",
                        icon = R.drawable.outline_target_24, // Add your target icon
                        value = monthlyTarget,
                        onValueChange = onMonthlyTargetChange,
                        placeholder = "Set your monthly goal",
                        maxLines = 2
                    )
                }

                // Expected percentage field
                item {
                    InputSection(
                        title = "Expected Percentage",
                        description = "How much time should this domain occupy?",
                        icon = R.drawable.outline_percent_24, // Add your percentage icon
                        value = expectedPercentage,
                        onValueChange = onExpectedPercentageChange,
                        placeholder = "e.g., 30",
                        suffix = "%"
                    )
                }

                // Time spent field
                item {
                    InputSection(
                        title = "Time Spent",
                        description = "Track your current time investment",
                        icon = R.drawable.baseline_timer_24, // Add your time icon
                        value = timeSpent,
                        onValueChange = onTimeSpentChange,
                        placeholder = "Hours per day/week"
                    )
                }

                // Total percentage info card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (totalPercentage > 100)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (totalPercentage > 100)
                                        R.drawable.outline_warning_24 // Add warning icon
                                    else
                                        R.drawable.outline_description_24 // Add info icon
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (totalPercentage > 100)
                                    MaterialTheme.colorScheme.onErrorContainer
                                else
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Total Allocation",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (totalPercentage > 100)
                                        MaterialTheme.colorScheme.onErrorContainer
                                    else
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "${totalPercentage.toInt()}% of your time",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (totalPercentage > 100)
                                        MaterialTheme.colorScheme.onErrorContainer
                                    else
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                if (totalPercentage > 100) {
                                    Text(
                                        text = "⚠️ Exceeds 100% - please adjust",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Color picker section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_palette_24), // Add palette icon
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Domain Color",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Choose a color to identify this domain",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    color = Color.Transparent
                                ) {
                                    ColorItem(
                                        color = androidx.compose.ui.graphics.Color(selectedColor.color),
                                        onClick = onColorClicked
                                    )
                                }
                            }
                        }
                    }
                }

                // Save button
                item {
                    Button(
                        onClick = onSaveClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_save_24), // Add save icon
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Domain",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Color picker dialog
        AnimatedVisibility(isColorPickerVisible) {
            ColorPickerDialog(
                onColorSelected = onColorSelected,
                onDismissRequest = onColorPickerDismiss
            )
        }
    }
}

@Composable
private fun InputSection(
    title: String,
    description: String,
    icon: Int,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLines: Int = 1,
    suffix: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = maxLines,
                suffix = suffix?.let { { Text(it) } }
            )
        }
    }
}