package com.project.samay.presentation.calender

import ProductivityComposable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.samay.domain.model.CalendarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectProductivityLevelBottomSheet(
    onDismiss: () -> Unit,
    bottomSheetState: SheetState,
    selectedColor: CalendarColor,
    onSelect: (CalendarColor) -> Unit,
) {
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onDismiss,
    ){
        Box(modifier = Modifier.fillMaxSize()
            .padding(16.dp)
        ) {
            ProductivityComposable(
                selectedColor = selectedColor,
                heading = "How productive were you?",
                onClick = { _, color ->
                    onSelect(color)
                }
            )
        }
    }
}