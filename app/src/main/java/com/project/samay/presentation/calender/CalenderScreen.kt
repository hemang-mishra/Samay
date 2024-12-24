package com.project.samay.presentation.calender

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.project.samay.SamayApplication
import com.project.samay.domain.model.CalendarEvent
import com.project.samay.domain.model.CalendarType
import com.project.samay.presentation.domains.BoldItalicText
import com.project.samay.presentation.tasks.SelectDomainDialogue
import com.project.samay.util.calculations.TimeUtils
import com.project.samay.util.getRandomColor
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object NavCalenderScreen

@Composable
fun CalenderScreen(calendarViewModel: CalendarViewModel) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            val calenders by calendarViewModel.calendarType
            val context = LocalContext.current.applicationContext as SamayApplication
            val selectedCalendarIndex by context.readGoalCalendarFromDataStore(context)
                .collectAsState(initial = null)
            var isSelectDialogueVisible by remember {
                mutableStateOf(false)
            }
            val scope = rememberCoroutineScope()
            val uiState by calendarViewModel.calendarUIState
//            var isDomainDialogVisible by remember {
//                mutableStateOf(false)
//            }
            val allDomains by calendarViewModel.allDomains.collectAsState(initial = emptyList())
            val lazyColumnState= rememberLazyListState()
            

            Column(modifier = Modifier.fillMaxSize()) {
                TopBarCalendar(
                    modifier = Modifier.fillMaxHeight(0.3f),
                    calendarViewModel,
                    uiState.isFilterApplied
                )
                LazyColumn {
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

                    item {
                        Text(text = "Select Calender for adding events")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Current calender is: ${
                                calendarViewModel.getCalenderAtIndex(
                                    selectedCalendarIndex
                                )?.displayName ?: "None"
                            }"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedCard(
                            onClick = {
                                isSelectDialogueVisible = true
                            },
                            modifier = Modifier
                        ) {
                            Text(text = "Select Calender", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

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
            AnimatedVisibility(visible = uiState.isDomainDialogueVisible) {
                SelectDomainDialogue(domains = allDomains) { domainEntity ->
                    calendarViewModel.selectDomain(domainEntity)
                    calendarViewModel.switchVisibilityOfDialogue()
                    calendarViewModel.approveEvent(context)
                }
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
    // Validate color string
    val profileColor = getRandomColor()
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
                imageVector = Icons.Default.CalendarMonth, contentDescription = null,
                tint = Color(profileColor.hex),
                modifier = Modifier
                    .align(Alignment.Top)
                    .width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                BoldItalicText(text = calendarEvent.title, fontSize = 24)
                Text(text = calendarEvent.description)
            }
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(4.dp)
            ) {
                BoldItalicText(text = durationString)
//                Row(modifier = Modifier) {
//                    Text(text = (domain.timeSpent/60.0f).toOneDecimalPlace().toString())
//                    BoldItalicText(
//                        text = " hrs",
//                        modifier = Modifier.align(Alignment.CenterVertically)
//                    )
//                }
//                BoldItalicText(text = "spent")
            }
            BoldItalicText(text = TimeUtils.convertMillisToDate(calendarEvent.dtstart))
        }
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
//            Row {
//                BoldItalicText(
//                    text = "Montly target: ",
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//                Text(text = domain.monthlyTarget)
//            }
            Row(modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = {
//                    navController.navigate(NavUseDomainScreen)
                    viewModel.switchVisibilityOfDialogue()
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }

                IconButton(onClick = {
//                    viewModel.deleteDomain(domain)
                    viewModel.rejectEvent(context)
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
    HorizontalDivider()

}

@Composable
fun TopBarCalendar(modifier: Modifier, viewModel: CalendarViewModel, isFilterEnabled: Boolean) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            BoldItalicText(text = "Filter")
            Switch(checked = isFilterEnabled, onCheckedChange = viewModel::toggleFilter)
        }
        Text(
            text = "Add events from calendar", modifier = Modifier.align(Alignment.BottomStart),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderDialogue(list: List<CalendarType>, onSelect: (CalendarType?) -> Unit) {
//    val context = LocalContext.current
    BasicAlertDialog(onDismissRequest = { onSelect(null) }) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clip(RoundedCornerShape(32.dp))
                .padding(16.dp)
        ) {
            LazyColumn {
                items(list.size) { index->
                    val it = list[index]
                    Text(
                            text = it.displayName,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onSelect(it)
                                },
                            style = MaterialTheme.typography.bodyMedium,

                            )

                }
            }
        }
    }
}