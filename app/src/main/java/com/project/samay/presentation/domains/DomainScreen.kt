package com.project.samay.presentation.domains

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.samay.R
import com.project.samay.domain.model.DomainEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.components.TopAppBarGoal
import java.util.Locale
import kotlin.math.ceil

@Composable
fun DomainScreen(domainViewModel: DomainViewModel, navController: NavController) {
    val context = LocalContext.current
    val domains by domainViewModel.allDomains.collectAsState(initial = emptyList())
    val uiState by domainViewModel.uiStateValue


    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarGoal()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Statistics Card
                item {
                    StatisticsCard(
                        totalHours = (domains.sumOf { it.timeSpent } / 60.0f).toOneDecimalPlace(),
                        totalDomains = domains.size
                    )
                }

                // Domain Items
                itemsIndexed(domains) { _, domain ->
                    DomainItem(
                        domain = domain,
                        isSelected = uiState.selectedDomain == domain,
                        viewModel = domainViewModel,
                        navController = navController
                    ) {
                        domainViewModel.selectDomain(context = context, domain)
                    }
                }

                // Reset Button
                item {
                    ResetButton(
                        onReset = { domainViewModel.resetAllDomains() }
                    )
                }

                // Bottom Spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { navController.navigate(NavAddDomainScreen(false)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_add_24), // Replace with your drawable
                contentDescription = "Add Domain",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun StatisticsCard(
    totalHours: Float,
    totalDomains: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatisticItem(
                value = totalHours.toString(),
                label = "Total Hours",
                suffix = "hrs"
            )

            VerticalDivider()

            StatisticItem(
                value = totalDomains.toString(),
                label = "Active Domains",
                suffix = ""
            )
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String,
    suffix: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            if (suffix.isNotEmpty()) {
                Text(
                    text = " $suffix",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )
    )
}

@Composable
fun DomainItem(
    domain: DomainEntity,
    isSelected: Boolean,
    viewModel: DomainViewModel,
    navController: NavController,
    onClick: () -> Unit
) {
    // Calculate target hours based on expected percentage
    // Assuming total available hours in a month (e.g., 30 days * 24 hours = 720 hours)
    // You can adjust this calculation based on your business logic
    val totalAvailableHours = 150f // Monthly total hours
    val targetHours = (domain.expectedPercentage / 100f) * totalAvailableHours
    val currentHours = domain.timeSpent / 60.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(animationSpec = tween(300)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Content Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon with colored background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(domain.color).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_category_24), // Replace with your drawable
                        contentDescription = "Category",
                        tint = Color(domain.color),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Domain Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = domain.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = domain.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isSelected) Int.MAX_VALUE else 1
                    )
                }
            }

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Spent with Target
                MetricCardWithTarget(
                    currentHours = currentHours,
                    targetHours = targetHours
                )

                // Progress Indicator
                ProgressIndicator(
                    current = domain.presentPercentage,
                    target = domain.expectedPercentage
                )
            }

            // Expanded Content
            if (isSelected) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                ExpandedContent(
                    domain = domain,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    value: String,
    unit: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " $unit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 1.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MetricCardWithTarget(
    currentHours: Float,
    targetHours: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Display as "currentHours/targetHours hrs"
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${currentHours.toOneDecimalPlace()}/${targetHours.toOneDecimalPlace()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " hrs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 1.dp)
            )
        }
        Text(
            text = "Spent/Target",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProgressIndicator(
    current: Float,
    target: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Progress Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${current.toOneDecimalPlace()}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "of",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${target.toOneDecimalPlace()}%",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = (current / target).coerceAtMost(1.0f),
            modifier = Modifier.width(80.dp),
            color = if (current >= target)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun ExpandedContent(
    domain: DomainEntity,
    viewModel: DomainViewModel,
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Monthly Target
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_target_24), // Replace with your drawable
                contentDescription = "Target",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Monthly Target: ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = domain.monthlyTarget,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = painterResource(R.drawable.outline_add_24), // Replace with your drawable
                label = "Use",
                onClick = { navController.navigate(NavUseDomainScreen) }
            )

            ActionButton(
                icon = painterResource(R.drawable.outline_edit_24), // Replace with your drawable
                label = "Edit",
                onClick = { navController.navigate(NavAddDomainScreen(true)) }
            )

            ActionButton(
                icon = painterResource(R.drawable.baseline_delete_24), // Replace with your drawable
                label = "Delete",
                onClick = { viewModel.deleteDomain(domain) },
                isDestructive = true
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.painter.Painter,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isDestructive)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = if (isDestructive)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDestructive)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResetButton(onReset: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        TextButton(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_refresh_24), // Replace with your drawable
                contentDescription = "Reset",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reset All Domains",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun Float.toOneDecimalPlace(): Float {
    val format = String.format(Locale.ROOT, "%.1f", this)
    return format.toFloat()
}