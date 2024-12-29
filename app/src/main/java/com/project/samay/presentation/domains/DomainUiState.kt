package com.project.samay.presentation.domains

import com.project.samay.domain.model.CalendarColor
import com.project.samay.domain.model.DomainEntity

data class DomainUiState(
    val selectedDomain: DomainEntity? = null,
    val selectedColor: CalendarColor = CalendarColor.default,
    val isColorPickerDialogVisible: Boolean = false,
)
