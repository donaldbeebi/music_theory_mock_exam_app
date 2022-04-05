package com.donald.musictheoryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.question.QuestionSection
import com.donald.musictheoryapp.util.toggle

class ExerciseOverviewAdapter : RecyclerView.Adapter<SectionViewHolder>() {

    var exercise: Exercise? = null
        set(exercise) {
            if (exercise == null) return
            sectionDataList.clear()
            for (i in 0 until exercise.sectionCount()) {
                sectionDataList += SectionData(i, exercise.sectionAt(i), false)
            }
            field = exercise
        }

    lateinit var viewGroup: (Int, Int) -> Unit

    private val sectionDataList = ArrayList<SectionData>()
    private var currentlyExpanded = false
    private var indexOfCurrentlyExpanded = -1

    private val onExpanded = fun(position: Int) {
        when (currentlyExpanded) {
            true -> {
                assert(indexOfCurrentlyExpanded != position)
                // collapse the currently expanded
                sectionDataList[indexOfCurrentlyExpanded].expanded = false
                notifyItemChanged(indexOfCurrentlyExpanded)
                // expand as requested
                sectionDataList[position].expanded = true
                notifyItemChanged(position)
                indexOfCurrentlyExpanded = position
            }
            false -> {
                // expand as requested without needing to collapse any item
                currentlyExpanded = true
                sectionDataList[position].expanded = true
                notifyItemChanged(position)
                indexOfCurrentlyExpanded = position
            }
        }
    }

    private val onCollapsed = fun() {
        when (currentlyExpanded) {
            true -> {
                currentlyExpanded = false
                sectionDataList[indexOfCurrentlyExpanded].expanded = false
                notifyItemChanged(indexOfCurrentlyExpanded)
            }
            false -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_result_question_section, parent, false)
        return SectionViewHolder(itemView, parent.context, onExpanded, onCollapsed, viewGroup)
    }

    override fun onBindViewHolder(holderSection: SectionViewHolder, position: Int) {
        //holder.setQuestions(exercise) // TODO: CALL THIS WHEN SETTING QUESTIONS IN ADAPTER, NOT HERE
        holderSection.bindData(sectionDataList[position])
    }

    override fun getItemCount(): Int {
        return exercise?.sectionCount() ?: 0
    }

    data class SectionData(val sectionIndex: Int, val section: QuestionSection, var expanded: Boolean)

}

class SectionViewHolder(
    itemView: View,
    private val context: Context,
    private val notifyExpanded: (Int) -> Unit,
    private val notifyCollapsed: () -> Unit,
    viewGroup: (Int, Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private lateinit var sectionData: ExerciseOverviewAdapter.SectionData
    private val sectionNumber: TextView = itemView.findViewById(R.id.result_section_number)
    private val sectionName: TextView = itemView.findViewById(R.id.result_section_name)
    private val sectionScore: TextView = itemView.findViewById(R.id.result_section_score)
    private val expandButton: ImageView = itemView.findViewById(R.id.result_section_expand_button)
    private val bottomShadow = itemView.findViewById<View>(R.id.result_section_bottom_shadow)
    private val groupListAdapter: GroupListAdapter = GroupListAdapter(viewGroup)
    private val groupList = itemView.findViewById<RecyclerView>(R.id.result_section_group_list).apply {
        adapter = groupListAdapter
        layoutManager = LinearLayoutManager(context)
    }

    init {
        expandButton.setOnClickListener {
            val expanded = toggle(sectionData.expanded)
            updateExpansion(expanded)
            when (expanded) {
                true -> notifyExpanded(adapterPosition)
                false -> notifyCollapsed()
            }
            sectionData.expanded = expanded
        }
    }

    private fun updateExpansion(expanded: Boolean) {
        when (expanded) {
            true -> updateExpand()
            false -> updateCollapse()
        }
    }

    private fun updateCollapse() {
        expandButton.setImageResource(R.drawable.ic_expand_button_collapsed)
        groupList.visibility = View.GONE
    }

    private fun updateExpand() {
        expandButton.setImageResource(R.drawable.ic_expand_button_expanded)
        groupList.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun bindData(sectionData: ExerciseOverviewAdapter.SectionData) {
        this.sectionData = sectionData
        val (sectionIndex, section, expanded) = sectionData
        sectionNumber.text = context.getString(R.string.exercise_overview_section_name, sectionIndex + 1)
        sectionName.text = section.name
        sectionScore.text = "${section.points}/${section.maxPoints}"
        groupListAdapter.section = section
        groupListAdapter.notifyDataSetChanged()
        updateExpansion(expanded)
        bottomShadow.visibility = when (expanded) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

}