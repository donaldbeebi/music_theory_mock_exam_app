package com.donald.musictheoryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.screen.ExerciseDownloadScreen
import com.donald.musictheoryapp.screen.ExerciseScreen
import com.donald.musictheoryapp.screen.Screen
import com.donald.musictheoryapp.util.*
import com.donald.musictheoryapp.util.Time.Companion.sec
import org.json.JSONObject

class ExerciseActivity : AppCompatActivity() {

    private lateinit var frame: FrameLayout
    private lateinit var currentScreen: Screen
    private lateinit var downloadScreen: ExerciseDownloadScreen
    private lateinit var exerciseScreen: ExerciseScreen
    //private var exercise: Exercise? = null

    /*
    private val showExercise = fun(exercise: Exercise) {
        frame.removeAllViews()
        frame.addView(exerciseScreen.view)
        exerciseScreen.startExercise(exercise)
    }
     */

    private val onPauseExercise = fun(exercise: Exercise, currentQuestionIndex: Int) {
        PauseExerciseDialog(
            callback = {
                exercise.savedPageIndex = currentQuestionIndex
                saveExercise(this, exercise)
                finish()
            }
        ).show(supportFragmentManager, null)
    }

    private val onExitExercise = fun(exercise: Exercise, currentPage: Int) {
        EndExerciseDialog(
            saveExercise = {
                val originalQuestionIndexForResume = exercise.savedPageIndex
                exercise.savedPageIndex = currentPage
                runBackground {
                    val successful = saveExercise(this, exercise)
                    if (successful) runMain{
                        finish()
                    } else runMain {
                        displayToast(R.string.toast_failed_exercise_save, this)
                        exercise.savedPageIndex = originalQuestionIndexForResume
                    }
                }
            },
            endExercise = {
                val originalTimeRemaining = exercise.timeRemaining
                exercise.timeRemaining = 0.sec
                runBackground {
                    val successful = saveExercise(this, exercise)
                    if (successful) runMain {
                        finish()
                    } else runMain {
                        displayToast(R.string.toast_failed_exercise_save, this)
                        exercise.timeRemaining = originalTimeRemaining
                    }
                }

            }
        ).show(supportFragmentManager, null)
    }

    private val showTimeOutDialog = fun(exercise: Exercise) {
        saveExercise(this, exercise)
        TimeOutDialog(callback = { finish() }).show(supportFragmentManager, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        findViewById<LinearLayout>(R.id.toolbar_back_button).setOnClickListener { onBackPressed() }
        findViewById<TextView>(R.id.toolbar_back_button_text).setText(R.string.main_activity_title)
        findViewById<TextView>(R.id.toolbar_title_text).setText(R.string.exercise_activity_title)

        frame = findViewById(R.id.exercise_frame)
        downloadScreen = ExerciseDownloadScreen(this)
        exerciseScreen = ExerciseScreen(this, onExitExercise, showTimeOutDialog)
        currentScreen = downloadScreen
        frame.addView(downloadScreen.view)

        val action = intent.getStringExtra("action") ?: throw IllegalStateException("No action provided")
        when (action) {

            "redo" -> {
                val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data") ?: throw IllegalStateException("No exercise data provided")
                if (exerciseData.timeRemaining == 0.sec) {
                    TODO()
                } else {
                    runBackground {
                        val exercise = retrieveExercise(this, exerciseData)
                        if (exercise == null) {
                            runMain {
                                displayToastForFailedExerciseRetrieval(this)
                                Log.d("ExerciseActivity", "Error while retrieving for exercise $exerciseData")
                            }
                            return@runBackground
                        }
                        runMain {
                            frame.removeAllViews()
                            frame.addView(exerciseScreen.view)
                            exerciseScreen.startExercise(exercise)
                        }
                    }
                }
            }

            "download" -> {
                val mode = intent.getStringExtra("mode") ?: throw IllegalStateException("No mode provided")
                when (mode) {
                    "practice" -> {
                        val optionsJson = JSONObject(intent.getStringExtra("options_json") ?: throw IllegalStateException())
                        downloadScreen.downloadPractice(optionsJson).then { exercise ->
                            if (exercise == null) {
                                displayToastForJsonError(this)
                                Log.d("ExerciseActivity", "Exercise is null when downloading practice")
                            } else {
                                frame.removeAllViews()
                                frame.addView(exerciseScreen.view)
                                exerciseScreen.startExercise(exercise)
                            }
                        }
                    }
                    "test" -> {
                        downloadScreen.downloadTest().then { exercise ->
                            if (exercise == null) {
                                displayToastForJsonError(this)
                                Log.d("ExerciseActivity", "Exercise is null when downloading test")
                            } else {
                                frame.removeAllViews()
                                frame.addView(exerciseScreen.view)
                                exerciseScreen.startExercise(exercise)
                            }
                        }
                    }
                    else -> {
                        throw IllegalStateException("Mode $mode not recognized")
                    }
                }
            }

        }
    }

    override fun onBackPressed() = exerciseScreen.promptExitExercise()

}