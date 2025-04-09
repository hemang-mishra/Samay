package com.project.samay.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavigationDrawerContent(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxWidth(0.6f)) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(NavigationDrawerItems.entries.size) { index ->
                RowOfDrawer(
                    navigationDrawerItems = NavigationDrawerItems.entries[index],
                    modifier = Modifier
                ) {
                    when (NavigationDrawerItems.entries[index]) {
//                        NavigationDrawerItems.BACKUP -> {
//                            navController.navigate(Destinations.BackupScreen)
//                        }

                        NavigationDrawerItems.SETTINGS -> {
                            navController.navigate(Destinations.SettingsScreen)
                        }

                        NavigationDrawerItems.HISTORY -> {
                            navController.navigate(Destinations.HistoryScreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowOfDrawer(
    modifier: Modifier,
    navigationDrawerItems: NavigationDrawerItems,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Icon(imageVector = navigationDrawerItems.icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = navigationDrawerItems.title, style = MaterialTheme.typography.titleMedium)
    }
}