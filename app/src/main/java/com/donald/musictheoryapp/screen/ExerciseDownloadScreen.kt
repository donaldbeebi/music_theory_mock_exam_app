package com.donald.musictheoryapp.screen

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.SERVER_URL
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.util.*
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class ExerciseDownloadScreen(
    activity: Activity
) : Screen(activity, R.layout.screen_exercise_downloading) {

    private val statusTextView = view.findViewById<TextView>(R.id.exercise_download_status_text)

    private fun setStatus(messageResource: String) {
        statusTextView.text = messageResource
    }

    // TODO: PROPER EXCEPTION HANDLING
    fun downloadTest(): Promise<Exercise> {
        setStatus(activity.getString(R.string.contacting_server_status))
        val promise = Promise<Exercise>()

        TokenManager.getAccessTokenAsync { accessToken ->
            if (accessToken == null) {
                displayToastForAccessTokenError(activity)
                activity.finish()
                return@getAccessTokenAsync
            }

            val bodyJson = JSONObject().apply {
                put("mode", "test")
                put("lang_pref", "en")
            }
            val client = OkHttp()
            val request = Request(Method.POST, "$SERVER_URL/exercise")
                .header("Authorization", "Bearer ${accessToken.token}")
                .body(bodyJson.toString())

            runBackground {
                val response = client(request)
                Log.d("ExerciseDownloadScreen", response.bodyString())

                val exerciseJson = parseJSONObjectOrNull(response.bodyString())
                if (exerciseJson == null) {
                    runMain {
                        displayToast("Failed to parse server response into exercise json", activity)
                        Log.d("ExerciseDownloadScreen", "Response from server: ${response.bodyString()}")
                        activity.finish()
                    }
                    return@runBackground
                }

                runBackground { saveExercise(activity, exerciseJson) }
                val images = exerciseJson.getJSONArray("images")
                val imagesNeeded = countImagesNeeded(images)
                val tracker = NumberTracker().apply {
                    target = imagesNeeded
                    onIncrement = {
                        runMain {
                            //CoroutineScope(Dispatchers.Main).launch {
                            setStatus(activity.getString(R.string.image_download_status, count, imagesNeeded))
                        }
                    }
                    onTarget = {
                        runMain {
                            //CoroutineScope(Dispatchers.Main).launch {
                            //showExercise(Exercise.fromJson(exerciseJson))
                            promise.complete(Exercise.fromJsonOrNull(exerciseJson))
                            setStatus(activity.getString(R.string.click_to_download_exercise_status))
                        }
                    }
                    activate()
                }
                for (index in 0 until images.length()) {
                    val image = images.getString(index)
                    if (!imageExists(image)) {
                        downloadImage(images.getString(index), tracker, accessToken)
                    }
                }
            }
        }
        return promise
    }

    fun downloadPractice(optionsJson: JSONObject): Promise<Exercise> {
        setStatus(activity.getString(R.string.contacting_server_status))
        val promise = Promise<Exercise>()

        TokenManager.getAccessTokenAsync { accessToken ->
            if (accessToken == null) {
                displayToastForAccessTokenError(activity)
                activity.finish()
                return@getAccessTokenAsync
            }

            val bodyJson = JSONObject().apply {
                put("mode", "practice")
                put("lang_pref", "en")
                put("options", optionsJson)
            }
            val request = Request(Method.POST, "$SERVER_URL/exercise")
                .header("Authorization", "Bearer ${accessToken.token}")
                .body(bodyJson.toString())
            val client = OkHttp()

            runBackground {
                val response = client(request)
                val exerciseJson = parseJSONObjectOrNull(response.bodyString()) ?: run { onJsonError(); return@runBackground }
                runBackground { saveExercise(activity, exerciseJson) }
                val images = exerciseJson.getJSONArrayOrNull("images") ?: run { onJsonError(); return@runBackground }
                val imagesNeeded = countImagesNeeded(images)
                val tracker = NumberTracker().apply {
                    target = imagesNeeded
                    onIncrement = {
                        runMain {
                            setStatus(activity.getString(R.string.image_download_status, count, imagesNeeded))
                        }
                    }
                    onTarget = {
                        runMain {
                            promise.complete(Exercise.fromJsonOrNull(exerciseJson))
                            //showExercise(Exercise.fromJson(exerciseJson))
                            setStatus(activity.getString(R.string.click_to_download_exercise_status))
                        }
                    }
                    activate()
                }
                for (index in 0 until images.length()) {
                    val image = images.getString(index)
                    if (!imageExists(image)) {
                        downloadImage(images.getString(index), tracker, accessToken)
                    }
                }
            }
        }

        return promise
    }

    private fun countImagesNeeded(images: JSONArray): Int {
        var count = 0
        for (i in 0 until images.length()) {
            if (!imageExists(images.getString(i))) {
                count++
            }
        }
        return count
    }

    private fun imageExists(title: String): Boolean {
        val dir = File(activity.filesDir, "images/$title.png")
        return dir.exists();
    }

    private fun downloadImage(title: String, tracker: NumberTracker, accessToken: DecodedJWT) {
        val dir = File(activity.filesDir, "images")
        if (!dir.exists()) dir.mkdir()
        val destination = File(dir, "$title.png")

        val client = OkHttp()
        val request = Request(Method.GET, "$SERVER_URL/images/${title}")
            .header("Authorization", "Bearer ${accessToken.token}")

        runBackground {
            val response = client(request)
            // TODO: NULL POINTER -> HANDLE FAILURE TO DOWNLOAD FROM SERVER
            val bitmap = BitmapFactory.decodeStream(response.body.stream) ?: throw IllegalStateException("Error decoding $title")
            try {
                destination.createNewFile()
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                FileOutputStream(destination).run {
                    write(bos.toByteArray())
                    flush()
                    close()
                }
                tracker.increment()
            } catch (e: Exception) {
                runMain {
                    e.printStackTrace()
                    setStatus(activity.getString(R.string.image_save_error))
                }
            }
        }
    }

    private fun onJsonError() {
        runMain {
            displayToastForJsonError(activity)
            activity.finish()
        }
    }

}