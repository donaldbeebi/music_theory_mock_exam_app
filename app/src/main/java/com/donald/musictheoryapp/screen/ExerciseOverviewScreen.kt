package com.donald.musictheoryapp.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.QuestionReadingActivity
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.adapter.ExerciseOverviewAdapter
import com.donald.musictheoryapp.pagedexercise.PagedExercise
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.ExerciseData

class ExerciseOverviewScreen(
    activity: Activity,
    private val viewGroup: (ExerciseData, Int, Int) -> Unit
) : Screen(activity, R.layout.screen_exercise_overview) {

    private val adapter = ExerciseOverviewAdapter()

    init {
        view.findViewById<RecyclerView>(R.id.exercise_overview_section_list).apply {
            adapter = this@ExerciseOverviewScreen.adapter
            layoutManager = LinearLayoutManager(activity)
            val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL).apply {
                AppCompatResources.getDrawable(activity, R.drawable.recycler_view_divider)?.let { setDrawable(it) }
            }
            //addItemDecoration(decoration)

            itemAnimator = null
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun displayExercise(exercise: Exercise) {
        val onViewGroup = fun(sectionIndex: Int, localGroupIndex: Int) {
            viewGroup(ExerciseData.fromExercise(exercise), sectionIndex, localGroupIndex)
        }
        adapter.apply {
            this.exercise = exercise
            this.viewGroup = onViewGroup
        }.notifyDataSetChanged()
    }

}