package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.auth0.jwt.JWT
import com.donald.musictheoryapp.util.TokenManager
import com.donald.musictheoryapp.util.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton
    private lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton = findViewById(R.id.exercise_menu_sign_in_button)
        signInButton.setOnClickListener { signInToGoogle() }
    }

    override fun onStart() {
        super.onStart()
        // check if the user has already signed in
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            if (!it.isExpired) {
                GlobalScope.launch { continueToAmta(it) }
            }
            else {
                silentSignInToGoogle()
            }
        }
    }

    private fun silentSignInToGoogle() {
        GoogleSignIn.getClient(this, gso).silentSignIn()
            .addOnCompleteListener(this) {
                OnCompleteListener<GoogleSignInAccount> { task ->
                    try {
                        GlobalScope.launch { continueToAmta(task.result) }
                    } catch (e: ApiException) {
                        Toast.makeText(this, R.string.toast_google_sign_in_exception, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun signInToGoogle() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private suspend fun continueToAmta(account: GoogleSignInAccount) {
        Log.d("SignInActivity", "continueToAmta called")
        val idToken = account.idToken ?: throw IllegalStateException("Error getting idToken from Google Account")
        TokenManager.idToken = JWT.decode(idToken)
        val client = OkHttp()
        val request = Request(Method.GET, "$SERVER_URL/user")
            .header("Authorization", "Bearer $idToken")
        val response = withContext(Dispatchers.IO) {
            client(request)
        }
        val context = this
        withContext(Dispatchers.Main) {
            when (response.status) {
                Status.OK -> {
                    val jsonObject = JSONObject(response.bodyString())
                    val accessToken = jsonObject.getString("access_token")
                    val nickname = jsonObject.getString("nickname")
                    Toast.makeText(context, getString(R.string.toast_successful_sign_in, nickname), Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("id_token", idToken)
                    intent.putExtra("access_token", accessToken)
                    intent.putExtra("profile", Profile(nickname))
                    startActivity(intent)
                    finish()
                }
                Status.NO_CONTENT -> {
                    Toast.makeText(context, R.string.toast_no_account_found, Toast.LENGTH_LONG).show()
                    SignUpDialog("Google") { nickname ->
                        GlobalScope.launch { signUpForAmta(idToken, nickname, "en") }
                    }.show(supportFragmentManager, "sign_up_dialog")

                }
                else -> {
                    Log.d("SignInActivity", "Error with status code: ${response.status}")
                }
            }
        }
    }

    private suspend fun signUpForAmta(idToken: String, nickname: String, langPref: String) {
        val client = OkHttp()
        val request = Request(Method.POST, "$SERVER_URL/user")
            .header("Content-Type", "application/json; charset=utr-8")
            .header("Authorization", "Bearer $idToken")
            .body(
                JSONObject().apply {
                    put("nickname", nickname)
                    put("lang_pref", "en")
                }.toString()
            )
        val response = withContext(Dispatchers.IO) {
            client(request)
        }
        val context = this
        withContext(Dispatchers.Main) {
            when (response.status) {
                Status.CREATED -> {
                    val jsonObject = JSONObject(response.bodyString())
                    val accessToken = jsonObject.getString("access_token")
                    TokenManager.idToken = JWT.decode(idToken)
                    Toast.makeText(context, getString(R.string.toast_successful_sign_up, nickname), Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra("id_token", idToken)
                        putExtra("access_token", accessToken)
                        putExtra("profile", Profile(nickname))
                    }
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(context, R.string.toast_unsuccessful_sign_up, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // return the sign in result back to exercise menu screen
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                GlobalScope.launch { continueToAmta(task.result) }
            } catch (e: ApiException) {
                Toast.makeText(this, R.string.toast_google_sign_in_exception, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {

        const val RC_SIGN_IN = 0

    }

}