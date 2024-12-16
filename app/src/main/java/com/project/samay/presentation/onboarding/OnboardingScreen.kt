package com.project.samay.presentation.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(){
    Scaffold { padding->
        Box(modifier = Modifier.fillMaxSize()
            .padding(padding),
            contentAlignment = Alignment.Center
        ){
            Column {
                Button(onClick = {

                }) {
                    Text("Grant one permission")
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick ={

                }) {
                    Text("Grant all permissions")
                }
            }
        }
    }
}