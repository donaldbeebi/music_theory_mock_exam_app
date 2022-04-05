package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.donald.musictheoryapp.util.TokenManager
import com.donald.musictheoryapp.util.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.*
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Status

class MainActivity : AppCompatActivity() {

    private lateinit var profile: Profile
    private lateinit var signInClient: GoogleSignInClient

    private var backPressCountForExit = 0

    private lateinit var titleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.getParcelableExtra<Profile>("profile")?.let { profile = it } ?: throw IllegalStateException("Profile not found")
        signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<LinearLayout>(R.id.toolbar_back_button).apply { visibility = View.INVISIBLE; isEnabled = false }
        titleText = toolbar.findViewById<TextView>(R.id.toolbar_title_text).apply { setText(R.string.main_activity_title) }

        findViewById<CardView>(R.id.main_begin_test_button).setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java).apply {
                putExtra("action", "download")
                putExtra("mode", "test")
            }
            startActivity(intent)
        }

        findViewById<CardView>(R.id.main_begin_practice_button).setOnClickListener {
            startActivity(Intent(this, PracticeOptionsActivity::class.java))
        }

        findViewById<CardView>(R.id.main_exercise_list_button).setOnClickListener {
            startActivity(Intent(this, ExerciseListActivity::class.java))
        }

        findViewById<CardView>(R.id.main_profile_button).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("profile", profile)
            }
            startActivity(intent)
        }
    }

    /*
     * *********
     * CALLBACKS
     * *********
     */

    override fun onBackPressed() {
        onExit()
    }

    private fun onExit() {
        if (backPressCountForExit == 0) {
            backPressCountForExit = 1
            Toast.makeText(this, R.string.toast_exit_confirmation, Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed(
                { backPressCountForExit = 0 },
                DOUBLE_BACK_PRESS_DELAY_FOR_EXIT
            )
        }
        else {
            backPressCountForExit = 0
            finishAffinity()
        }
    }

    private fun onSignOut() {
        signInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, R.string.toast_sign_out_successful, Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            e.printStackTrace()
            Toast.makeText(this, R.string.toast_sign_out_failed , Toast.LENGTH_LONG).show()
        }
        startActivity(Intent(this, SignInActivity::class.java))
        finishAffinity()
    }

    private fun onDisconnectAccount() {
        val client = OkHttp()
        val request = org.http4k.core.Request(Method.DELETE, "$SERVER_URL/user")
            .header("Authorization", "Bearer ${TokenManager.idToken.token}")

        val context = this
        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO) {
                client(request)
            }
            when (response.status) {
                Status.OK -> {
                    signInClient.revokeAccess().addOnCompleteListener {
                        Toast.makeText(context, R.string.toast_disconnect_successful, Toast.LENGTH_LONG).show()
                        startActivity(Intent(context, SignInActivity::class.java))
                        finishAffinity()
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        Toast.makeText(context, R.string.toast_disconnect_unsuccessful_google, Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Log.d("MainActivity", "Request failed with status ${response.status}")
                    Toast.makeText(context, R.string.toast_disconnect_unsuccessful_amta, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {

        const val DOUBLE_BACK_PRESS_DELAY_FOR_EXIT = 2000L

    }

}