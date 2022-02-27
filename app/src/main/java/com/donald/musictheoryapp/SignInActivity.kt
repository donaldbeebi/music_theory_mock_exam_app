package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
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
                continueToAmta(it)
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
                        continueToAmta(task.result)
                    } catch (e: ApiException) {
                        Toast.makeText(this, R.string.toast_google_sign_in_exception, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun signInToGoogle() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun continueToAmta(account: GoogleSignInAccount) {
        val idToken = account.idToken ?: throw IllegalStateException("Error getting idToken from Google Account")
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(
            // method
            Method.GET,
            // url
            "$SERVER_URL/user/",
            // on response
            { response ->
                val jsonObject = JSONObject(response)
                val accessToken = jsonObject.getString("access_token")
                val nickname = jsonObject.getString("nickname")
                Toast.makeText(this, getString(R.string.toast_successful_sign_in, nickname), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id_token", idToken)
                intent.putExtra("access_token", accessToken)
                startActivity(intent)
                finish()
            },
            // on error
            { error ->
                when (error.networkResponse.statusCode) {
                    404 -> {
                        Log.d("SignInActivity", "User not found. Signing up instead.")
                        Toast.makeText(this, R.string.toast_no_account_found, Toast.LENGTH_LONG).show()
                        signUpForAmta(account)
                    }
                    else -> {
                        error.printStackTrace()
                        Log.d("SignInActivity", "Volley error with status code: ${error.networkResponse.statusCode}")
                    }
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Authorization" to "Bearer $idToken")
            }
        }

        queue.add(request)
    }

    private fun signUpForAmta(account: GoogleSignInAccount) {
        val idToken = account.idToken ?: throw IllegalStateException("Error getting email from Google Account")
        val dialog = SignUpDialog("Google") { nickname ->
            val queue = Volley.newRequestQueue(this)
            object : StringRequest(
                Method.POST,
                "$SERVER_URL/user/",
                { response ->
                    val jsonObject = JSONObject(response)
                    val accessToken = jsonObject.getString("access_token")
                    Toast.makeText(this, getString(R.string.toast_successful_sign_up, nickname), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id_token", idToken)
                    intent.putExtra("access_token", accessToken)
                    startActivity(intent)
                    finish()
                },
                { Toast.makeText(this, R.string.toast_unsuccessful_sign_up, Toast.LENGTH_SHORT).show() }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("Authorization" to "Bearer $idToken")
                }
                override fun getBodyContentType(): String {
                    return "application/json; charset=utr-8"
                }
                override fun getBody(): ByteArray {
                    return JSONObject().apply {
                        put("nickname", nickname)
                        put("lang_pref", "en")
                    }.toString().toByteArray()
                }
            }.also { queue.add(it) }
        }
        dialog.show(supportFragmentManager, "sign_up_dialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // return the sign in result back to exercise menu screen
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                continueToAmta(task.result)
            } catch (e: ApiException) {
                Toast.makeText(this, R.string.toast_google_sign_in_exception, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {

        const val RC_SIGN_IN = 0

    }

}