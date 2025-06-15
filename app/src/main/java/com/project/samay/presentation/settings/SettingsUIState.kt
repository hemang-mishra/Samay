package com.project.samay.presentation.settings

import com.project.samay.domain.model.ResponseError

data class SettingsUIState(
    val isLoading: Boolean = true,
    val error: ResponseError? = null,
    val isColorPickerForSettingsVisible: Boolean = false,
    val selectedProductivityLevel: String? = null,
    val exportedJson: String? = null,
    val isExportDialogVisible: Boolean = false,
    val isImportDialogVisible: Boolean = false
)