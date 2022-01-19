package com.donald.musictheoryapp.Screen;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.donald.musictheoryapp.BuildConfig;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.Question.QuestionDisplayHelper;
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
    private boolean hiddenAnswerPanel = false;

    private int currentQuestion;
    private final QuestionDisplayHelper displayHelper;
    private QuestionArray questions;
    private final OnFinishExerciseListener onFinishExerciseListener;
    private final OnReturnToOverviewListener onReturnToOverviewListener;
    private final ExerciseTimer exerciseTimer;

    // Views
    private final TextView progressDisplay;
    private final CardView questionAnswerPanel;
    private final Button previousButton;
    private final Button nextButton;
    private final Button finishButton;

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

        progressDisplay = view.findViewById(R.id.question_progress);
        questionAnswerPanel = view.findViewById(R.id.question_answer_panel);
        previousButton = view.findViewById(R.id.question_previous_button);
        nextButton = view.findViewById(R.id.question_next_button);
        finishButton = view.findViewById(R.id.question_finish_button);
        Button debugButton = view.findViewById(R.id.question_debug_button);

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
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                changeQuestion(true);
                updatePreviousNextButton();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!readingMode) QuestionScreen.this.onFinishExerciseListener.onFinishExercise();
                else QuestionScreen.this.onReturnToOverviewListener.onReturnToOverview();
            }
        });

        debugButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(hiddenAnswerPanel) questionAnswerPanel.setVisibility(View.VISIBLE);
                else questionAnswerPanel.setVisibility(View.GONE);
                hiddenAnswerPanel = !hiddenAnswerPanel;
            }
        });
    }

    public void setQuestions(QuestionArray questions)
    {
        this.questions = questions; currentQuestion = 0;
    }

    public void startExercise()
    {
        if(BuildConfig.DEBUG && questions == null)
            throw new AssertionError("No questions set. Make sure to call setQuestions(QuestionArray) first.");

        readingMode = false;
        displayHelper.setReadingMode(readingMode);

        /*
         * Setting up the progress view
         */
        TextView progressTextView = getView().findViewById(R.id.question_progress);
        progressTextView.setText("1/" + questions.numberOfQuestions());

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
        finishButton.setText(R.string.return_to_overview_text);

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
        progressDisplay.setText((currentQuestion + 1) + "/" + questions.numberOfQuestions());
        updatePreviousNextButton();
    }

    private void changeQuestion(boolean nextQuestion)
    {
        if(nextQuestion && currentQuestion < questions.numberOfQuestions() - 1) currentQuestion++;
        else if (!nextQuestion && currentQuestion > 0) currentQuestion--;
        displayCurrentQuestion();
        hiddenAnswerPanel = false;
        questionAnswerPanel.setVisibility(View.VISIBLE);
    }

    private void updatePreviousNextButton()
    {
        if(questions != null)
        {
            previousButton.setEnabled(currentQuestion != 0);
            nextButton.setEnabled(currentQuestion != questions.numberOfQuestions() - 1);
        }
        else Log.e("QuestionScreen", "updatePreviousNextButton() called before questions are set!");
    }
}
