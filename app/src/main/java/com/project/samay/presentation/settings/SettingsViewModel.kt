package com.project.samay.presentation.settings

import androidx.lifecycle.viewModelScope
import com.project.samay.domain.usecases.BackupUseCases
import com.project.samay.presentation.BaseViewModel
import com.project.samay.presentation.domains.DomainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val backupUseCases: BackupUseCases
) : BaseViewModel<SettingsUIState>() {
    override val uiState: StateFlow<SettingsUIState>
        get() = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<SettingsUIState> =
        MutableStateFlow(SettingsUIState())

    fun backupDomainsAndHistory() {
        viewModelScope.launch {
            backupUseCases.backupDomainsAndHistory()
        }
    }

    fun fetchDomainsAndHistory() {
        viewModelScope.launch {
            backupUseCases.fetchDomainsAndHistory()
        }
    }
}