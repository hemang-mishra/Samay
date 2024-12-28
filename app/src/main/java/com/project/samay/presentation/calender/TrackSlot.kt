package com.project.samay.presentation.calender

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.samay.presentation.components.PrimaryAppButton
import com.project.samay.ui.theme.SamayTheme
import com.project.samay.util.calculations.TimeUtils

@Composable
fun TimeSlotNote(allSlots: List<Pair<Long, Long>>, selectedSlots: List<Pair<Long,Long>>, onClickSlot:(Pair<Long,Long>)->Unit, onSave:()->Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "What you did in these time slots?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            allSlots.forEach {
                TimeSlotItem(it.first, it.second, selectedSlots.contains(it)){
                    onClickSlot(it)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryAppButton(modifier = Modifier.fillMaxWidth(), onClick = {
                onSave()
            }, text = "Track", icon = Icons.AutoMirrored.Filled.Send)
        }
    }
}

@Composable
fun TimeSlotItem(startTime: Long, endTime: Long, isSelected: Boolean = false, onClick: ()->Unit) {
    val start = TimeUtils.convertMillisToString(startTime)
    val end = TimeUtils.convertMillisToString(endTime)
    val relativeTimeDescription = TimeUtils.getRelativeTimeDescription(startTime)

    val backgroundColor = if (!isSelected) CardDefaults.cardColors().containerColor.copy(alpha = 0.2f) else CardDefaults.cardColors().containerColor
    val textColor = if (!isSelected) CardDefaults.cardColors().contentColor.copy(alpha = 0.7f) else CardDefaults.cardColors().contentColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClick()
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "$relativeTimeDescription $start: $end",
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewTimeSlotNote() {
    SamayTheme(
        darkTheme = true
    ) {
        TimeSlotNote(
            allSlots = listOf(
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(),30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(),30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(),30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(),30)),
            ),
            selectedSlots = listOf(
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(),30)),

                ),
            {}
        ){

        }
    }
}