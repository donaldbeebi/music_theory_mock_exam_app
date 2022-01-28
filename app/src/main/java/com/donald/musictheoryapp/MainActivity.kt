package com.donald.musictheoryapp

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.donald.musictheoryapp.Screen.QuestionScreen.OnFinishExerciseListener
import com.donald.musictheoryapp.Screen.QuestionScreen.OnReturnToOverviewListener
import com.donald.musictheoryapp.Screen.FinishExerciseConfirmationDialog.OnConfirmDialogListener
import com.donald.musictheoryapp.Screen.ResultOverviewScreen.OnProceedToDetailListener
import android.view.ViewGroup
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import org.json.JSONObject
import org.json.JSONException
import org.xmlpull.v1.XmlPullParserException
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.donald.musictheoryapp.QuestionArray.QuestionArray
import com.donald.musictheoryapp.Screen.*
import java.io.IOException

class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        OnFinishExerciseListener,
        OnReturnToOverviewListener,
        OnConfirmDialogListener,
        OnProceedToDetailListener {

    companion object {
        const val URL = "http://161.81.107.94/"
        const val DOUBLE_BACK_PRESS_DELAY_FOR_EXIT = 2000L
    }

    private var backPressCountForExit = 0

    private lateinit var exerciseMenuScreen: ExerciseMenuScreen
    private lateinit var questionScreen: QuestionScreen
    private lateinit var resultOverviewScreen: ResultOverviewScreen
    private lateinit var currentExerciseScreen: Screen
    private lateinit var mainFrame: ViewGroup

    //private ExerciseGenerator m_ExerciseGenerator;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (findViewById<View>(R.id.bottom_navigation_view) as BottomNavigationView).setOnNavigationItemSelectedListener(this)
        mainFrame = findViewById(R.id.main_frame_layout)
        exerciseMenuScreen = ExerciseMenuScreen(this, layoutInflater.inflate(R.layout.screen_exercise_menu, null), this::onRetrieveQuestions)
        questionScreen = QuestionScreen(this, layoutInflater.inflate(R.layout.screen_question, null), this, this)
        resultOverviewScreen = ResultOverviewScreen(this, layoutInflater.inflate(R.layout.screen_result_overview, null), this)
        currentExerciseScreen = exerciseMenuScreen
        mainFrame.addView(currentExerciseScreen.view)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentExerciseScreen === questionScreen) questionScreen.onBackPressed()
            }
        })
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exercise_nav_button) {
            mainFrame.removeAllViews()
            mainFrame.addView(currentExerciseScreen.view)
        } else {
            mainFrame.removeAllViews()
            mainFrame.addView(resultOverviewScreen.view)
        }
        return true
    }

    @kotlin.Throws(JSONException::class, IOException::class, XmlPullParserException::class)
    private fun onRetrieveQuestions(jsonObject: JSONObject?) {
        mainFrame.removeAllViews()
        val questions = QuestionArray.fromJSON(jsonObject)
        questionScreen.setQuestions(questions)
        //m_QuestionScreen.setQuestions(m_ExerciseGenerator.generateExercise());
        questionScreen.startExercise()
        questionScreen.startTimer()
        currentExerciseScreen = questionScreen
        mainFrame.addView(currentExerciseScreen.view)
        exerciseMenuScreen.setStatus(getString(R.string.exercise_menu_start_button_default_text), true)
    }

    override fun onFinishExercise(outOfTime: Boolean) {
        if (outOfTime) {
            val dialog = OutOfTimeDialog { onConfirmDialog() }
            dialog.show(supportFragmentManager, "exercise_out_of_time_dialog")
        } else {
            val dialog = FinishExerciseConfirmationDialog(this)
            dialog.show(supportFragmentManager, "exercise_finish_confirmation_dialog")
        }
    }

    override fun onConfirmDialog() {
        mainFrame.removeAllViews()
        resultOverviewScreen.setQuestions(questionScreen.questions)
        currentExerciseScreen = resultOverviewScreen
        mainFrame.addView(currentExerciseScreen.view)
    }

    override fun onProceedToDetail(questions: QuestionArray, targetGroup: Int) {
        mainFrame.removeAllViews()
        questionScreen.setQuestions(questions)
        questionScreen.displayQuestion(questions.questionIndexOf(questions.groupAt(targetGroup).questions[0]))
        currentExerciseScreen = questionScreen
        mainFrame.addView(currentExerciseScreen.view)
    }

    override fun onReturnToOverview() {
        mainFrame.removeAllViews()
        currentExerciseScreen = resultOverviewScreen
        mainFrame.addView(currentExerciseScreen.view)
    }

    private fun onExit() {
        if (backPressCountForExit == 0) {
            backPressCountForExit = 1
            Toast.makeText(this, "Back again to exit the app", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed(
                { backPressCountForExit = 0 },
                DOUBLE_BACK_PRESS_DELAY_FOR_EXIT
            )
        }
        else {
            backPressCountForExit = 0
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        if (currentExerciseScreen == questionScreen && questionScreen.readingMode) {
            // reading on question screen
            onReturnToOverview()
        } else if (currentExerciseScreen == resultOverviewScreen || currentExerciseScreen == exerciseMenuScreen) {
            // result overview screen or exercise menu screen
            onExit()
        }
    }
}