package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.ResultOverviewAdapter;

public class ResultOverviewScreen extends Screen
{
    public interface OnProceedToDetailListener { void onProceedToDetail(int targetQuestion); }

    private RecyclerView m_RecyclerView;
    private ResultOverviewAdapter m_Adapter;
    private final OnProceedToDetailListener m_OnProceedToDetailListener;

    public ResultOverviewScreen(Context context, View view, OnProceedToDetailListener listener) {
        super(context, view);
        m_OnProceedToDetailListener = listener;
    }

    public void setQuestions(QuestionArray questions) {
        // load questions
        m_RecyclerView = getView().findViewById(R.id.result_recycler_view);
        m_Adapter = new ResultOverviewAdapter(questions, m_OnProceedToDetailListener);
        m_RecyclerView.setAdapter(m_Adapter);
        m_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
