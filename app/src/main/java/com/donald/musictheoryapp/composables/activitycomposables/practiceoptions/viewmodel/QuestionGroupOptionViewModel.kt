package com.donald.musictheoryapp.composables.activitycomposables.practiceoptions.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class QuestionGroupOptionViewModel(
    val number: Int,
    val identifier: String,
    val name: String,
    count: Int
) {
    var count by mutableStateOf(count)
}