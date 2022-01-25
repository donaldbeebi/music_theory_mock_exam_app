package com.donald.musictheoryapp.Screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.donald.musictheoryapp.BuildConfig;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.Question.QuestionDisplayHelper;
import com.donald.musictheoryapp.R;
import com.donald.musictheoryapp.Utils.ProgressBarOnTouchListener;
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
        private final TextView questionTimerTextView;

        ExerciseTimer(int timeInSeconds, TextView questionTimerTextView)
        { super(timeInSeconds * 1000, 1000); this.questionTimerTextView = questionTimerTextView; }

        @Override
        public void onTick(long timeRemainingInMillis) { questionTimerTextView.setText(TimeFormatter.convert(timeRemainingInMillis)); }

        @Override
        public void onFinish() { questionTimerTextView.setText(R.string.times_up_text); }
    }

    /*
     * ******
     * FIELDS
     * ******
     */

    private boolean readingMode = false;

    private int currentQuestion;
    private final QuestionDisplayHelper displayHelper;
    private QuestionArray questions;
    private final OnFinishExerciseListener onFinishExerciseListener;
    private final OnReturnToOverviewListener onReturnToOverviewListener;
    private final ExerciseTimer exerciseTimer;

    // Views
    private final Button previousButton;
    private final Button nextButton;
    private final SeekBar progressBar;

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

        displayHelper = new QuestionDisplayHelper(this);
        this.onFinishExerciseListener = onFinishExerciseListener;
        this.onReturnToOverviewListener = onReturnToOverviewListener;
        exerciseTimer = new ExerciseTimer(60 * 60 * 2, view.findViewById(R.id.question_timer_display));
        currentQuestion = 0;

        previousButton = view.findViewById(R.id.question_previous_button);
        nextButton = view.findViewById(R.id.question_next_button);
        progressBar = view.findViewById(R.id.question_progress_bar);

        /*
         *  SETTING UP LISTENERS
         */
        previousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                changeQuestion(false);
                updatePreviousNextButton();
                progressBar.setProgress(currentQuestion);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(currentQuestion == questions.numberOfQuestions() - 1)
                {
                    if(!readingMode) QuestionScreen.this.onFinishExerciseListener.onFinishExercise();
                    else QuestionScreen.this.onReturnToOverviewListener.onReturnToOverview();
                }
                else
                {
                    changeQuestion(true);
                }
                updatePreviousNextButton();
                progressBar.setProgress(currentQuestion);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setQuestions(QuestionArray questions)
    {
        this.questions = questions;
        currentQuestion = 0;
        progressBar.setMin(0);
        progressBar.setMax(questions.numberOfQuestions() - 1);
        progressBar.setOnTouchListener(
            new ProgressBarOnTouchListener(progressBar)
        );
        progressBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    setCurrentQuestion(progress);
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) { /* do nothing */ }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { /* do nothing */ }
            }
        );
    }

    public void startExercise()
    {
        if(BuildConfig.DEBUG && questions == null)
            throw new AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.");

        readingMode = false;
        displayHelper.setReadingMode(readingMode);
        displayCurrentQuestion();
    }

    public QuestionArray getQuestions()
    {
        return questions;
    }

    public void displayQuestion(int questionIndex)
    {
        if(BuildConfig.DEBUG && questions == null)
            throw new AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.");

        readingMode = true;
        currentQuestion = questionIndex;
        displayHelper.setReadingMode(true);

        displayCurrentQuestion();
    }

    public void startTimer()
    {
        exerciseTimer.start();
    }

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
        displayHelper.displayQuestion(questions.question(currentQuestion));
        updatePreviousNextButton();
    }

    private void changeQuestion(boolean nextQuestion)
    {
        if(nextQuestion && currentQuestion < questions.numberOfQuestions() - 1) currentQuestion++;
        else if (!nextQuestion && currentQuestion > 0) currentQuestion--;
        displayCurrentQuestion();
    }

    private void setCurrentQuestion(int questionIndex)
    {
        if(questionIndex < 0 || questionIndex > questions.numberOfQuestions() - 1)
        {
            throw new IllegalStateException();
        }
        currentQuestion = questionIndex;
        displayCurrentQuestion();
    }

    private void updatePreviousNextButton()
    {
        if(questions != null)
        {
            previousButton.setEnabled(currentQuestion != 0);
            if(currentQuestion == questions.numberOfQuestions() - 1)
            {
                nextButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
            else
            {
                nextButton.setBackgroundTintList(ColorStateList.valueOf(
                    context().getResources().getColor(
                        R.color.design_default_color_primary, context().getTheme())
                ));
            }
            //nextButton.setEnabled(currentQuestion != questions.numberOfQuestions() - 1);
        }
        else throw new IllegalStateException(
            "updatePreviousNextButton() called before questions are set!"
        );
    }
}
