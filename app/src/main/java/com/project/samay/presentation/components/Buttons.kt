package com.project.samay.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.ui.graphics.painter.Painter
import com.project.samay.R
import com.project.samay.ui.theme.SamayTheme

@Composable
fun PrimaryAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter? = null,
    iconRes: Int? = null, // For drawable resources
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    // Animated values for better visual feedback
    val animatedScale by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.95f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.6f,
        animationSpec = tween(durationMillis = 200),
        label = "button_alpha"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant
            isLoading -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 200),
        label = "container_color"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
            else -> MaterialTheme.colorScheme.onPrimary
        },
        animationSpec = tween(durationMillis = 200),
        label = "content_color"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp) // Increased height for better touch target
            .scale(animatedScale)
            .alpha(animatedAlpha),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled && !isLoading) 2.dp else 0.dp,
            pressedElevation = if (enabled && !isLoading) 4.dp else 0.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            // Icon handling
            when {
                isLoading -> {
                    // Loading indicator - you can replace this with a custom loading icon
                    Icon(
                        painter = painterResource(R.drawable.outline_downloading_24), // Replace with loading icon
                        contentDescription = "Loading",
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                icon != null -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                iconRes != null -> {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            // Button text
            Text(
                text = if (isLoading) "Loading..." else text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

// Variant for secondary button style
@Composable
fun SecondaryAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    iconRes: Int? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.95f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.6f,
        animationSpec = tween(durationMillis = 200),
        label = "button_alpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(animatedScale)
            .alpha(animatedAlpha),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            when {
                isLoading -> {
                    Icon(
                        painter = painterResource(R.drawable.outline_downloading_24),
                        contentDescription = "Loading",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                iconRes != null -> {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            Text(
                text = if (isLoading) "Loading..." else text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

// Variant for outline button style
@Composable
fun OutlineAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    iconRes: Int? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.95f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.6f,
        animationSpec = tween(durationMillis = 200),
        label = "button_alpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(animatedScale)
            .alpha(animatedAlpha),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 1.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = enabled && !isLoading),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            when {
                isLoading -> {
                    Icon(
                        painter = painterResource(R.drawable.outline_downloading_24),
                        contentDescription = "Loading",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                iconRes != null -> {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            Text(
                text = if (isLoading) "Loading..." else text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButtons() {
    SamayTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // Primary buttons
            PrimaryAppButton(
                onClick = {},
                text = "Primary Button",
                icon = painterResource(R.drawable.outline_downloading_24),
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryAppButton(
                onClick = {},
                text = "Disabled Button",
                icon = painterResource(R.drawable.outline_downloading_24),
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryAppButton(
                onClick = {},
                text = "Loading Button",
                isLoading = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Secondary button
            SecondaryAppButton(
                onClick = {},
                text = "Secondary Button",
                icon = Icons.AutoMirrored.Filled.Send,
                modifier = Modifier.fillMaxWidth()
            )

            // Outline button
            OutlineAppButton(
                onClick = {},
                text = "Outline Button",
                icon = Icons.AutoMirrored.Filled.Send,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}