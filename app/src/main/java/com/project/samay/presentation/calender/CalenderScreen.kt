package com.project.samay.presentation.calender

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.samay.R
import com.project.samay.SamayApplication
import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.CalendarType
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.tasks.SelectDomainDialogue
import com.project.samay.util.calculations.TimeUtils
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object NavCalenderScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderScreen(calendarViewModel: CalendarViewModel) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            val uiState by calendarViewModel.calendarUIState
            if (uiState.selectedEmptySlots.isNotEmpty() && !uiState.isSearchComposableVisible) {
                ExtendedFloatingActionButton(
                    onClick = {
                        //Saving the selected slots
                        calendarViewModel.switchVisibilityOfSearchComposable()
                        calendarViewModel.onToggleVisiblilityOfProductivityBottomSheet()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_send_24),
                        contentDescription = "Track slots"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Track ${uiState.selectedEmptySlots.size} slot${if (uiState.selectedEmptySlots.size > 1) "s" else ""}",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val context = LocalContext.current.applicationContext as SamayApplication
            val selectedCalendarIndex by context.readGoalCalendarFromDataStore(context)
                .collectAsState(initial = null)
            val productivityLevelSheet = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            val uiState by calendarViewModel.calendarUIState
            val lazyListState = rememberLazyListState()
            val allDomains by calendarViewModel.allDomains.collectAsState(initial = emptyList())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Enhanced Top Bar
                TopBarCalendar(
                    modifier = Modifier
                        .fillMaxHeight(0.25f)
                        .fillMaxWidth(),
                    calendarViewModel,
                    uiState.isFilterApplied
                ) {
                    calendarViewModel.resetAfterSaving(context)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    state = lazyListState
                ) {
                    item {
                        if (uiState.allEmptySlots.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                AnimatedContent(uiState.isSearchComposableVisible) {
                                    if (it) {
                                        SearchComposable(
                                            query = uiState.queryText,
                                            matchingNames = uiState.matchingNames,
                                            selectedName = uiState.selectedName,
                                            onSelectName = { calendarViewModel.onSelectDistinctName(it) },
                                            onChangeQuery = { calendarViewModel.onChangeQuery(it) },
                                            isConfirmPromptVisible = uiState.isConfirmModeActive,
                                            generatedHistory = uiState.generatedHistoryToBeSaved,
                                            onConfirm = {
                                                calendarViewModel.onSelectConfirmButton(context)
                                                scope.launch {
                                                    lazyListState.animateScrollToItem(0)
                                                }
                                            },
                                        ) {
                                            calendarViewModel.onSaveInSearchScreen(context)
                                            scope.launch {
                                                lazyListState.animateScrollToItem(0)
                                            }
                                        }
                                    } else {
                                        TimeSlotNote(
                                            uiState.allEmptySlots,
                                            uiState.selectedEmptySlots,
                                            {
                                                calendarViewModel.onClickSlot(it)
                                            }) {
                                            //Saving the selected slots
                                            calendarViewModel.switchVisibilityOfSearchComposable()
                                            calendarViewModel.onToggleVisiblilityOfProductivityBottomSheet()
                                            scope.launch {
                                                lazyListState.animateScrollToItem(0)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    item{
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_sleep_score_24), // Replace with your sleep icon
                                    contentDescription = "Sleep icon",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                TextButton(
                                    onClick = {
                                        calendarViewModel.showSleepDialog(context)
                                    }
                                ) {
                                    Text(
                                        "Track Sleep",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    item {
                        if(uiState.events.isNotEmpty()){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(4.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {}

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "Events",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    items(uiState.events.size) { index: Int ->
                        val event = uiState.events[index]
                        if (calendarViewModel.validate(event, selectedCalendarIndex?.toLong())) {
                            Log.i("Calendar screen", "$event")
                            CalendarItem(
                                calendarEvent = event,
                                isSelected = uiState.selectedEvent == event,
                                viewModel = calendarViewModel,
                                onClick = {
                                    calendarViewModel.selectEvent(event)
                                }
                            )
                        }
                    }
                }
            }

            // Sleep tracking dialog
            if (uiState.isSleepDialogVisible) {
                SleepTrackingDialog(
                    sleepSlots = uiState.sleepSlots,
                    domains = allDomains,
                    onDismiss = { calendarViewModel.dismissSleepDialog() },
                    onSave = { domainId, domainName, selectedSleepSlots, color ->
                        calendarViewModel.saveSleepData(
                            context,
                            domainId,
                            domainName,
                            selectedSleepSlots,
                            color
                        )
                    }
                )
            }

            AnimatedVisibility(visible = uiState.isDomainDialogueVisible) {
                SelectDomainDialogue(domains = allDomains) { domainEntity ->
                    calendarViewModel.selectDomain(domainEntity)
                    calendarViewModel.switchVisibilityOfDialogue()
                    if (uiState.queryText.isNotEmpty()) {
                        calendarViewModel.saveAfterDialogueGetsClosed(context)
                    } else {
                        calendarViewModel.approveEvent(context)
                    }
                }
            }

            AnimatedVisibility(uiState.isProductivityBottomSheetVisible) {
                SelectProductivityLevelBottomSheet(
                    onDismiss = {
                        calendarViewModel.onToggleVisiblilityOfProductivityBottomSheet()
                    },
                    bottomSheetState = productivityLevelSheet,
                    selectedColor = uiState.selectedProductivityColor,
                    onSelect = { color->
                        calendarViewModel.onSelectColor(color)
                        calendarViewModel.onToggleVisiblilityOfProductivityBottomSheet()
                    }
                )
            }
        }
    }
}

@Composable
fun CalendarItem(
    calendarEvent: CalendarEvent,
    isSelected: Boolean,
    viewModel: CalendarViewModel,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val daysAgo = TimeUtils.covertMillisToNumberOfDaysAgo(System.currentTimeMillis())
    val durationString =
        TimeUtils.convertTimeDurationToHoursAndMinutes(calendarEvent.dtend - calendarEvent.dtstart)

    val profileColor =
        (context.applicationContext as SamayApplication).calendarColors.randomOrNull()
            ?: CalendarColor.default

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
            )
        else
            CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Event Icon and Details
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_event_24), // Replace with calendar icon
                            contentDescription = "Calendar event",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = calendarEvent.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (calendarEvent.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = calendarEvent.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Duration and Date
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = durationString,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = TimeUtils.convertMillisToDate(calendarEvent.dtstart),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action buttons when selected
            if (isSelected) {
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            viewModel.switchVisibilityOfDialogue()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_add_24), // Replace with add icon
                            contentDescription = "Add event",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.rejectEvent(context)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24), // Replace with delete icon
                            contentDescription = "Delete event",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarCalendar(
    modifier: Modifier,
    viewModel: CalendarViewModel,
    isFilterEnabled: Boolean,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Refresh Icon
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
                    .clickable { onRefresh() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_refresh_24), // Replace with refresh icon
                    contentDescription = "Refresh calendar",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Title with gradient-like effect using multiple text layers
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "Manage",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "Calendar",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                // Filter indicator
                if (isFilterEnabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Filtered",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderDialogue(list: List<CalendarType>, onSelect: (CalendarType?) -> Unit) {
    BasicAlertDialog(onDismissRequest = { onSelect(null) }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Calendar",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list.size) { index ->
                        val calendarType = list[index]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(calendarType) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(8.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {}

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = calendarType.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}