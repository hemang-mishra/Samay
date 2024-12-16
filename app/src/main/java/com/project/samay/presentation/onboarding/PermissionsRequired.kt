package com.project.samay.presentation.onboarding

import android.Manifest

enum class PermissionsRequired(
    val permission: String,
    val title: String,
    val permanentlyDeclinedText: String,
    val rationaleText: String
) {
    POST_NOTIFICATION(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        title = "Notification Permission Required",
        permanentlyDeclinedText = "Seems you have permanently declined this permission. Give access to this permission if you want to use view notifications.",
        rationaleText = " Give access to this permission if you want to use view notifications."
    ),
    READ_CALENDAR(
        permission = Manifest.permission.READ_CALENDAR,
        title = "Calendar Read Permission Required",
        permanentlyDeclinedText = "Seems you have permanently declined this permission. Give access to this permission if you want to use view calendar.",
        rationaleText = " Give access to this permission if you want to use view calendar."
    ),
    WRITE_CALENDAR(
        permission = Manifest.permission.WRITE_CALENDAR,
        title = "Calendar Write Permission Required",
        permanentlyDeclinedText = "Seems you have permanently declined this permission. Give access to this permission if you want to use view calendar.",
        rationaleText = " Give access to this permission if you want to use view calendar."
    ),
    FOREGROUND_SERVICE(
        permission = Manifest.permission.FOREGROUND_SERVICE,
        title = "Foreground Service Permission Required",
        permanentlyDeclinedText = "Seems you have permanently declined this permission. Give access to this permission if you want to use view foreground service.",
        rationaleText = " Give access to this permission if you want to use view foreground service."
    ),
    FOREGROUND_SERVICE_TYPE(
        permission = Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
        title = "Foreground Service Type Permission Required",
        permanentlyDeclinedText = "Seems you have permanently declined this permission. Give access to this permission if you want to use view foreground service type.",
        rationaleText = " Give access to this permission if you want to use view foreground service type."
    ),

}