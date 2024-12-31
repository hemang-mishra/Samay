package com.project.samay.presentation.settings

import com.project.samay.domain.model.ResponseError

data class SettingsUIState(
    val isLoading: Boolean = true,
    val error: ResponseError? = null
)