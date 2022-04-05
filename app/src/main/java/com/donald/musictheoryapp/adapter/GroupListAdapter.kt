package com.donald.musictheoryapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.QuestionGroup
import com.donald.musictheoryapp.question.QuestionSection

class GroupListAdapter(
    private val viewGroup: (Int, Int) -> Unit,
) : RecyclerView.Adapter<GroupViewHolder>() {

    lateinit var section: QuestionSection

    private val onViewGroup = fun(localGroupIndex: Int) = viewGroup(section.number - 1, localGroupIndex)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        Log.d("GroupListAdapter", "onCreateViewHolder called")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_result_question_group, parent, false)
        return GroupViewHolder(itemView, onViewGroup)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        Log.d("GroupListAdapter", "onBindViewHolder called")
        holder.bindData(position, section.groups[position], position != section.groups.size - 1)
    }

    override fun getItemCount() = section.groups.size

}

class GroupViewHolder(
    itemView: View,
    viewGroup: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val groupName = itemView.findViewById<TextView>(R.id.result_group_name)
    private val groupScore = itemView.findViewById<TextView>(R.id.result_group_score)
    private val nextButton = itemView.findViewById<ImageView>(R.id.result_group_next_button)
    private val divider = itemView.findViewById<View>(R.id.result_group_divider)

    private var localGroupIndexToDisplay = -1

    init {
        nextButton.setOnClickListener { viewGroup(localGroupIndexToDisplay) }
    }

    fun bindData(groupIndex: Int, group: QuestionGroup, showsDivider: Boolean) {
        groupName.text = group.name
        groupScore.text = "${group.points}/${group.maxPoints}"
        localGroupIndexToDisplay = groupIndex
        divider.visibility = when (showsDivider) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

}