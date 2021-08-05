package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donald.musictheoryapp.BuildConfig;
import com.donald.musictheoryapp.Exercise.ExerciseGenerator;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.QuestionDisplayUnit.QuestionDisplayHelper;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Utils.TimeFormatter;

public class QuestionScreen extends Screen
{
    /*
     * **********************
     * CLASSES AND INTERFACES
     * **********************
     */

    public interface OnFinishExerciseListener { void onFinishExercise(); }
    public interface OnReturnToOverviewListener { void onReturnToOverview(); }

    private static class ExerciseTimer extends CountDownTimer
    {
        private final TextView m_QuestionTimerTextView;

        ExerciseTimer(int timeInSeconds, TextView questionTimerTextView)
        { super(timeInSeconds * 1000, 1000); m_QuestionTimerTextView = questionTimerTextView; }

        @Override
        public void onTick(long timeRemainingInMillis) { m_QuestionTimerTextView.setText(TimeFormatter.convert(timeRemainingInMillis)); }

        @Override
        public void onFinish() { m_QuestionTimerTextView.setText(R.string.times_up_text); }
    }

    /*
     * ******
     * FIELDS
     * ******
     */

    private boolean m_ReadingMode;

    private int m_CurrentQuestion;
    private final QuestionDisplayHelper m_DisplayHelper;
    private QuestionArray m_Questions;
    private final ExerciseGenerator m_ExerciseGenerator;
    private final OnFinishExerciseListener m_OnFinishExerciseListener;
    private final OnReturnToOverviewListener m_OnReturnToOverviewListener;
    private final ExerciseTimer m_ExerciseTimer;

    // Views
    private final TextView m_ProgressDisplay;
    private final Button m_PreviousButton;
    private final Button m_NextButton;
    private final Button m_FinishButton;

    /*
     * *******
     * METHODS
     * *******
     */

    public QuestionScreen(Context context, View view,
                          OnFinishExerciseListener onFinishExerciseListener,
                          OnReturnToOverviewListener onReturnToOverviewListener)
    {
        super(context, view);

        m_DisplayHelper = new QuestionDisplayHelper(this);
        m_OnFinishExerciseListener = onFinishExerciseListener;
        m_OnReturnToOverviewListener = onReturnToOverviewListener;
        m_ExerciseTimer = new ExerciseTimer(60 * 60 * 2, view.findViewById(R.id.question_timer_display));
        m_CurrentQuestion = 0;
        m_ExerciseGenerator = new ExerciseGenerator(getContext());

        m_ProgressDisplay = view.findViewById(R.id.question_progress);
        m_PreviousButton = view.findViewById(R.id.question_previous_button);
        m_NextButton = view.findViewById(R.id.question_next_button);
        m_FinishButton = view.findViewById(R.id.question_finish_button);

        /*
         *  SETTING UP LISTENERS
         */
        m_PreviousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                changeQuestion(false);
                updatePreviousNextButton();
            }
        });

        m_NextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                changeQuestion(true);
                updatePreviousNextButton();
            }
        });

        m_FinishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!m_ReadingMode) m_OnFinishExerciseListener.onFinishExercise();
                else m_OnReturnToOverviewListener.onReturnToOverview();
            }
        });
    }

    public void setQuestions(QuestionArray questions) { m_Questions = questions; m_CurrentQuestion = 0; }

    public void startExercise()
    {
        if(BuildConfig.DEBUG && m_Questions == null)
            throw new AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.");

        m_ReadingMode = false;
        m_DisplayHelper.setReadingMode(m_ReadingMode);

        /*
         * Setting up the progress view
         */
        TextView progressTextView = getView().findViewById(R.id.question_progress);
        progressTextView.setText("1/" + m_Questions.getNumberOfQuestions());

        displayCurrentQuestion();
    }

    public QuestionArray getQuestions() { return m_Questions; }

    public void displayQuestion(int questionIndex)
    {
        if(BuildConfig.DEBUG && m_Questions == null)
            throw new AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.");

        m_ReadingMode = true;
        m_CurrentQuestion = questionIndex;
        m_DisplayHelper.setReadingMode(true);
        m_FinishButton.setText(R.string.return_to_overview_text);

        displayCurrentQuestion();
    }

    public void startTimer() { m_ExerciseTimer.start(); }

    public void onBackPressed()
    {
        changeQuestion(false);
        updatePreviousNextButton();
    }

    /*
     * ******************
     * INTERNAL FUNCTIONS
     * ******************
     */

    private void displayCurrentQuestion()
    {
        m_DisplayHelper.displayQuestion(m_Questions.getQuestion(m_CurrentQuestion));
        m_ProgressDisplay.setText((m_CurrentQuestion + 1) + "/" + m_Questions.getNumberOfQuestions());
        updatePreviousNextButton();
    }

    private void changeQuestion(boolean nextQuestion)
    {
        if(nextQuestion && m_CurrentQuestion < m_Questions.getNumberOfQuestions() - 1) m_CurrentQuestion++;
        else if (!nextQuestion && m_CurrentQuestion > 0) m_CurrentQuestion--;
        displayCurrentQuestion();
    }

    private void updatePreviousNextButton()
    {
        if(m_Questions != null)
        {
            m_PreviousButton.setEnabled(m_CurrentQuestion != 0);
            m_NextButton.setEnabled(m_CurrentQuestion != m_Questions.getNumberOfQuestions() - 1);
        }
        else Log.e("QuestionScreen", "updatePreviousNextButton() called before questions are set!");
    }
}
