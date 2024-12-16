package com.project.samay.presentation.meditate

enum class MeditationCategories(val title: String, val durationInSec: Int, val reason: String, val mp3FileName: String) {
    MINI("Mini", 60*3, "Mind is already focused. Just need to focus it in a certain direction.", "mini.mp3"),
    CALM("Calm", 60*5, "Mind has little thoughts. Need to focus it.", "calm.mp3"),
    FOCUS("Focus", 60*10, "Mind is wandering. Need to bring it back to focus.", "focus.mp3"),
    YOGA_NIDRA("Yoga Nidra", 60*15, "Mind and mind is tired. Need to relax it.", "yoga_nidra.mp3"),
    CLEANING("Cleaning", 60*20, "Mind is full of negativity and restlessness. Need to clean it.", "cleaning.mp3"),
}