package com.donald.musictheoryapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.SERVER_URL
import com.donald.musictheoryapp.util.Profile.CREATOR.tryGetProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


// TODO: MAKE ALL REQUESTS EXECUTED VIA THE executeRequest FUNCTION

private const val MAX_RETRY_COUNT = 10//5

private val HttpClient = OkHttp(
    OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .build()
)

private val HttpClientForImage = OkHttp(
    OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .build()
)

/**
 * @return null when the response from server cannot be decoded into a bitmap
 */
fun downloadImage(imageName: String, accessToken: DecodedJWT): Bitmap? {
    val request = Request(Method.GET, "$SERVER_URL/images/${imageName}")
        .header("Authorization", "Bearer ${accessToken.token}")
    var attempts = 0
    var bitmap: Bitmap? = null
    do {
        attempts++
        val response = HttpClientForImage.invoke(request)
        val downloadedBitmap = BitmapFactory.decodeStream(response.body.stream)
        if (downloadedBitmap != null) {
            bitmap = downloadedBitmap
            break
        }
    } while (attempts <= MAX_RETRY_COUNT)
    return bitmap
}

/**
 * This function is blocking
 */
fun Context.downloadExerciseJson(optionsJson: JSONObject, accessToken: DecodedJWT): JSONObject? {
    val request = Request(Method.POST, "$SERVER_URL/exercise")
        .header("Authorization", "Bearer ${accessToken.token}")
        .body(optionsJson.toString())
    val response = executeRequest(request) otherwise { return null }
    // TODO: CRITICAL THIS CRASHES APP AND MAKES APP UNRESPONSIVE WHEN NULL
    return parseJSONObjectOrNull(response.bodyString()).also { Log.d("HttpUtils", "response.bodyString() = ${response.bodyString()}") }
}

private fun networkConnected(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }
    return result
}

fun Context.getProfile(): Result<Profile, NetworkError> {
    val accessToken = TokenManager.tryGetAccessToken(this) otherwise { tokenError ->
        return when (tokenError) {
            TokenError.NoInternet -> Result.Error(NetworkError.NoInternet)
            TokenError.BadResponse -> Result.Error(NetworkError.BadResponse)
            TokenError.Timeout -> Result.Error(NetworkError.Timeout)
        }
    }
    val request = Request(Method.GET, "$SERVER_URL/user")
        .header("Authorization", "Bearer ${accessToken.token}")
    val response = executeRequest(request) otherwise { executeError ->
        return when (executeError) {
            ExecuteError.NoInternet -> Result.Error(NetworkError.NoInternet)
            ExecuteError.Timeout -> Result.Error(NetworkError.Timeout)
        }
    }
    val bodyJson = parseJSONObjectOrNull(response.bodyString()) ?: return Result.Error(NetworkError.BadJson)
    val profile = bodyJson.tryGetProfile("profile").otherwise { return Result.Error(NetworkError.BadJson) }
        ?: return Result.Error(NetworkError.BadJson)
    return Result.Value(profile)
}

private fun internetConnected(): Boolean {
    try {
        val address: InetAddress = InetAddress.getByName("www.google.com")
        return !address.equals("")
    } catch (e: UnknownHostException) {
        Log.d("NetworkUtils", "Internet is not available.")
    }
    return false
}

fun Context.internetIsAvailable() = networkConnected(this) && internetConnected()

/**
 * @return null if internet is not available
 */
fun Context.executeRequest(request: Request): Result<Response, ExecuteError> {
    if (internetIsAvailable()) {
        Log.d("NetworkUtils", "Internet is available")
        var response: Response
        var retry = -1
        do {
            Log.d("NetworkUtils", "Trying with retry: $retry")
            response = HttpClient(request)
            if (!(response.status == Status.GATEWAY_TIMEOUT || response.status == Status.CLIENT_TIMEOUT)) break
            retry++
        } while (retry <= MAX_RETRY_COUNT)
        if (response.status == Status.GATEWAY_TIMEOUT || response.status == Status.CLIENT_TIMEOUT) {
            return Result.Error(ExecuteError.Timeout)
        }
        //if (response.status == Status.OK && response.bodyString().isEmpty()) printStream(response.body.stream)
        return Result.Value(response)
    }
    Log.d("NetworkUtils", "Internet is NOT available")
    return Result.Error(ExecuteError.NoInternet)
}

enum class ExecuteError {
    NoInternet,
    Timeout
}

enum class NetworkError {
    NoInternet,
    BadJson,
    BadResponse,
    Timeout
}

// TODO: TRY TO MAKE THIS NON-BLOCKING
fun printStream(stream: InputStream) = CoroutineScope(Dispatchers.IO).launch {
    val buffer = ByteArray(1024)
    stream.read(buffer)
    while (true) {
        val byteCount = stream.read(buffer)
        Log.d("NetworkUtils", """
            number of bytes read: $byteCount
            bytes in buffer: ${buffer.toPrettyString()}
            bytes in buffer to String (UTF-8): ${buffer.toString(Charsets.UTF_8)} 
        """.trimIndent())
        delay(1000)
    }
}

private fun ByteArray.toPrettyString(): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append('[')
    forEachIndexed { index, byte ->
        if (index != 0) stringBuilder.append(", ")
        stringBuilder.append(byte.toString(radix = 16))
    }
    stringBuilder.append(']')
    return stringBuilder.toString()
}