package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import com.donald.musictheoryapp.util.TokenManager
import com.donald.musictheoryapp.util.Profile
import com.donald.musictheoryapp.util.runBackground
import com.donald.musictheoryapp.util.runMain
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import de.hdodenhof.circleimageview.CircleImageView
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Status

class ProfileActivity : AppCompatActivity() {

    private lateinit var signInClient: GoogleSignInClient

    private lateinit var profilePicture: CircleImageView
    private lateinit var nickname: TextView


    private val signOut = fun() {
        signInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, R.string.toast_sign_out_successful, Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            e.printStackTrace()
            Toast.makeText(this, R.string.toast_sign_out_failed , Toast.LENGTH_LONG).show()
        }
        startActivity(Intent(this, SignInActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }

    private val showDisconnectAccountDialog = fun() {
        DisconnectAccountDialog(disconnectAccount).show(supportFragmentManager, null)
    }

    private val disconnectAccount = fun() {
        val client = OkHttp()
        val request = org.http4k.core.Request(Method.DELETE, "$SERVER_URL/user")
            .header("Authorization", "Bearer ${TokenManager.idToken.token}")

        val context = this
        runBackground {
            val response = client(request)
            runMain {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profile = intent.getParcelableExtra<Profile>("profile")
            ?: throw IllegalStateException("Profile not found")

        profilePicture = findViewById(R.id.profile_picture)
        nickname = findViewById(R.id.profile_nickname)

        findViewById<LinearLayout>(R.id.toolbar_back_button).setOnClickListener { finish() }
        findViewById<TextView>(R.id.toolbar_back_button_text).setText(R.string.main_activity_title)
        findViewById<TextView>(R.id.toolbar_title_text).setText(R.string.profile_activity_title)

        findViewById<Button>(R.id.profile_sign_out_button).setOnClickListener { signOut() }
        findViewById<Button>(R.id.profile_disconnect_account_button).setOnClickListener { showDisconnectAccountDialog() }

        signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        displayProfile(profile)
    }

    override fun onBackPressed() = finish()

    fun displayProfile(profile: Profile) {
        profilePicture.setImageResource(R.drawable.profile_picture)
        nickname.text = profile.nickname
    }

}