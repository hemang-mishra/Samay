package com.project.samay.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryAppButton(
    onClick: ()->Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null
){
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
    ) {
        if(icon != null){
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier=Modifier.width(8.dp))
        }
        Text(text)
    }
}