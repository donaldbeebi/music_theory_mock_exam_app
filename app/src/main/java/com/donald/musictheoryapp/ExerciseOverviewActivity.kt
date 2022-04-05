package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.donald.musictheoryapp.screen.ExerciseOverviewScreen
import com.donald.musictheoryapp.util.ExerciseData
import com.donald.musictheoryapp.util.displayToastForJsonError
import com.donald.musictheoryapp.util.retrieveExercise

class ExerciseOverviewActivity : AppCompatActivity() {

    private lateinit var frame: FrameLayout
    private lateinit var exerciseOverviewScreen: ExerciseOverviewScreen

    private val goToQuestion = fun(exerciseData: ExerciseData, sectionIndex: Int, localGroupIndex: Int) {
        val intent = Intent(this, QuestionReadingActivity::class.java).apply {
            putExtra("exercise_data", exerciseData)
            putExtra("section_index", sectionIndex)
            putExtra("local_group_index", localGroupIndex)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_overview)

        val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data")
            ?: throw IllegalStateException("Exercise data is not provided")

        val exercise = retrieveExercise(this, exerciseData)
        if (exercise == null) {
            displayToastForJsonError(this)
            finish()
        } else {
            findViewById<LinearLayout>(R.id.toolbar_back_button).setOnClickListener { finish() }
            findViewById<TextView>(R.id.toolbar_back_button_text).setText(R.string.exercise_list_activity_title)
            findViewById<TextView>(R.id.toolbar_title_text).setText(R.string.question_overview_activity_title)

            frame = findViewById(R.id.question_overview_frame)
            exerciseOverviewScreen = ExerciseOverviewScreen(this, goToQuestion).apply {
                displayExercise(exercise)
            }
            frame.addView(exerciseOverviewScreen.view)
        }
    }

    override fun onBackPressed() { finish() }

}