package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.donald.musictheoryapp.MainActivity;
import com.donald.musictheoryapp.R;

import org.json.JSONObject;

public class ExerciseMenuScreen extends Screen
{
    public interface OnStartExerciseListener { void onStartExercise(); }

    private final OnStartExerciseListener m_OnStartExerciseListener;

    public ExerciseMenuScreen(Context context, View view, OnStartExerciseListener listener)
    {
        super(context, view);

        m_OnStartExerciseListener = listener;

        Button button = view.findViewById(R.id.exercise_menu_start_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                m_OnStartExerciseListener.onStartExercise();
            }
        });
    }
}
