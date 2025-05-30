package com.project.samay.presentation.meditate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.samay.R
import com.project.samay.util.calculations.MediaUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun MeditationMusicScreen(meditateViewModel: MeditateViewModel = koinViewModel<MeditateViewModel>()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Image
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_timer_24), // Add your image resource here
                contentDescription = "Meditation Image",
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Song Title
        Text(
            text = "Music title",
            style = TextStyle(fontSize = 24.sp, color = Color(0xFF393E46))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Music Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(onClick = { /* Previous action */ }) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color(0xFF646464)
                )
            }
            IconButton(onClick = {
                meditateViewModel.onClickPlayPauseButton()
            }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color(0xFF393E46))
            }
            IconButton(onClick = { /* Next action */ }) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color(0xFF646464))
            }
        }

        // Slider for Music Progress
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                value = 0.5f, // Example value
                onValueChange = { /* Update progress */ },
                modifier = Modifier.fillMaxWidth(0.8f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = MediaUtil.getTimeStampFromSeconds(360))
                Text(text = MediaUtil.getTimeStampFromSeconds(9000))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
