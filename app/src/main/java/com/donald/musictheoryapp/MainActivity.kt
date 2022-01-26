package com.donald.musictheoryapp

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.donald.musictheoryapp.Screen.ExerciseMenuScreen.OnStartExerciseListener
import com.donald.musictheoryapp.Screen.QuestionScreen.OnFinishExerciseListener
import com.donald.musictheoryapp.Screen.QuestionScreen.OnReturnToOverviewListener
import com.donald.musictheoryapp.Screen.FinishExerciseConfirmationDialog.OnConfirmDialogListener
import com.donald.musictheoryapp.Screen.ResultOverviewScreen.OnProceedToDetailListener
import com.donald.musictheoryapp.Screen.ExerciseMenuScreen
import com.donald.musictheoryapp.Screen.QuestionScreen
import com.donald.musictheoryapp.Screen.ResultOverviewScreen
import com.donald.musictheoryapp.Screen.Screen
import android.widget.TextView
import android.view.ViewGroup
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.donald.musictheoryapp.Utils.NumberTracker
import org.json.JSONException
import org.xmlpull.v1.XmlPullParserException
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import android.graphics.Bitmap
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.android.volley.Request
import com.donald.musictheoryapp.QuestionArray.QuestionArray
import com.donald.musictheoryapp.Screen.FinishExerciseConfirmationDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        OnStartExerciseListener,
        OnFinishExerciseListener,
        OnReturnToOverviewListener,
        OnConfirmDialogListener,
        OnProceedToDetailListener {

    private lateinit var exerciseMenuScreen: ExerciseMenuScreen
    private lateinit var questionScreen: QuestionScreen
    private lateinit var resultOverviewScreen: ResultOverviewScreen
    private lateinit var currentScreenForExerciseTab: Screen
    private lateinit var exerciseMenuStatusTextView: TextView
    private lateinit var mainFrame: ViewGroup

    //private ExerciseGenerator m_ExerciseGenerator;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (findViewById<View>(R.id.bottom_navigation_view) as BottomNavigationView).setOnNavigationItemSelectedListener(this)
        mainFrame = findViewById(R.id.main_frame_layout)
        exerciseMenuScreen = ExerciseMenuScreen(this, layoutInflater.inflate(R.layout.screen_exercise_menu, null), this)
        exerciseMenuStatusTextView = exerciseMenuScreen.view.findViewById(R.id.exercise_menu_status_text_view)
        questionScreen = QuestionScreen(this, layoutInflater.inflate(R.layout.screen_question, null), this, this)
        resultOverviewScreen = ResultOverviewScreen(this, layoutInflater.inflate(R.layout.screen_result_overview, null), this)
        currentScreenForExerciseTab = exerciseMenuScreen
        mainFrame.addView(currentScreenForExerciseTab.view)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentScreenForExerciseTab === questionScreen) questionScreen.onBackPressed()
            }
        })
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exercise_nav_button) {
            mainFrame.removeAllViews()
            mainFrame.addView(currentScreenForExerciseTab.view)
        } else {
            mainFrame.removeAllViews()
            mainFrame.addView(resultOverviewScreen.view)
        }
        return true
    }

    override fun onStartExercise() {
        exerciseMenuStatusTextView.setText(R.string.contacting_server_status)
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
                Request.Method.GET,
                URL + "exercise",
                { response ->
                    try {
                        val `object` = JSONObject(response)
                        val images = `object`.getJSONArray("images")
                        val numberTracker = NumberTracker(
                                images.length(),
                                { tracker: NumberTracker ->
                                    exerciseMenuStatusTextView.text = getString(
                                            R.string.image_download_status,
                                            tracker.count(),
                                            tracker.target()
                                    )
                                }
                        ) { tracker: NumberTracker? ->
                            try {
                                onRetrieveQuestions(`object`)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                exerciseMenuStatusTextView.setText(R.string.json_error_status)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                exerciseMenuStatusTextView.setText(R.string.xml_error_status)
                            } catch (e: XmlPullParserException) {
                                e.printStackTrace()
                                exerciseMenuStatusTextView.setText(R.string.xml_error_status)
                            }
                        }
                        for (i in 0 until images.length()) {
                            downloadImage(images.getString(i), numberTracker)
                        }
                    } catch (e: JSONException) {
                        exerciseMenuStatusTextView.setText(R.string.volley_error_status)
                        e.printStackTrace()
                    }
                }
        ) { error ->
            Log.d("from string request", "error: " + error.message)
            exerciseMenuStatusTextView.setText(R.string.server_error_status)
        }
        queue.add(request)
    }

    fun downloadImage(title: String, tracker: NumberTracker) {
        val dir = File(filesDir, "images")
        if (!dir.exists()) dir.mkdir()
        val destination = File(dir, "$title.png")
        val request = ImageRequest(
                URL + "images/" + title,
                { response: Bitmap ->
                    try {
                        destination.createNewFile()
                        val bos = ByteArrayOutputStream()
                        response.compress(Bitmap.CompressFormat.PNG, 100, bos)
                        val fos = FileOutputStream(destination)
                        fos.write(bos.toByteArray())
                        fos.flush()
                        fos.close()
                        tracker.increment()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                },
                2000,
                2000,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.RGB_565
        ) { error: VolleyError -> Log.d("Volley error while fetch image $title", error.toString()) }
        Volley.newRequestQueue(this).add(request)
    }

    fun onImagesReady() {}

    @kotlin.Throws(JSONException::class, IOException::class, XmlPullParserException::class)
    fun onRetrieveQuestions(jsonObject: JSONObject?) {
        mainFrame.removeAllViews()
        val questions = QuestionArray.fromJSON(jsonObject)
        questionScreen.questions = questions
        //m_QuestionScreen.setQuestions(m_ExerciseGenerator.generateExercise());
        questionScreen.startExercise()
        questionScreen.startTimer()
        currentScreenForExerciseTab = questionScreen
        mainFrame.addView(currentScreenForExerciseTab.view)
        exerciseMenuStatusTextView.setText(R.string.exercise_menu_start_button_default_text)
    }

    override fun onFinishExercise() {
        val dialog = FinishExerciseConfirmationDialog(this)
        dialog.show(supportFragmentManager, "exercise_finish_confirmation_dialog")
    }

    override fun onConfirmDialog() {
        mainFrame.removeAllViews()
        resultOverviewScreen.setQuestions(questionScreen.questions)
        currentScreenForExerciseTab = resultOverviewScreen
        mainFrame.addView(currentScreenForExerciseTab.view)
    }

    override fun onProceedToDetail(questions: QuestionArray, targetGroup: Int) {
        mainFrame.removeAllViews()
        questionScreen.questions = questions
        questionScreen.displayQuestion(questions.questionIndexOf(questions.groupAt(targetGroup).getQuestion(0)))
        currentScreenForExerciseTab = questionScreen
        mainFrame.addView(currentScreenForExerciseTab.view)
    }

    override fun onReturnToOverview() {
        mainFrame.removeAllViews()
        currentScreenForExerciseTab = resultOverviewScreen
        mainFrame.addView(currentScreenForExerciseTab.view)
    }

    companion object {
        const val URL = "http://161.81.107.94/"
    }
}