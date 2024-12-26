package com.project.samay.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarGoal(text: String = "Domains") {
    LargeTopAppBar(title = {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge
        )
    })
}