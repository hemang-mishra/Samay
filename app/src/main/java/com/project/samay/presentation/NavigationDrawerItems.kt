package com.project.samay.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationDrawerItems(val title: String, val icon: ImageVector) {
    SETTINGS("Settings", Icons.Default.Settings),
    HISTORY("History", Icons.Default.History),
}