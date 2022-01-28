package com.donald.musictheoryapp.Screen

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.donald.musictheoryapp.MainActivity
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.Utils.NumberTracker
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExerciseMenuScreen(
    context: Context?,
    view: View,
    private val onRetrieveQuestions: (jsonObject: JSONObject) -> Unit,
    ) : Screen(context, view) {

    private val startButton = view.findViewById<Button>(R.id.exercise_menu_start_button)
    private val statusTextView = view.findViewById<TextView>(R.id.exercise_menu_status_text_view)
    private lateinit var tracker: NumberTracker

    init {
        startButton.setOnClickListener {
            onStartExercise()
        }

    }

    fun setStatus(messageResource: String, reEnableButton: Boolean) {
        statusTextView.text = messageResource
        startButton.isEnabled = reEnableButton
    }

    private fun onStartExercise() {
        setStatus(context.getString(R.string.contacting_server_status), false)
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            // method
            Request.Method.GET,
            // url
            MainActivity.URL + "exercise",
            // on response
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val images = jsonObject.getJSONArray("images")
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
                                onRetrieveQuestions(jsonObject)
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
        )
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
        val request = ImageRequest(
            MainActivity.URL + "images/" + title,
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
            Bitmap.Config.RGB_565
        ) { error: VolleyError ->
            Log.e("Volley error while fetch image $title", error.toString())
            setStatus(context.getString(R.string.image_download_error), true)
            queue.cancelAll(tag)
            tracker.abort()
        }
        request.tag = tag
        queue.add(request)
    }
}