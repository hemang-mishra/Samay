package com.project.samay.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp

@Composable
fun BoldItalicText(text: String, modifier: Modifier = Modifier, fontSize: Int = 16) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
    )
}
