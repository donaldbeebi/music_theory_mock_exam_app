package com.donald.musictheoryapp

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.donald.musictheoryapp.screen.QuestionListScreen.OnViewExerciseListener
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.screen.*

class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        OnViewExerciseListener {

    companion object {

        const val DOUBLE_BACK_PRESS_DELAY_FOR_EXIT = 2000L
        val NAVIGATION_BAR_ITEM_IDS = arrayOf(R.id.exercise_nav_button, R.id.review_nav_button)

    }

    lateinit var idToken: DecodedJWT
    lateinit var accessToken: DecodedJWT

    private var backPressCountForExit = 0

    private lateinit var exerciseMenuScreen: ExerciseMenuScreen
    private lateinit var questionScreen: QuestionScreen
    private lateinit var questionReadingScreen: QuestionReadingScreen
    private lateinit var questionListScreen: QuestionListScreen
    private lateinit var exerciseListScreen: ExerciseListScreen
    private lateinit var screens: Array<Screen>
    private lateinit var currentScreen: Screen
    private var currentScreenIndex: Int = 0
    private lateinit var mainFrame: ViewGroup
    private lateinit var navigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationBar = (findViewById<View>(R.id.bottom_navigation_view) as BottomNavigationView).apply {
            setOnNavigationItemSelectedListener(this@MainActivity)
        }

        mainFrame = findViewById(R.id.main_frame_layout)

        idToken = JWT.decode(intent.getStringExtra("id_token")!!)
        accessToken = JWT.decode(intent.getStringExtra("access_token")!!)

        exerciseMenuScreen = ExerciseMenuScreen(this, this::onRetrieveQuestions)
        questionScreen = QuestionScreen(this, this::onFinishExercise, this::onExerciseTimeOut)

        questionReadingScreen = QuestionReadingScreen(this, this::onReturnToQuestionList)
        questionListScreen = QuestionListScreen(this, this)
        exerciseListScreen = ExerciseListScreen(this, this::onViewExercise)

        screens = arrayOf(exerciseMenuScreen, exerciseListScreen)
        currentScreen = exerciseMenuScreen
        mainFrame.addView(exerciseMenuScreen.view)

        //onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
        //    override fun handleOnBackPressed() {
        //        if (currentExerciseScreen === questionScreen) questionScreen.onBackPressed()
        //    }
        //})
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */

    private fun onRetrieveQuestions(exercise: Exercise) {
        swapScreen(questionScreen, 0)
        questionScreen.startExercise(exercise)
        exerciseListScreen.refreshExerciseList()
    }

    private fun onExerciseTimeOut() {
        val dialog = OutOfTimeDialog(this::onExitExercise)
        dialog.show(supportFragmentManager, "out_of_time_dialog")
    }

    private fun onFinishExercise() {
        val dialog = FinishExerciseConfirmationDialog(this::onExitExercise)
        dialog.show(supportFragmentManager, "finish_confirmation_dialog")
    }

    private fun onExitExercise() {
        questionListScreen.setExercise(questionScreen.exercise)
        swapScreen(exerciseMenuScreen, 0)
        swapScreen(questionListScreen, 1)
        switchToScreen(1)
    }

    override fun onViewQuestion(exercise: Exercise, groupIndex: Int) {
        swapScreen(questionReadingScreen, 1)
        questionReadingScreen.readExercise(exercise, groupIndex)
    }

    private fun onViewExercise(exerciseFileName: String) {
        try {
            questionListScreen.setExercise(retrieveExerciseLocal(this, exerciseFileName))
            swapScreen(questionListScreen, 1)
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading the exercise from file.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun onReturnToQuestionList() {
        swapScreen(questionListScreen, 1)
    }

    private fun onReturnToExerciseList() {
        swapScreen(exerciseListScreen, 1)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exercise_nav_button) {
            switchToScreen(0)
        } else {
            switchToScreen(1)
        }
        return true
    }

    override fun onBackPressed() {
        if (currentScreenIndex == 1 && currentScreen === questionReadingScreen) {
            onReturnToQuestionList()
        } else if (currentScreenIndex == 0 && currentScreen === questionScreen) {
            onFinishExercise()
        } else if (currentScreenIndex == 1 && currentScreen === questionListScreen) {
            onReturnToExerciseList()
        } else if ((currentScreenIndex == 0 && currentScreen === exerciseMenuScreen) ||
            (currentScreenIndex == 1 && currentScreen === exerciseListScreen)) {
            // result overview screen or exercise menu screen
            onExit()
        }
    }

    private fun switchToScreen(screenIndex: Int) {
        if (currentScreenIndex != screenIndex) {
            updateActionBarBackButton()
            mainFrame.removeAllViews()
            currentScreen = screens[screenIndex]
            currentScreenIndex = screenIndex
            mainFrame.addView(currentScreen.view)
            navigationBar.selectedItemId = NAVIGATION_BAR_ITEM_IDS[screenIndex]
        }
    }

    private fun swapScreen(targetScreen: Screen, screenIndex: Int) {
        screens[screenIndex] = targetScreen
        if (currentScreenIndex == screenIndex) {
            updateActionBarBackButton()
            mainFrame.removeAllViews()
            currentScreen = targetScreen
            mainFrame.addView(currentScreen.view)
        }
    }

    private fun updateActionBarBackButton() {
        if (currentScreen !== exerciseMenuScreen || currentScreen !== exerciseListScreen) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

}