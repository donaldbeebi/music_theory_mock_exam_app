package com.donald.musictheoryapp.screen

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.ExerciseListItem
import com.donald.musictheoryapp.MainActivity
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.getExerciseList
import java.text.SimpleDateFormat
import java.util.*

class ExerciseListScreen(
    activity: MainActivity,
    onViewExercise: (String) -> Unit
    ) : Screen(activity, R.layout.screen_exercise_list) {

    private val adapter: ExerciseListAdapter = ExerciseListAdapter(context, onViewExercise)
    private val recyclerView: RecyclerView = view.findViewById(R.id.exercise_list_recycler_view)

    init {
        recyclerView.apply {
            adapter = this@ExerciseListScreen.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun refreshExerciseList() {
        adapter.refreshExerciseList()
    }

}

private class ExerciseListAdapter(
    private val context: Context,
    val onViewExercise: (String) -> Unit
) : RecyclerView.Adapter<ExerciseListViewHolder>() {

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var exerciseList: Array<ExerciseListItem> = getExerciseList(context)

    @SuppressLint("NotifyDataSetChanged")
    fun refreshExerciseList() {
        exerciseList = getExerciseList(context)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_exercise_list, parent, false)
        return ExerciseListViewHolder(itemView, onViewExercise, dateFormatter)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        holder.bindData(exerciseList[position])
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

}

private class ExerciseListViewHolder(

    itemView: View,
    private val onViewExercise: (String) -> Unit,
    private val dateFormatter: SimpleDateFormat
) : RecyclerView.ViewHolder(itemView) {

    private val titleTextView: TextView = itemView.findViewById(R.id.exercise_list_item_title)
    private val scoreTextView: TextView = itemView.findViewById(R.id.exercise_list_item_score)
    private val dateTextView: TextView = itemView.findViewById(R.id.exercise_list_item_date)
    private lateinit var exerciseTitle: String

    init {
        itemView.setOnClickListener { onViewExercise(exerciseTitle) }
    }

    fun bindData(item: ExerciseListItem) {
        titleTextView.text = item.title
        scoreTextView.text = "${item.points}/${item.maxPoints}"
        dateTextView.text = dateFormatter.format(item.date)
        exerciseTitle = "${item.title} ${item.date.time}"
    }

}