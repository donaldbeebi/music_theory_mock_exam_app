package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

public abstract class Screen
{
    private final Context m_Context;
    private final View m_View;
    private final LayoutInflater m_LayoutInflater;

    Screen(Context context, View view) {
        m_Context = context;
        m_View = view;
        m_LayoutInflater = LayoutInflater.from(context);
    }

    public Context getContext() { return m_Context; }

    public View getView() { return m_View; }

    public LayoutInflater getLayoutInflater() { return m_LayoutInflater; }

    protected void onAttach() {} //TODO: MAKE THIS ABSTRACT

    public void attachToFrame(FrameLayout frameLayout) { frameLayout.addView(m_View); onAttach(); }
}
