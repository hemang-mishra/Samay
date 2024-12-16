package com.project.samay.presentation.onboarding

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class OnboardingViewModel: ViewModel() {
    val visiblePermissionDialogQueue = mutableStateListOf<PermissionsRequired>()

    fun dismissDialogue(){
        if(visiblePermissionDialogQueue.isNotEmpty())
            visiblePermissionDialogQueue.removeAt(visiblePermissionDialogQueue.lastIndex)
        else
            Log.i("OnboardingViewModel", "PermissionDialogue is empty")
    }

    fun onPermissionInteractionResult(
        permissionsRequired: PermissionsRequired,
        isGranted: Boolean
    ){
        if(!isGranted){
            visiblePermissionDialogQueue.add(0,permissionsRequired)
        }
    }
}