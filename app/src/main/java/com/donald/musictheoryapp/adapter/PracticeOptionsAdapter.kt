package com.donald.musictheoryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.util.GroupOption
import com.donald.musictheoryapp.util.SectionOption

class PracticeOptionsAdapter(
    sectionOptions: List<SectionOption>
) : RecyclerView.Adapter<PracticeOptionsAdapter.ViewHolder>() {

    private val groupOptions = sectionOptions.flatten()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_practice_options, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(groupOptions[position])
    }

    override fun getItemCount() = groupOptions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var menuItem: GroupOption
        private val groupNameTextView = itemView.findViewById<TextView>(R.id.practice_option_group_name)
        private val countTextView = itemView.findViewById<TextView>(R.id.practice_option_group_count)

        init {
            itemView.findViewById<ImageView>(R.id.practice_option_plus_button).setOnClickListener {
                val menuItem = menuItem
                val newCount = menuItem.count + 1
                menuItem.count = newCount
                countTextView.text = newCount.toString()
            }

            itemView.findViewById<ImageView>(R.id.practice_option_minus_button).setOnClickListener {
                val menuItem = menuItem
                val count = menuItem.count
                if (count > 0) {
                    val newCount = count - 1
                    menuItem.count = newCount
                    countTextView.text = newCount.toString()
                }
            }
        }

        fun bindData(menuItem: GroupOption) {
            this.menuItem = menuItem
            groupNameTextView.text = menuItem.groupName
            countTextView.text = menuItem.count.toString()
        }

    }

}