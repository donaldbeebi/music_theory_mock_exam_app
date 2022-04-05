/*
package com.donald.musictheoryapp.screen

import android.graphics.Bitmap
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.auth0.jwt.JWT
import com.donald.musictheoryapp.*
import com.donald.musictheoryapp.util.NumberTracker
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.saveExercise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ExerciseMenuScreen(
    activity: MainActivity,
    private val retrieveQuestionsCallback: (exercise: Exercise) -> Unit,
    signOutCallback: () -> Unit,
    disconnectAccountCallback: () -> Unit
    ) : Screen(activity, R.layout.screen_exercise_menu) {

    private val startButton = view.findViewById<Button>(R.id.exercise_menu_start_button)
    private val statusTextView = view.findViewById<TextView>(R.id.exercise_menu_status_text_view)
    private lateinit var tracker: NumberTracker

    init {
        startButton.setOnClickListener { onStartExercise() }
        view.findViewById<Button>(R.id.exercise_menu_sign_out_button).setOnClickListener { signOutCallback() }
        view.findViewById<Button>(R.id.exercise_menu_disconnect_account_button).setOnClickListener { disconnectAccountCallback() }
    }

    fun setStatus(messageResource: String, reEnableButton: Boolean) {
        statusTextView.text = messageResource
        startButton.isEnabled = reEnableButton
    }

    private fun onStartExercise() {
        if (accessToken.expiresAt.after(Date(Date().time - 5 * 60 * 1000))) {
            // if access token expires in 5 minutes or has already expired
            Log.d("ExerciseMenuScreen", "Requesting a new access token.")
            val request = object : StringRequest(
                Method.GET,
                "$SERVER_URL/access-token?old-access-token=${accessToken.token}",
                { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        accessToken = JWT.decode(jsonObject.getString("access_token"))
                        retrieveQuestions()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    error.printStackTrace()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("Authorization" to "Bearer ${idToken.token}")
                }

                override fun getParams(): MutableMap<String, String> {
                    return mutableMapOf("old-access-token" to accessToken.token)
                }
            }
            Volley.newRequestQueue(context).add(request)
        }
        else {
            retrieveQuestions()
        }
    }

    private fun retrieveQuestions() {
        setStatus(context.getString(R.string.contacting_server_status), false)
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(
            // method
            Method.GET,
            // url
            "$SERVER_URL/exercise/",
            // on response
            { response ->
                try {
                    val exerciseJson = JSONObject(response)
                    Log.d("ExerciseMenuScreen", exerciseJson.toString(4))
                    saveExercise(context, exerciseJson)
                    val exercise = Exercise.fromJson(exerciseJson)
                    val images = exerciseJson.getJSONArray("images")
                    val numberOfImagesNeeded = countImagesNeeded(images)
                    tracker = NumberTracker(
                        numberOfImagesNeeded,
                        onIncrement = {
                            setStatus(
                                context.getString(
                                    R.string.image_download_status, tracker.count(), tracker.target()
                                ), false
                            )
                        },
                        onTarget = {
                            try {
                                retrieveQuestionsCallback(exercise)
                                setStatus(context.getString(R.string.click_to_download_exercise_status), true)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                setStatus(context.getString(R.string.json_error_status), true)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                setStatus(context.getString(R.string.xml_error_status), true)
                            } catch (e: XmlPullParserException) {
                                e.printStackTrace()
                                setStatus(context.getString(R.string.xml_error_status), true)
                            }
                        }
                    )
                    for (i in 0 until images.length()) {
                        if (!imageExists(images.getString(i))) {
                            downloadImage(images.getString(i), tracker)
                        }
                    }
                } catch (e: JSONException) {
                    setStatus(context.getString(R.string.volley_error_status), true)
                    e.printStackTrace()
                }
            },
            // on error
            { error ->
                Log.d("from string request", "error: " + error.message)
                setStatus(context.getString(R.string.server_error_status), true)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${accessToken.token}")
            }
        }
        queue.add(request)
    }

    private fun countImagesNeeded(images: JSONArray): Int
    {
        var count = 0
        for (i in 0 until images.length()) {
            if (!imageExists(images.getString(i))) {
                count++
            }
        }
        return count
    }

    private fun imageExists(title: String): Boolean {
        val dir = File(context.filesDir, "images/$title.png")
        return dir.exists();
    }

    private fun downloadImage(title: String, tracker: NumberTracker) {
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdir()
        val destination = File(dir, "$title.png")
        val queue = Volley.newRequestQueue(context)
        val tag = "image_download"
        val request = object : ImageRequest(
            "$SERVER_URL/images/$title/",
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
                    setStatus(context.getString(R.string.image_save_error), true)
                }
            },
            2000,
            2000,
            ImageView.ScaleType.CENTER,
            Bitmap.Config.RGB_565,
            { error: VolleyError ->
                Log.e("ExerciseMenuScreen", "Volley error fetching image $title: $error")
                setStatus(context.getString(R.string.image_download_error), true)
                queue.cancelAll(tag)
                tracker.abort()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer ${accessToken.token}")
            }
        }
        request.tag = tag
        queue.add(request)
    }

}

 */