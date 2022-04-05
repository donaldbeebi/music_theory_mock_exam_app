package com.donald.musictheoryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.donald.musictheoryapp.screen.QuestionReadingScreen
import com.donald.musictheoryapp.util.ExerciseData
import com.donald.musictheoryapp.util.displayToastForJsonError
import com.donald.musictheoryapp.util.retrieveExercise

class QuestionReadingActivity : AppCompatActivity() {

    private lateinit var frame: FrameLayout
    private lateinit var questionReadingScreen: QuestionReadingScreen

    private val returnToExerciseOverview = fun() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_reading)

        val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data")
            ?: throw IllegalStateException("Exercise data is not provided")
        val sectionIndex = intent.getIntExtra("section_index", -1).also { require(it != -1) }
        val localGroupIndex = intent.getIntExtra("local_group_index", -1).also { require(it != -1) }

        findViewById<LinearLayout>(R.id.toolbar_back_button).setOnClickListener { finish() }
        findViewById<TextView>(R.id.toolbar_back_button_text).setText(R.string.question_overview_activity_title)
        findViewById<TextView>(R.id.toolbar_title_text).setText(R.string.question_reading_activity_title)

        val exercise = retrieveExercise(this, exerciseData)
        if (exercise == null) {
            displayToastForJsonError(this)
            finish()
        } else {
            frame = findViewById(R.id.question_reading_frame)
            questionReadingScreen = QuestionReadingScreen(this, returnToExerciseOverview)
            questionReadingScreen.readExercise(exercise, sectionIndex, localGroupIndex)
            frame.addView(questionReadingScreen.view)
        }
    }

    override fun onBackPressed() = finish()

}