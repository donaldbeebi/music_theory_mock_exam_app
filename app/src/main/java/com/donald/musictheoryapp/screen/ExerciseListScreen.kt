package com.donald.musictheoryapp.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.adapter.ExerciseListAdapter
import com.donald.musictheoryapp.util.*

class ExerciseListScreen(
    activity: Activity,
    onViewExercise: (ExerciseData) -> Unit,
    onStartExercise: (ExerciseData) -> Unit
) : Screen(activity, R.layout.screen_exercise_list) {

    var editable: Boolean
        get() = adapter.editable
        set(editable) {
            adapter.editable = editable
        }

    private var currentSortType = ExerciseListAdapter.SortType.DATE
    private var currentSortByAscending = true
    private val adapter: ExerciseListAdapter = ExerciseListAdapter(activity, onViewExercise, onStartExercise)
    private val recyclerView: RecyclerView = view.findViewById(R.id.exercise_list_recycler_view)

    init {
        recyclerView.apply {
            adapter = this@ExerciseListScreen.adapter
            layoutManager = LinearLayoutManager(activity)
            val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL).apply {
                AppCompatResources.getDrawable(activity, R.drawable.recycler_view_divider)?.let { setDrawable(it) }
            }
            addItemDecoration(decoration)
        }
        runBackground {
            val exerciseList = sortedExerciseList(getExerciseList(activity))
            runMain { adapter.exerciseList = exerciseList }
        }

        val sortOrderImageView = view.findViewById<ImageView>(R.id.exercise_list_sort_order)
        sortOrderImageView.setOnClickListener {
            val ascending = toggle(currentSortByAscending)
            sortOrderImageView.setImageResource(
                when (ascending) {
                    true -> R.drawable.ic_exercise_list_sort_ascending
                    false -> R.drawable.ic_exercise_list_sort_descending
                }
            )
            setSortOrder(ascending)
        }

        val sortByTextView = view.findViewById<TextView>(R.id.exercise_list_sort_by)
        sortByTextView.setOnClickListener { view ->
            PopupMenu(activity, view).apply {
                menuInflater.inflate(R.menu.exercise_list_sort_menu, menu)
                setOnMenuItemClickListener { item ->
                    sortByTextView.text = item.title
                    when (item.itemId) {
                        R.id.exercise_list_menu_date -> {
                            setSortType(ExerciseListAdapter.SortType.DATE)
                            true
                        }
                        R.id.exercise_list_menu_type -> {
                            setSortType(ExerciseListAdapter.SortType.TYPE)
                            true
                        }
                        R.id.exercise_list_menu_ended -> {
                            setSortType(ExerciseListAdapter.SortType.ENDED)
                            true
                        }
                        R.id.exercise_list_menu_points -> {
                            setSortType(ExerciseListAdapter.SortType.POINTS)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    fun toggleEditable() = adapter.toggleEditable()

    fun refreshExerciseList() = runBackground {
        val exerciseList = getExerciseList(activity)
        runMain { adapter.exerciseList = exerciseList }
    }

    private fun setSortType(sortType: ExerciseListAdapter.SortType) {
        currentSortType = sortType
        val exerciseList = adapter.exerciseList ?: return
        runBackground {
            val sortedExerciseList = sortedExerciseList(exerciseList)
            runMain { adapter.exerciseList = sortedExerciseList }
        }
    }

    private fun setSortOrder(ascending: Boolean) {
        currentSortByAscending = ascending
        val exerciseList = adapter.exerciseList ?: return
        runBackground {
            val sortedExerciseList = sortedExerciseList(exerciseList)
            runMain { adapter.exerciseList = sortedExerciseList }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortedExerciseList(exerciseList: List<ExerciseData>): List<ExerciseData> {
        return when (currentSortByAscending) {
            true -> when (currentSortType) {
                ExerciseListAdapter.SortType.DATE -> exerciseList.sortedBy { it.date }
                ExerciseListAdapter.SortType.TYPE -> exerciseList.sortedBy { it.type }
                ExerciseListAdapter.SortType.ENDED -> exerciseList.sortedBy { it.ended }
                ExerciseListAdapter.SortType.POINTS -> exerciseList.sortedBy { it.points }
            }
            false -> when (currentSortType) {
                ExerciseListAdapter.SortType.DATE -> exerciseList.sortedByDescending { it.date }
                ExerciseListAdapter.SortType.TYPE -> exerciseList.sortedByDescending { it.type }
                ExerciseListAdapter.SortType.ENDED -> exerciseList.sortedByDescending { it.ended }
                ExerciseListAdapter.SortType.POINTS -> exerciseList.sortedByDescending { it.points }
            }
        }
    }

}