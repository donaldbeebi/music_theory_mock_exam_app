package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.donald.musictheoryapp.screen.ExerciseListScreen
import com.donald.musictheoryapp.util.ExerciseData
import com.donald.musictheoryapp.util.displayToast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExerciseListActivity : AppCompatActivity() {

    private lateinit var frame: FrameLayout
    private lateinit var exerciseListScreen: ExerciseListScreen

    private val onViewExercise = fun(exerciseData: ExerciseData) {
        val intent = Intent(this, ExerciseOverviewActivity::class.java)
        intent.putExtra("exercise_data", exerciseData)
        exerciseListScreen.editable = false
        startActivity(intent)
    }

    private val onStartExercise = fun(exerciseData: ExerciseData) {
        val intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("action", "redo")
        intent.putExtra("exercise_data", exerciseData)
        exerciseListScreen.editable = false
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)

        findViewById<LinearLayout>(R.id.toolbar_back_button).setOnClickListener { finish() }
        findViewById<TextView>(R.id.toolbar_back_button_text).setText(R.string.main_activity_title)
        findViewById<TextView>(R.id.toolbar_title_text).setText(R.string.exercise_list_activity_title)
        findViewById<FloatingActionButton>(R.id.exercise_list_edit_button).apply {
            setOnClickListener {
                exerciseListScreen.toggleEditable()
                when (exerciseListScreen.editable) {
                    true -> this.setImageResource(R.drawable.ic_fab_confirm)
                    false -> this.setImageResource(R.drawable.ic_fab_edit)
                }
            }
        }
        frame = findViewById(R.id.exercise_list_frame)
        exerciseListScreen = ExerciseListScreen(this, onViewExercise, onStartExercise).apply {
            refreshExerciseList()
        }
        frame.addView(exerciseListScreen.view)
    }

    override fun onBackPressed() = finish()

}