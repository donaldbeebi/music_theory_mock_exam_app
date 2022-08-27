package com.donald.musictheoryapp.util

import android.content.Context
import android.widget.Toast
import com.donald.musictheoryapp.R

fun displayToastForJsonError(context: Context) {
    Toast.makeText(context, R.string.toast_json_parsing_error, Toast.LENGTH_LONG).show()
}

fun displayToastForAccessTokenError(context: Context) {
    Toast.makeText(context, R.string.toast_access_token_error, Toast.LENGTH_LONG).show()
}

fun displayToastForZeroQuestionGroups(context: Context) {
    Toast.makeText(context, R.string.toast_zero_question_groups, Toast.LENGTH_LONG).show()
}

fun displayToastForSuccessfulExerciseDeletion(context: Context) {
    Toast.makeText(context, R.string.toast_successful_exercise_deletion, Toast.LENGTH_LONG).show()
}

fun displayToastForFailedExerciseDeletion(context: Context) {
    Toast.makeText(context, R.string.toast_failed_exercise_deletion, Toast.LENGTH_LONG).show()
}

fun displayToastForFailedExerciseRetrieval(context: Context) {
    Toast.makeText(context, R.string.toast_failed_exercise_retrieval, Toast.LENGTH_LONG).show()
}

fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.displayToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}