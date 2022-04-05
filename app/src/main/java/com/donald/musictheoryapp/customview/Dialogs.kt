package com.donald.musictheoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment

class PauseExerciseDialog(private val callback: () -> Unit) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).setTitle(R.string.pause_exercise_dialog_title)
            .setMessage(R.string.pause_exercise_dialog_message)
            .setPositiveButton(R.string.pause_exercise_dialog_confirm) { _, _ -> callback() }
            .setNegativeButton(R.string.pause_exercise_back_to_exercise) { _, _ -> /* do nothing */ }
            .create()
    }

}

class EndExerciseDialog(private val endExercise: () -> Unit, private val saveExercise: () -> Unit) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).setTitle(R.string.exit_exercise_dialog_title)
            .setMessage(R.string.exit_exercise_dialog_message)
            .setPositiveButton(R.string.exit_exercise_dialog_end) { _, _ -> endExercise() }
            .setNegativeButton(R.string.exit_exercise_dialog_save) { _, _ -> saveExercise() }
            .setNeutralButton(R.string.exit_exercise_dialog_back_to_exercise) { _, _ -> /* do nothing */ }
            .create()
    }

}

class TimeOutDialog(private val callback: () -> Unit) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).setTitle(getString(R.string.out_of_time_dialog_title))
            .setPositiveButton(getString(R.string.ok_dialog_confirmation)) { _, _ -> callback() }
            .create()
    }

}

class SignUpDialog(private val platform: String, private val callback: (String) -> Unit) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = it.layoutInflater.inflate(R.layout.dialog_sign_in, null).apply {
                findViewById<TextView>(R.id.sign_up_dialog_title).text = it.getString(R.string.sign_up_dialog_title, platform)
            }
            val editText = view.findViewById<EditText>(R.id.sign_up_dialog_nick_name_edit_text)
            builder.setView(view)
                .setPositiveButton(R.string.sign_up) { _, _ -> callback(editText.text.toString()) }
                .setNegativeButton(R.string.cancel) { _, _ -> /* do nothing */ }
                .create()
        } ?: throw IllegalStateException("Activity is null.")
    }

}

class DisconnectAccountDialog(private val callback: () -> Unit): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setPositiveButton(getString(R.string.ok_dialog_confirmation)) { _, _ -> callback() }
                .setNegativeButton(getString(R.string.no_thanks_dialog_confirmation)) { _, _ -> /* do nothing */ }
                .create()
        } ?: throw IllegalStateException("Activity is null.")
    }

}