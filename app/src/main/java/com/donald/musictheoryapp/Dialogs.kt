package com.donald.musictheoryapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.donald.musictheoryapp.question.Exercise

class FinishExerciseConfirmationDialog(private val callback: () -> Unit) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).setTitle("Confirm finishing exercise")
            .setPositiveButton("Finish") { _, _ -> callback() }
            .setNegativeButton("Back to exercise") { _, _ -> /* do nothing */ }
            .create()
    }
}

class OutOfTimeDialog(private val callback: () -> Unit) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).setTitle(getString(R.string.out_of_time_dialog_title))
            .setPositiveButton(getString(R.string.ok_dialog_confirmation)) { _, _ -> callback() }
            .create()
    }
}

class SignUpOldDialog(private val callback: () -> Unit) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).setTitle(getString(R.string.sign_up_dialog_title))
            .setPositiveButton(getString(R.string.ok_dialog_confirmation)) { _, _ -> callback() }
            .setNegativeButton(getString(R.string.no_thanks_dialog_confirmation)) { _, _ -> /* do nothing */ }
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