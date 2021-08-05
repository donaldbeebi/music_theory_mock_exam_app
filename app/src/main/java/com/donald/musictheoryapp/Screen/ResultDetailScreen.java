package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.QuestionDisplayUnit.QuestionDisplayUnitLoader;
import com.donald.musictheoryapp.R;

public class ResultDetailScreen extends Screen
{
    public interface OnReturnToOverviewListener { void onReturnToOverview(); }

    private QuestionArray m_Questions;
    private final QuestionDisplayUnitLoader m_UnitLoader;
    private int m_CurrentQuestion;
    private final OnReturnToOverviewListener m_OnReturnToOverviewListener;

    private final TextView m_ProgressDisplay;
    private final Button m_PreviousButton;
    private final Button m_NextButton;


    public ResultDetailScreen(Context context, View view, OnReturnToOverviewListener listener)
    {
        super(context, view);

        m_UnitLoader = new QuestionDisplayUnitLoader(this, false);
        m_OnReturnToOverviewListener = listener;

        m_ProgressDisplay = view.findViewById(R.id.question_progress);
        m_PreviousButton = view.findViewById(R.id.question_previous_button);
        m_NextButton = view.findViewById(R.id.question_next_button);


        view.findViewById(R.id.question_timer_head).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.question_timer_display).setVisibility(View.INVISIBLE);
        ((TextView) view.findViewById(R.id.question_finish_button)).setText(R.string.return_to_overview_text);
    }

    public void setQuestionDisplayUnits(QuestionArray questions)
    {
        m_Questions = questions;
    }

    public void displayQuestion(int questionIndex)
    {

        int unitIndex = -1;
        for(int i = 0; questionIndex >= 0; i++)
        {
            questionIndex -= m_QuestionDisplayUnits[i].getNumberOfQuestions();
            unitIndex = i;
        }
        m_CurrentQuestion = unitIndex;

        /*
         * Setting up the progress view
         */
        m_ProgressDisplay.setText((m_CurrentQuestion + 1) + "/" + m_QuestionDisplayUnits.length);

        /*
         * Setting up the buttons
         */
        m_PreviousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(m_CurrentQuestion > 0)
                {
                    m_CurrentQuestion--;
                    //m_UnitLoader.displayQuestion(m_QuestionDisplayUnits[m_CurrentQuestion]);
                    m_ProgressDisplay.setText((m_CurrentQuestion + 1) + "/" + m_QuestionDisplayUnits.length);
                }
                updatePreviousNextButton();
            }
        });

        m_NextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(m_CurrentQuestion < m_QuestionDisplayUnits.length - 1)
                {
                    m_CurrentQuestion++;
                    //m_UnitLoader.displayQuestion(m_QuestionDisplayUnits[m_CurrentQuestion]);
                    m_ProgressDisplay.setText((m_CurrentQuestion + 1) + "/" + m_QuestionDisplayUnits.length);
                }
                updatePreviousNextButton();
            }
        });

        updatePreviousNextButton();

        Button finishButton = getView().findViewById(R.id.question_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                m_OnReturnToOverviewListener.onReturnToOverview();
            }
        });

        //m_UnitLoader.displayQuestion(m_QuestionDisplayUnits[m_CurrentQuestion]);
    }

    /*
     * ******************
     * INTERNAL FUNCTIONS
     * ******************
     */

    private void updatePreviousNextButton()
    {
        m_PreviousButton.setEnabled(m_CurrentQuestion != 0);
        m_NextButton.setEnabled(m_CurrentQuestion != m_QuestionDisplayUnits.length - 1);
    }
}
