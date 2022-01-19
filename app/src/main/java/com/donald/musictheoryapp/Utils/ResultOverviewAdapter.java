package com.donald.musictheoryapp.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donald.musictheoryapp.Question.QuestionGroup;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Screen.ResultOverviewScreen;

public class ResultOverviewAdapter extends RecyclerView.Adapter<ResultOverviewAdapter.ResultViewHolder>
{
    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        private final ResultOverviewScreen.OnProceedToDetailListener m_OnProceedToDetailListener;
        private final TextView m_QuestionNumber;
        private final TextView m_QuestionTitle;
        private final TextView m_QuestionScore;
        private int m_GroupIndexToDisplay;
        private QuestionArray m_Questions;

        public ResultViewHolder(View itemView, ResultOverviewScreen.OnProceedToDetailListener listener)
        {
            super(itemView);
            m_OnProceedToDetailListener = listener;
            m_QuestionNumber = itemView.findViewById(R.id.result_question_number);
            m_QuestionTitle = itemView.findViewById(R.id.result_question_title);
            m_QuestionScore = itemView.findViewById(R.id.result_question_score);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    m_OnProceedToDetailListener.onProceedToDetail(m_Questions, m_GroupIndexToDisplay);
                }
            });
        }

        public void setQuestions(QuestionArray questions) { m_Questions = questions; }

        public void bindData(int groupIndexToDisplay)
        {
            QuestionGroup group = m_Questions.group(groupIndexToDisplay);
            m_QuestionNumber.setText(String.valueOf(group.getNumber()));
            m_QuestionTitle.setText(group.getTopic());
            m_QuestionScore.setText(String.valueOf(group.totalPoints()));
            m_GroupIndexToDisplay = groupIndexToDisplay;
        }
    }

    private final QuestionArray m_Questions;
    private final ResultOverviewScreen.OnProceedToDetailListener m_OnProceedToDetailListener;

    public ResultOverviewAdapter(QuestionArray questions, ResultOverviewScreen.OnProceedToDetailListener listener)
    {
        m_Questions = questions;
        m_OnProceedToDetailListener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_overview, parent, false);
        return new ResultViewHolder(itemView, m_OnProceedToDetailListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position)
    {
        holder.setQuestions(m_Questions); // TODO: CALL THIS WHEN SETTING QUESTIONS IN ADAPTER, NOT HERE
        holder.bindData(position);
    }

    @Override
    public int getItemCount()
    {
        return m_Questions.numberOfGroups();
    }
}
