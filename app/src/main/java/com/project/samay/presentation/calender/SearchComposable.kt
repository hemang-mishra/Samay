package com.project.samay.presentation.calender

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.samay.R
import com.project.samay.domain.model.DistinctNames
import com.project.samay.domain.model.HistoryEntity
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.presentation.components.PrimaryAppButton
import com.project.samay.presentation.history.HistoryItem
import com.project.samay.ui.theme.SamayTheme

@Composable
fun SearchComposable(
    query: String,
    matchingNames: List<DistinctNames>,
    selectedName: DistinctNames?,
    isConfirmPromptVisible: Boolean = false,
    generatedHistory: List<HistoryEntity>,
    onSelectName: (DistinctNames) -> Unit,
    onChangeQuery: (String) -> Unit,
    onConfirm: () -> Unit,
    onClickSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Search Bar Section
        SearchBar(
            query = query,
            onChangeQuery = onChangeQuery
        )

        // Results Section
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isConfirmPromptVisible) "Generated Slots" else "Search Results",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                if (!isConfirmPromptVisible && matchingNames.isNotEmpty()) {
                    Text(
                        text = "${matchingNames.size} found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Animated Content
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AnimatedContent(
                    targetState = isConfirmPromptVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "content_animation"
                ) { showConfirm ->
                    if (showConfirm) {
                        // Generated History Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            generatedHistory.forEach { history ->
                                HistoryItem(
                                    history,
                                    false,
                                    { /* deleteHistory(it) */ },
                                    { /* selectHistory(it) */ }
                                )
                            }
                        }
                    } else {
                        // Search Results Section
                        if (matchingNames.isEmpty()) {
                            EmptySearchState(query = query)
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                matchingNames.forEach { name ->
                                    SearchItem(
                                        distinctNames = name,
                                        isSelected = name == selectedName,
                                        onClick = { onSelectName(name) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Button
        PrimaryAppButton(
            onClick = if (!isConfirmPromptVisible) onClickSave else onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = if (!isConfirmPromptVisible) "Save Selection" else "Confirm Slots",
            icon = painterResource(R.drawable.outline_save_24) // Replace with your actual drawable resource
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onChangeQuery: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onChangeQuery,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                "Search activities...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_feature_search_24), // Replace with your actual drawable resource
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun SearchItem(
    distinctNames: DistinctNames,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(animationSpec = tween(200)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with colored background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Color(distinctNames.productivityColor).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_category_24), // Replace with your actual drawable resource
                    contentDescription = "Category",
                    tint = Color(distinctNames.productivityColor),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = distinctNames.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = distinctNames.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            // Selection Indicator
            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.outline_check_24), // Replace with your actual drawable resource
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_feature_search_24), // Replace with your actual drawable resource
            contentDescription = "No results",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = if (query.isEmpty()) "Start typing to search" else "No results found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        if (query.isNotEmpty()) {
            Text(
                text = "Try different keywords or check spelling",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun SearchComposablePreview() {
    SamayTheme {
        Surface {
            // Preview implementation would go here
            // Uncomment and modify as needed for your preview
        }
    }
}