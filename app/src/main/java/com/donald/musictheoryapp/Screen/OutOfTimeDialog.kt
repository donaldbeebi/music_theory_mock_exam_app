package com.donald.musictheoryapp.Screen

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.donald.musictheoryapp.Screen.FinishExerciseConfirmationDialog
import android.os.Bundle
import android.content.DialogInterface

class OutOfTimeDialog(private var callback: () -> Unit) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Time is up")
            .setPositiveButton("Ok") { _, _ -> callback() }
        return builder.create()
    }
}