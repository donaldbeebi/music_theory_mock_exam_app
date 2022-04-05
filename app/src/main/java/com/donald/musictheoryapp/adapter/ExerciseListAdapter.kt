package com.donald.musictheoryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.util.*
import java.text.SimpleDateFormat
import java.util.*

class ExerciseListAdapter(
    private val context: Context,
    private val onViewExercise: (ExerciseData) -> Unit,
    private val onStartExercise: (ExerciseData) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    var editable: Boolean = false
        set(editable) {
            field = editable
            viewHolders.forEach { viewHolder ->
                viewHolder.editable = editable
            }
        }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var exerciseList: List<ExerciseData>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private val viewHolders = ArrayList<ViewHolder>()

    private val onRemoveItem: (Int) -> Unit = { position ->
        exerciseList = getExerciseList(context)
        notifyItemRemoved(position)
    }

    fun toggleEditable() {
        editable = toggle(editable)
        viewHolders.forEach { viewHolder ->
            viewHolder.editable = editable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_exercise_list, parent, false)
        return ViewHolder(onRemoveItem, itemView, onViewExercise, onStartExercise, dateFormatter)
            .also { viewHolders += it }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        exerciseList?.get(position)?.let { holder.bindData(it) }
    }

    override fun getItemCount(): Int {
        return exerciseList?.size ?: 0
    }

    class ViewHolder(
        removeItem: (Int) -> Unit,
        itemView: View,
        private val viewExercise: (ExerciseData) -> Unit,
        private val resumeExercise: (ExerciseData) -> Unit,
        private val dateFormatter: SimpleDateFormat
    ) : RecyclerView.ViewHolder(itemView) {

        //private val titleTextView: TextView = itemView.findViewById(R.id.exercise_list_item_title)
        private val typeTextView: TextView = itemView.findViewById(R.id.exercise_list_item_type)
        private val scoreTextView: TextView = itemView.findViewById(R.id.exercise_list_item_score)
        private val dateTextView: TextView = itemView.findViewById(R.id.exercise_list_item_date)
        private val deleteButton: ImageView = itemView.findViewById(R.id.exercise_list_item_delete_button)
        private val pausedTextView: TextView = itemView.findViewById(R.id.exercise_list_item_paused_text)
        private val nextButton: ImageView = itemView.findViewById(R.id.exercise_list_item_next_button)
        private lateinit var exerciseData: ExerciseData
        private val context = itemView.context

        var editable: Boolean
            get() = when (deleteButton.visibility) {
                View.VISIBLE -> true
                View.GONE -> false
                else -> throw IllegalStateException()
            }
            set(editable) {
                deleteButton.visibility = when (editable) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
            }

        init {
            pausedTextView.setOnClickListener {
                Toast.makeText(context, R.string.toast_exercise_list_paused_info, Toast.LENGTH_LONG).show()
            }
            nextButton.setOnClickListener {
                when (exerciseData.ended) {
                    true -> viewExercise(exerciseData)
                    false -> resumeExercise(exerciseData)
                }
            }
            deleteButton.apply {
                setOnClickListener {
                    runBackground {
                        val deleted = deleteExercise(context, exerciseData)
                        when (deleted) {
                            true -> runMain {
                                displayToastForSuccessfulExerciseDeletion(context)
                                removeItem(adapterPosition)
                            }
                            false -> runMain {
                                displayToastForFailedExerciseDeletion(context)
                            }
                        }
                    }
                }
                visibility = View.GONE
            }
        }

        fun bindData(data: ExerciseData) {
            when (data.ended) {
                true -> {
                    pausedTextView.visibility = View.GONE
                    nextButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.exercise_list_next_button_view))
                    scoreTextView.apply {
                        visibility = View.VISIBLE
                        text = "${data.points}/${data.maxPoints}"
                    }
                }
                false -> {
                    pausedTextView.visibility = View.VISIBLE
                    nextButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.exercise_list_next_button_resume))
                    scoreTextView.visibility = View.GONE
                }
            }
            dateTextView.text = dateFormatter.format(data.date)
            typeTextView.text = context.getString(data.type.resId)
            exerciseData = data
        }

    }

    enum class SortType { DATE, TYPE, ENDED, POINTS }

}