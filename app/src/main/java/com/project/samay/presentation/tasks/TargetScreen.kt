package com.project.samay.presentation.tasks

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.samay.SamayApplication
import com.project.samay.domain.model.DEFAULT_TARGET
import com.project.samay.presentation.components.BoldItalicText
import com.project.samay.util.calculations.Logic
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object NavTargetScreen

@Composable
fun TargetScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as SamayApplication
    val targetInitial by context.readTargetFromDataStore(context).collectAsState(initial = DEFAULT_TARGET)
    var target by remember {
        mutableStateOf(targetInitial?.toString()?:"$DEFAULT_TARGET")
    }
    val coroutine = rememberCoroutineScope()
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("Change Target")
                Spacer(modifier = Modifier.height(8.dp))
                BoldItalicText(text = "Enter task in minutes")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = target, onValueChange = {
                    target = it
                })
                Spacer(modifier = Modifier.height(8.dp))
                BoldItalicText(text = "Current target is ${Logic.formatInHrsAndMins(target.toLongOrNull() ?: DEFAULT_TARGET.toLong())} hours")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (target.toIntOrNull() == null) {
                        Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (target.toInt() < 3*60) {
                        Toast.makeText(
                            context,
                            "Number of hours should be greater than 3",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    coroutine.launch {
                        context.saveTargetToDataStore(context, target.toLong())
                        navController.navigateUp()
                    }
                }) {
                    Text(text = "Save Target")
                }
            }

        }
    }
}