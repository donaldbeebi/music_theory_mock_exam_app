package com.donald.musictheoryapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.donald.musictheoryapp.Exercise.ExerciseGenerator;
import com.donald.musictheoryapp.QuestionArray.QuestionArray;
import com.donald.musictheoryapp.Screen.FinishExerciseConfirmationDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.donald.musictheoryapp.Screen.ResultOverviewScreen;
import com.donald.musictheoryapp.Screen.ExerciseMenuScreen;
import com.donald.musictheoryapp.Screen.QuestionScreen;
import com.donald.musictheoryapp.Screen.Screen;
import com.donald.musictheoryapp.Utils.Note;

public class MainActivity extends AppCompatActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener,
    ExerciseMenuScreen.OnStartExerciseListener,
    QuestionScreen.OnFinishExerciseListener,
    QuestionScreen.OnReturnToOverviewListener,
    FinishExerciseConfirmationDialog.OnConfirmDialogListener,
    ResultOverviewScreen.OnProceedToDetailListener
{
    private ExerciseMenuScreen m_ExerciseMenuScreen;
    private QuestionScreen m_QuestionScreen;
    private ResultOverviewScreen m_ResultOverviewScreen;
    private Screen m_CurrentScreenForExerciseTab;

    private ViewGroup m_MainFrame;

    private ExerciseGenerator m_ExerciseGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((BottomNavigationView) findViewById(R.id.bottom_navigation_view)).setOnNavigationItemSelectedListener(this);

        m_MainFrame = findViewById(R.id.main_frame_layout);

        m_ExerciseMenuScreen = new ExerciseMenuScreen(this, getLayoutInflater().inflate(R.layout.screen_exercise_menu, null), this);
        m_QuestionScreen = new QuestionScreen(this, getLayoutInflater().inflate(R.layout.screen_question, null), this, this);
        m_ResultOverviewScreen = new ResultOverviewScreen(this, getLayoutInflater().inflate(R.layout.screen_result_overview, null), this);

        m_CurrentScreenForExerciseTab = m_ExerciseMenuScreen;
        m_MainFrame.addView(m_CurrentScreenForExerciseTab.getView());

        m_ExerciseGenerator = new ExerciseGenerator(this);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                if(m_CurrentScreenForExerciseTab == m_QuestionScreen)
                    m_QuestionScreen.onBackPressed();
            }
        });

        for(int i = 2; i < 450; i++)
        {
            Log.d("note id", i + " " + new Note(i).getStringWithRange());
        }
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.exercise_nav_button)
        {
            m_MainFrame.removeAllViews();
            m_MainFrame.addView(m_CurrentScreenForExerciseTab.getView());
        }

        else
        {
            m_MainFrame.removeAllViews();
            m_MainFrame.addView(m_ResultOverviewScreen.getView());
        }

        return true;
    }

    @Override
    public void onStartExercise()
    {
        m_MainFrame.removeAllViews();
        m_QuestionScreen.setQuestions(m_ExerciseGenerator.generateExercise());
        m_QuestionScreen.startExercise();
        m_QuestionScreen.startTimer();
        m_CurrentScreenForExerciseTab = m_QuestionScreen;
        m_MainFrame.addView(m_CurrentScreenForExerciseTab.getView());
    }

    @Override
    public void onFinishExercise()
    {
        FinishExerciseConfirmationDialog dialog = new FinishExerciseConfirmationDialog(this);
        dialog.show(getSupportFragmentManager(), "exercise_finish_confirmation_dialog");
    }

    @Override
    public void onConfirmDialog()
    {
        m_MainFrame.removeAllViews();
        m_ResultOverviewScreen.setQuestions(m_QuestionScreen.getQuestions());
        m_CurrentScreenForExerciseTab = m_ResultOverviewScreen;
        m_MainFrame.addView(m_CurrentScreenForExerciseTab.getView());
    }

    @Override
    public void onProceedToDetail(QuestionArray questions, int targetGroup)
    {
        m_MainFrame.removeAllViews();
        m_QuestionScreen.setQuestions(questions);
        m_QuestionScreen.displayQuestion(questions.getQuestionIndex(questions.getGroup(targetGroup).getQuestion(0)));
        m_CurrentScreenForExerciseTab = m_QuestionScreen;
        m_MainFrame.addView(m_CurrentScreenForExerciseTab.getView());
    }

    @Override
    public void onReturnToOverview()
    {
        m_MainFrame.removeAllViews();
        m_CurrentScreenForExerciseTab = m_ResultOverviewScreen;
        m_MainFrame.addView(m_CurrentScreenForExerciseTab.getView());
    }
}