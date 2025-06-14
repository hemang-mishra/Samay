package com.project.samay.presentation.calender

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.samay.R
import com.project.samay.presentation.components.PrimaryAppButton
import com.project.samay.ui.theme.SamayTheme
import com.project.samay.util.calculations.TimeUtils

@Composable
fun TimeSlotNote(
    allSlots: List<Pair<Long, Long>>,
    selectedSlots: List<Pair<Long, Long>>,
    onClickSlot: (Pair<Long, Long>) -> Unit,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Section
            HeaderSection(selectedCount = selectedSlots.size, totalCount = allSlots.size)

            Spacer(modifier = Modifier.height(20.dp))

            // Time Slots List
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                allSlots.forEach { slot ->
                    TimeSlotItem(
                        startTime = slot.first,
                        endTime = slot.second,
                        isSelected = selectedSlots.contains(slot)
                    ) {
                        onClickSlot(slot)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            ActionButton(
                selectedCount = selectedSlots.size,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun HeaderSection(selectedCount: Int, totalCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_filled_24), // Replace with time icon
                        contentDescription = "Time slots",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Track Your Time",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "What did you do in these time slots?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator
            ProgressIndicator(selectedCount = selectedCount, totalCount = totalCount)
        }
    }
}

@Composable
private fun ProgressIndicator(selectedCount: Int, totalCount: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Selected",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "$selectedCount of $totalCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            shape = RoundedCornerShape(3.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ) {
            val progress = if (totalCount > 0) selectedCount.toFloat() / totalCount else 0f
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 300),
                label = "progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
fun TimeSlotItem(
    startTime: Long,
    endTime: Long,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val start = TimeUtils.convertMillisToString(startTime)
    val end = TimeUtils.convertMillisToString(endTime)
    val relativeTimeDescription = TimeUtils.getRelativeTimeDescription(startTime)

    // Animated values
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f,
        animationSpec = tween(durationMillis = 200),
        label = "alpha"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(durationMillis = 200),
        label = "container_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onSecondaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "content_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .alpha(animatedAlpha)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection Indicator
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ) {
                if (isSelected) {
                    Icon(
                        painter = painterResource(R.drawable.outline_check_24), // Replace with check icon
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Time Information
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = relativeTimeDescription,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = start,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(4.dp),
                        shape = CircleShape,
                        color = contentColor.copy(alpha = 0.6f)
                    ) {}

                    Text(
                        text = end,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Duration Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                val duration = TimeUtils.convertTimeDurationToHoursAndMinutes(endTime - startTime)
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ActionButton(selectedCount: Int, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (selectedCount > 0) {
                Row(
                    modifier = Modifier.padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(20.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = selectedCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "time slots selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            PrimaryAppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSave,
                text = if (selectedCount > 0) "Track Selected Slots" else "Track",
                icon = painterResource(R.drawable.outline_send_24),
                enabled = selectedCount > 0
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimeSlotNote() {
    SamayTheme(darkTheme = false) {
        TimeSlotNote(
            allSlots = listOf(
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
            ),
            selectedSlots = listOf(
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
            ),
            onClickSlot = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimeSlotNoteDark() {
    SamayTheme(darkTheme = true) {
        TimeSlotNote(
            allSlots = listOf(
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
            ),
            selectedSlots = listOf(
                Pair(System.currentTimeMillis(), TimeUtils.addMinutesToMillis(System.currentTimeMillis(), 30)),
            ),
            onClickSlot = {},
            onSave = {}
        )
    }
}