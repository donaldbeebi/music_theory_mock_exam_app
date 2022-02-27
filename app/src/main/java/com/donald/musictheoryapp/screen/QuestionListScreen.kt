package com.donald.musictheoryapp.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.donald.musictheoryapp.question.Exercise
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.donald.musictheoryapp.MainActivity

class QuestionListScreen(
    activity: MainActivity,
    private val onViewExerciseListener: OnViewExerciseListener
    ) : Screen(activity, R.layout.screen_result_overview) {

    interface OnViewExerciseListener {
        fun onViewQuestion(exercise: Exercise, targetGroup: Int)
    }

    fun setExercise(exercise: Exercise) {
        // load questions
        val recyclerView: RecyclerView = view.findViewById(R.id.result_recycler_view)
        val adapter = QuestionListAdapter(exercise, onViewExerciseListener) // TODO: OPTIMIZE OBJECT CREATION
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

}

class QuestionListAdapter(
    private val exercise: Exercise,
    private val onViewExerciseListener: QuestionListScreen.OnViewExerciseListener
) : RecyclerView.Adapter<QuestionListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_result_question_section, parent, false)
        return QuestionListViewHolder(itemView, inflater, exercise, onViewExerciseListener)
    }

    override fun onBindViewHolder(holder: QuestionListViewHolder, position: Int) {
        //holder.setQuestions(exercise) // TODO: CALL THIS WHEN SETTING QUESTIONS IN ADAPTER, NOT HERE
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return exercise.sectionCount()
    }
}

class QuestionListViewHolder(
    itemView: View,
    private val inflater: LayoutInflater,
    private val exercise: Exercise,
    private val onViewExerciseListener: QuestionListScreen.OnViewExerciseListener
) : RecyclerView.ViewHolder(itemView) {

    private val sectionNumber: TextView = itemView.findViewById(R.id.result_section_number)
    private val sectionName: TextView = itemView.findViewById(R.id.result_section_name)
    private val sectionScore: TextView = itemView.findViewById(R.id.result_section_score)
    private val sectionGroup: View = itemView.findViewById(R.id.result_section_group)
    private val groupsLinearLayout: LinearLayout = itemView.findViewById(R.id.result_section_groups)
    private var groupIndexToDisplay = 0

    init {
        groupsLinearLayout.visibility = View.GONE
        sectionGroup.setOnClickListener {
            groupsLinearLayout.visibility = if (groupsLinearLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    fun bindData(sectionIndex: Int) {
        val section = exercise.sectionAt(sectionIndex)
        sectionNumber.text = section.number.toString()
        sectionName.text = section.name
        sectionScore.text = "${section.points}/${section.maxPoints}"
        groupIndexToDisplay = sectionIndex
        groupsLinearLayout.removeAllViews()
        val groups = section.groups
        var i = 0
        val groupsLength = groups.size
        while (i < groupsLength) {
            val group = groups[i]
            val groupItem = inflater.inflate(
                R.layout.item_result_question_group, groupsLinearLayout, false
            )
            (groupItem.findViewById<View>(R.id.result_group_name) as TextView).text = group.name
            (groupItem.findViewById<View>(R.id.result_group_score) as TextView).text = "${group.points}/${group.maxPoints}"
            groupItem.setOnClickListener {
                onViewExerciseListener.onViewQuestion(
                    exercise, exercise.groupIndexOf(group)
                )
            }
            groupsLinearLayout.addView(groupItem)
            i++
        }
    }

}