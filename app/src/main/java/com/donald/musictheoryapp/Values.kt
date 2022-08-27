package com.donald.musictheoryapp
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.donald.musictheoryapp.util.Profile

var debug: Boolean by mutableStateOf(false)

val SERVER_URL: String
    get() {
        //return if (debug) "https://amta.site"
        return if (debug) "http://161.81.12.181".also { Log.d("Values", "debug SERVER_URL retrieved") }
        else "https://asia-southeast1-music-theory-app-1643350728268.cloudfunctions.net/amta-server". also { Log.d("Values", "cloud SERVER_URL retrieved") }
    }

const val TEST_COST = 50
const val PRACTICE_COST = 5

var CurrentProfile by mutableStateOf<Profile?>(null)