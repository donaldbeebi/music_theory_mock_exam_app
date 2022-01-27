package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.R;

public class ResultOverviewScreen extends Screen
{
    public interface OnProceedToDetailListener
    { void onProceedToDetail(QuestionArray questions, int targetGroup); }

    private RecyclerView recyclerView;
    private ResultOverviewAdapter adapter;
    private final OnProceedToDetailListener onProceedToDetailListener;

    public ResultOverviewScreen(Context context, View view, OnProceedToDetailListener listener) {
        super(context, view);
        onProceedToDetailListener = listener;
    }

    public void setQuestions(QuestionArray questions) {
        // load questions
        recyclerView = getView().findViewById(R.id.result_recycler_view);
        adapter = new ResultOverviewAdapter(questions, onProceedToDetailListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context()));
    }
}
