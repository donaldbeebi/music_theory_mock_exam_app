package com.donald.musictheoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.Screen.ResultOverviewScreen;

public class ResultOverviewAdapter extends RecyclerView.Adapter<ResultOverviewAdapter.ResultViewHolder>
{
    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        private final ResultOverviewScreen.OnProceedToDetailListener m_OnProceedToDetailListener;
        private final TextView m_QuestionNumber;
        private final TextView m_QuestionTitle;
        private final ImageView m_QuestionScore;
        private int m_QuestionIndex;

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
                    m_OnProceedToDetailListener.onProceedToDetail(m_QuestionIndex);
                }
            });
        }

        public void bindData(String questionNumber, String questionTitle, boolean isCorrect, int questionIndex)
        {
            m_QuestionNumber.setText(questionNumber);
            m_QuestionTitle.setText(questionTitle);
            if(isCorrect) m_QuestionScore.setImageResource(R.drawable.image_result_overview_check);
            else m_QuestionScore.setImageResource(R.drawable.image_result_overview_cross);
            m_QuestionIndex = questionIndex;
        }
    }

    private static class QuestionInfoStruct
    {
        int index;
        int section;
        int number;
        String questionType;
        int score = 0;
        int maxScore = 0;
    }

    //private final Question[] m_Questions;
    private QuestionInfoStruct[] m_Structs;
    private final QuestionArray m_Questions;
    private final ResultOverviewScreen.OnProceedToDetailListener m_OnProceedToDetailListener;

    public ResultOverviewAdapter(QuestionArray questions, ResultOverviewScreen.OnProceedToDetailListener listener)
    {
        m_Questions = questions;
        // calculating required size
        /*
        int size = 0;
        for (QuestionDisplayUnit questionDisplayUnit : questionDisplayUnits)
        {
            if (questionDisplayUnit.getSubNumber() < 2) size++;
        }
        m_Structs = new QuestionInfoStruct[size];

        // populating with uninitialized structs
        for (int i = 0; i < m_Structs.length; i++)
        {
            m_Structs[i] = new QuestionInfoStruct();
        }

        int currentStruct = -1;
        for (int i = 0; i < questionDisplayUnits.length; i++)
        {
            // if it is a parent question
            if(questionDisplayUnits[i].getSubNumber() < 2)
            {
                currentStruct++;
                m_Structs[currentStruct].index = i;
                m_Structs[currentStruct].section = questionDisplayUnits[i].getSection();
                m_Structs[currentStruct].number = questionDisplayUnits[i].getNumber();
                m_Structs[currentStruct].questionType = questionDisplayUnits[i].getQuestionType();
                m_Structs[currentStruct].score = questionDisplayUnits[i].getTotalScore();
                m_Structs[currentStruct].maxScore = questionDisplayUnits[i].getCorrectAnswers().length;
            }
            else
            {
                m_Structs[currentStruct].score += questionDisplayUnits[i].getTotalScore();
                m_Structs[currentStruct].maxScore += questionDisplayUnits[i].getCorrectAnswers().length;
            }
        }

         */

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
        // 1. question number
        /*
        char subNumberChar = 0;
        if(m_Questions.getNumberOfSubQuestionsForQuestionIndex(position) > 1)
            subNumberChar = (char) ('a' + m_Questions.getQuestion(position).getSubNumber() - 1);

        holder.bindData(String.valueOf(m_Questions.getQuestion(position).getNumber()) + subNumberChar,
            m_Questions.getQuestion(position).getTopic(),
            m_Questions.getQuestion(position).isCorrect(),
            position);

         */
    }

    @Override
    public int getItemCount()
    {
        return m_Questions.getNumberOfQuestions();
    }
}
