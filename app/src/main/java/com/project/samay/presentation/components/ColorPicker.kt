package com.project.samay.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.samay.SamayApplication
import com.project.samay.domain.model.CalendarColor

@Composable
fun ColorPickerDialog(
    onColorSelected: (CalendarColor) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current.applicationContext as SamayApplication
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)
                .verticalScroll(scrollState)) {
                Text(text = "Pick a Color", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                context.calendarColors.forEach { calendarColor ->
                    ColorItem(
                        color = Color(calendarColor.color),
                        onClick = { onColorSelected(calendarColor) }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorItem(color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .clickable(onClick = onClick)
            .background(color, shape = CircleShape)
    )
}

@Composable
fun ColorPickerExample() {
    var selectedColor by remember { mutableStateOf<CalendarColor?>(null) }
    var showDialog by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showDialog = true }) {
            Text(text = "Pick a Color")
        }

        selectedColor?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Selected Color: ${it.color}")
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(it.color), shape = CircleShape)
            )
        }

        if (showDialog) {
            ColorPickerDialog(
                onColorSelected = {
                    selectedColor = it
                    showDialog = false
                },
                onDismissRequest = { showDialog = false }
            )
        }
    }
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPickerExample()
}