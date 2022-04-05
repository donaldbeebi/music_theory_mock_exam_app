package com.donald.musictheoryapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donald.musictheoryapp.adapter.PracticeOptionsAdapter
import com.donald.musictheoryapp.util.GroupOption
import com.donald.musictheoryapp.util.SectionOption
import com.donald.musictheoryapp.util.displayToastForZeroQuestionGroups
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader


class PracticeOptionsActivity : AppCompatActivity() {

    private lateinit var sectionOptions: List<SectionOption>
    private lateinit var adapter: PracticeOptionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_options)

        findViewById<LinearLayout>(R.id.toolbar_back_button).setOnClickListener { finish() }
        findViewById<TextView>(R.id.toolbar_back_button_text).setText(R.string.main_activity_title)
        findViewById<TextView>(R.id.toolbar_title_text).setText(R.string.practice_options_activity_title)

        sectionOptions = loadMenu()
        adapter = PracticeOptionsAdapter(sectionOptions)

        val activity = this
        findViewById<RecyclerView>(R.id.practice_options_recycler_view).apply {
            adapter = activity.adapter
            layoutManager = LinearLayoutManager(activity)
            val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL).apply {
               AppCompatResources.getDrawable(activity, R.drawable.recycler_view_divider)?.let { setDrawable(it) }
            }
            addItemDecoration(decoration)
        }
        findViewById<TextView>(R.id.practice_options_confirm_button).setOnClickListener {
            if (sectionOptions.all { sectionOption -> sectionOption.all { groupOption -> groupOption.count == 0 } }) {
                displayToastForZeroQuestionGroups(this)
            } else {
                confirmPractice()
            }
        }
    }

    private fun loadMenuN(): List<GroupOption> {
        val menuJson = InputStreamReader(assets.open("question_menu.json")).use {
            JSONObject(it.readText())
        }
        //val sections = ArrayList<List<QuestionMenuItem>>()
        val groups = ArrayList<GroupOption>()

        val sectionJsonArray = menuJson.getJSONArray("sections")
        for (sectionIndex in 0 until sectionJsonArray.length()) {
            val groupJsonArray = sectionJsonArray.getJSONObject(sectionIndex).getJSONArray("groups")

            for (groupIndex in 0 until groupJsonArray.length()) {
                val identifier = groupJsonArray.getJSONObject(groupIndex).getString("identifier")
                val resId = resources.getIdentifier("group_$identifier", "string", packageName)
                groups += GroupOption(identifier = identifier, groupName = getString(resId))
            }
        }
        return groups
    }

    private fun loadMenu(): List<SectionOption> {
        val menuJson = InputStreamReader(assets.open("question_menu.json")).use {
            JSONObject(it.readText())
        }

        val sectionJsonArray = menuJson.getJSONArray("sections")
        val sectionOptions = ArrayList<SectionOption>(sectionJsonArray.length())

        for (sectionIndex in 0 until sectionJsonArray.length()) {
            val sectionJson = sectionJsonArray.getJSONObject(sectionIndex)
            val groupJsonArray = sectionJson.getJSONArray("groups")
            val groupOptions = ArrayList<GroupOption>(groupJsonArray.length())
            // populating the groupOptions
            for (groupIndex in 0 until groupJsonArray.length()) {
                val groupJson = groupJsonArray.getJSONObject(groupIndex)
                val identifier = groupJson.getString("identifier")
                val groupName = getString(resources.getIdentifier("group_$identifier", "string", packageName))
                groupOptions += GroupOption(identifier = identifier, groupName = groupName)
            }
            val identifier = sectionJson.getString("identifier")
            val sectionName = getString(resources.getIdentifier("section_$identifier", "string", packageName))
            sectionOptions += SectionOption(identifier = identifier, sectionName = sectionName, groupOptions)
        }

        return sectionOptions
    }

    private fun confirmPractice() {
        val options = JSONObject()
        val sectionJsonArray = JSONArray()

        sectionOptions.forEach { sectionOption ->
            // see if this section is not needed
            if (sectionOption.all { it.count == 0 }) { return@forEach }

            val sectionJson = JSONObject()
            val groupJsonArray = JSONArray()
            sectionOption.forEach { groupOption ->
                if (groupOption.count > 0) {
                    val option = JSONObject().apply {
                        put("identifier", groupOption.identifier)
                        put("count", groupOption.count)
                    }
                    groupJsonArray.put(option)
                }
            }
            sectionJson.put("identifier", sectionOption.identifier)
            sectionJson.put("groups", groupJsonArray)
            sectionJsonArray.put(sectionJson)
        }
        options.put("sections", sectionJsonArray)

        val intent = Intent(this, ExerciseActivity::class.java).apply {
            putExtra("action", "download")
            putExtra("mode", "practice")
            putExtra("options_json", options.toString())
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() = finish()

}



