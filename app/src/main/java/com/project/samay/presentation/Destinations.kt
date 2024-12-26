package com.project.samay.presentation

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    data object OnboardingScreen: Destinations

    @Serializable
    data object MeditationScreen: Destinations

    @Serializable
    data object BackupScreen: Destinations

    @Serializable
    data object SettingsScreen: Destinations

    @Serializable
    data object HistoryScreen: Destinations

}