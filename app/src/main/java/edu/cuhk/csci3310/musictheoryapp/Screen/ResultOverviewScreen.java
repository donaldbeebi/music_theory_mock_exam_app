package edu.cuhk.csci3310.musictheoryapp.Screen;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.cuhk.csci3310.musictheoryapp.QuestionArray.QuestionArray;
import edu.cuhk.csci3310.musictheoryapp.R;
import edu.cuhk.csci3310.musictheoryapp.ResultOverviewAdapter;
import edu.cuhk.csci3310.musictheoryapp.Screen.Screen;

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
