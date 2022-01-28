package com.donald.musictheoryapp.Screen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class FinishExerciseConfirmationDialog extends AppCompatDialogFragment
{
    public interface OnConfirmDialogListener { void onConfirmDialog(); }

    private final OnConfirmDialogListener m_OnConfirmDialogListener;

    public FinishExerciseConfirmationDialog(OnConfirmDialogListener listener)
    {
        m_OnConfirmDialogListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm finishing exercise")
                .setPositiveButton("Finish", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        m_OnConfirmDialogListener.onConfirmDialog();
                    }
                })
                .setNegativeButton("Back to exercise", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //do nothing
                    }
                });
        return builder.create();
    }
}
