package com.project.samay.presentation.onboarding

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class OnboardingViewModel: ViewModel() {
    val visiblePermissionDialogQueue = PermissionsRequired.entries.toMutableStateList()

    fun dismissDialogue(permission: PermissionsRequired){
        visiblePermissionDialogQueue.remove(permission)
    }

    fun onPermissionInteractionResult(
        permissionsRequired: PermissionsRequired,
        isGranted: Boolean
    ){
        if(!isGranted){
            visiblePermissionDialogQueue.add(permissionsRequired)
        }else{
            visiblePermissionDialogQueue.remove(permissionsRequired)
        }
    }

    fun areAllPermissionsGranted() = (visiblePermissionDialogQueue.isEmpty())
}