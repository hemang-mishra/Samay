package com.project.samay.presentation.onboarding

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import com.project.samay.MainActivity
import com.project.samay.R
import com.project.samay.presentation.Destinations
import com.project.samay.presentation.NavHomeScreen
import com.project.samay.presentation.components.PrimaryAppButton

@Composable
fun PermissionScreen(viewModel: OnboardingViewModel, navController: NavController){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    if(viewModel.visiblePermissionDialogQueue.isEmpty()){
        navController.navigate(NavHomeScreen)
    }

    Column(
        modifier = Modifier.verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .fillMaxSize()
    ) {
        Text(viewModel.visiblePermissionDialogQueue.size.toString()+ " Permissions Missing!!",
            style = MaterialTheme.typography.headlineLarge)
        PermissionsRequired.entries.toList().forEach { permissionRequired ->

            AnimatedVisibility(viewModel.visiblePermissionDialogQueue.contains(permissionRequired)) {

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { viewModel.onPermissionInteractionResult(permissionRequired,it)}
                PermissionCard(
                    permissionsRequired = permissionRequired,
                    isPermanentlyDeclined = shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        permissionRequired.permission
                    ),
                    onOkClick = {
                        viewModel.dismissDialogue(permissionRequired)
                        launcher.launch(permissionRequired.permission)
                    },
                    onGoToAppSettingsClick = {
                        viewModel.dismissDialogue(permissionRequired)
                        context.openAppSettings()
                    },
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    permissionsRequired: PermissionsRequired,
    isPermanentlyDeclined: Boolean,
    onOkClick: ()->Unit,
    onGoToAppSettingsClick: ()->Unit,
    modifier : Modifier = Modifier
){
    Card(
        modifier = modifier
    ) {
        Column (
            modifier = Modifier.padding(12.dp)
        ){
            Row {
                Icon(
                    modifier = Modifier.padding(4.dp),
                    imageVector=permissionsRequired.image,
                    contentDescription = stringResource(R.string.permission_image),

                )
                Column {
                    Text(permissionsRequired.title,
                        style = MaterialTheme.typography.titleLarge)
                    Text(
                        if(!isPermanentlyDeclined)
                        permissionsRequired.rationaleText
                        else
                        permissionsRequired.permanentlyDeclinedText,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            PrimaryAppButton(
                onClick = if(isPermanentlyDeclined) onGoToAppSettingsClick else onOkClick,
                text = if(isPermanentlyDeclined) "Go to Settings" else "Grant",
                modifier = Modifier.padding(horizontal = 8.dp)
                    .fillMaxSize(0.8f)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

fun Activity.openAppSettings(){
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
