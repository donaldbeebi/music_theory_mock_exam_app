package edu.cuhk.csci3310.musictheoryapp.Screen;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import edu.cuhk.csci3310.musictheoryapp.R;

public class ExerciseMenuScreen extends Screen
{
    public interface OnStartExerciseListener { void onStartExercise(); }

    OnStartExerciseListener m_OnStartExerciseListener;

    public ExerciseMenuScreen(Context context, View view, OnStartExerciseListener listener)
    {
        super(context, view);

        m_OnStartExerciseListener = listener;

        Button button = view.findViewById(R.id.exercise_menu_start_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                m_OnStartExerciseListener.onStartExercise();
            }
        });
    }
}
