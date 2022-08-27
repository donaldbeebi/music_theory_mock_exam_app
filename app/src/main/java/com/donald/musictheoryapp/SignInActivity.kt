package com.donald.musictheoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.activitycomposables.signin.SignUpDialog
import com.donald.musictheoryapp.composables.general.ButtonState
import com.donald.musictheoryapp.composables.general.StandardTextButton
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.music.musicxml.*
import com.donald.musictheoryapp.music.musicxml.Time
import com.donald.musictheoryapp.util.*
import com.donald.musictheoryapp.util.Profile.CREATOR.tryGetProfile
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.http4k.core.*
import org.json.JSONObject

private const val MAX_RETRY_COUNT = 4

class SignInActivity : AppCompatActivity() {
    private var state by mutableStateOf<State>(
        State.Idling(onSignInButtonClick = this::onSignInButtonClick)
    )
    //private var retryCount = 0
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract(), ::onAuthentication)
    private val signInIntent: Intent = run {
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    private fun onSignInButtonClick() {
        signInLauncher.launch(signInIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CustomTheme {
                SignInScreen(state)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // check if the user has already signed in
        // TODO: PUT THIS BACK IN
        //trySilentSignIn()
    }

    private fun trySilentSignIn() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            state = State.ContactingServer
            getIdToken(user)
        }
    }

    private fun onAuthentication(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        Log.d("onSignInResult", "Result code = ${result.resultCode}")
        when (result.resultCode) {
            RESULT_OK -> {
                val user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException()
                getIdToken(user)
            }
            else -> {
                /*
                 * Sign in failed. If response is null the user canceled the
                 * sign-in flow using the back button. Otherwise check
                 * response.getError().getErrorCode() and handle the error.
                 */
                if (response == null) Log.d("SignInActivity", "Sign in cancelled")
                else Log.d("SignInActivity", "Sign in failed with error code: ${response.error?.errorCode}")
            }
        }
    }

    private fun getIdToken(user: FirebaseUser) {
        // getting an id token from firebase user
        user.getIdToken(false)
            .addOnFailureListener { exception ->
                this.displayToast(R.string.toast_id_token_error)
                exception.printStackTrace()
            }
            .addOnCanceledListener {
                Log.d("SignInActivity", "Id token task cancelled")
            }
            .addOnCompleteListener { task ->
                val token = task.result.token?.let { JWT.decode(it) }
                requireNotNull(token)
                TokenManager.idToken = token
                Log.d("SignInActivity", "id token retrieved: ${token.token}")
                runNetworkIO { startSequence(token, user) } //TODO
            }
    }

    private fun handleNetworkError(error: SignInError) = runMain {
        when (error) {
            is SignInError.NoInternet -> displayToast(R.string.toast_no_internet)
            is SignInError.BadResponse -> {
                displayToast(R.string.toast_bad_response_from_server)
                Log.d("SignInActivity", error.errorMessage)
            }
            is SignInError.UnknownProviderId -> displayToast(getString(R.string.toast_unknown_provider_id))
            is SignInError.Timeout -> displayToast(R.string.toast_timeout)
        }
        state = State.Idling(this::onSignInButtonClick)
    }

    private fun startSequence(idToken: DecodedJWT, user: FirebaseUser) {
        // 1. check if user exists
        val userExists = tryCheckIfUserExists(user) otherwise { return handleNetworkError(it) }
        if (userExists) {
            // 2. if the user exists, get profile from server
            val profile = getProfileFromAmta() otherwise { return handleNetworkError(it) }
            // 3. successful profile retrieval
            onProfileRetrieved(profile)
        } else {
            // 2. if user does not exist, create a profile
            val (nickname, langPref, type) = tryCreateProfile(user)
                .otherwise { return handleNetworkError(it) }
                ?: return onDialogDismiss()
            // 3. post the profile to the server
            val profile = postProfileToAmta(idToken, nickname, langPref, type) otherwise { return handleNetworkError(it) }
            // 4. successful profile retrieval
            onProfileRetrieved(profile)
        }
    }

    private fun tryCheckIfUserExists(user: FirebaseUser): Result<Boolean, SignInError> {
        // update state
        runMain { state = State.ContactingServer }

        // check if user exists
        val request = Request(Method.GET, "$SERVER_URL/user-exists")
            .query("uid", user.uid)
        val response = executeRequest(request) otherwise { return Result.Error(getSignInError(it)) }

        return when (response.status) {
            Status.OK -> Result.Value(true)
            Status.NO_CONTENT -> Result.Value(false)
            else -> {
                //runMain { handleBadResponseFromServer(response.bodyString()) }
                Result.Error(SignInError.BadResponse("Bad response from server: ${response.bodyString()}"))
            }
        }
    }

    private fun tryCreateProfile(user: FirebaseUser): Result<Triple<String, Profile.LangPref, Profile.Type>?, SignInError> {
        val profileType = getProfileType(user) ?: return Result.Error(SignInError.UnknownProviderId)
        var finished = false
        var result: Triple<String, Profile.LangPref, Profile.Type>? = null
        // update state
        runMain {
            state = State.CreatingProfile(
                profileType = profileType,
                onConfirm = { nickname, langPref ->
                    result = Triple(nickname, langPref, profileType)
                    finished = true
                    //postProfileToAmta(idToken, nickname, langPref, profileType)
                    //this.state = State.SigningUp
                },
                onDismiss = {
                    finished = true
                }//this::onDialogDismiss
            )
        }
        while (!finished) { Unit }
        return Result.Value(result)
    }

    private fun getProfileFromAmta(): Result<Profile, SignInError> {
        // update state
        val state = State.ContactingServer
        runMain { this.state = state }

        val accessToken = TokenManager.tryGetAccessToken(this) otherwise { tokenError ->
            return when (tokenError) {
                TokenError.NoInternet -> Result.Error(SignInError.NoInternet)
                TokenError.BadResponse -> Result.Error(
                    SignInError.BadResponse("Bad response from server while getting an access token")
                )
                TokenError.Timeout -> Result.Error(SignInError.Timeout)
            }
        }

        val request = Request(Method.GET, "$SERVER_URL/user")
            .header("Authorization", "Bearer ${accessToken.token}")
        val response = executeRequest(request) otherwise { return Result.Error(getSignInError(it)) }

        return when (response.status) {
            Status.OK -> {
                Log.d("SignInActivity", "Status OK when getting profile from server")
                val jsonObject = parseJSONObjectOrNull(response.bodyString())
                    ?: return Result.Error(SignInError.BadResponse("Bad response from server when getting a profile with body: ${response.bodyString()}"))
                val profile = jsonObject.tryGetProfile("profile").otherwise {
                    return Result.Error(SignInError.BadResponse("Cannot parse profile object from server"))
                } ?: run {
                    return Result.Error(SignInError.BadResponse("Profile object from server is null"))
                }
                Result.Value(profile)
                //runMain { onSignIn(profile) }
            }
            Status.NO_CONTENT -> Result.Error(
                SignInError.BadResponse("No user could be found, even though server responded that user exists")
            )/*runMain {
                /*this.state = State.CreatingProfile(
                    profileType = Profile.Type.Google,
                    onConfirm = { nickname, langPref ->
                        postProfileToAmta(idToken, nickname, langPref, Profile.Type.Google)
                        this.state = State.SigningUp
                    },
                    onDismiss = this::onDialogDismiss
                )
                this.displayToast(R.string.toast_no_account_found)*/
                handleBadResponseFromServer("No user could be found, even though server responded that user exists")
            }*/
            else -> Result.Error(
                SignInError.BadResponse("Unexpected bad response with body: ${response.bodyString()} and status: ${response.status.code}")
            )/*if (state.retry <= MAX_NO_OF_RETRIES) {
                getProfileFromAmta(idToken, user, retry = state.retry + 1)
            } else {
                runMain { onTooManyRetries() }
            }*/
        }
    }

    private fun postProfileToAmta(
        idToken: DecodedJWT,
        nickname: String,
        langPref: Profile.LangPref,
        type: Profile.Type
    ): Result<Profile, SignInError> {
        runMain { state = State.SigningUp }
        val request = Request(Method.POST, "$SERVER_URL/user")
            //.header("Content-Type", "application/json; charset=utr-8")
            .header("Authorization", "Bearer ${idToken.token}")
            .body(
                JSONObject().apply {
                    put("nickname", nickname)
                    put("lang_pref", langPref.jsonValue)
                    put("type", type.jsonValue)
                }.toString()
            )
        val response = executeRequest(request) otherwise { return Result.Error(getSignInError(it)) }
        return when (response.status) {
            Status.CREATED -> {
                this.state = State.SignedIn
                val jsonObject = JSONObject(response.bodyString())
                val profile = jsonObject.tryGetProfile("profile").otherwise { error ->
                    error.printStackTrace()
                    return Result.Error(SignInError.BadResponse("Cannot parse profile object from server"))
                } ?: run {
                    return Result.Error(SignInError.BadResponse("Profile object from server is null"))
                }
                //runMain { onSignIn(profile) }
                Result.Value(profile)
            }
            else -> Result.Error(SignInError.BadResponse(
                "Sign up was unsuccessful with status code ${response.status.code}")
            )//runMain { handleUnsuccessfulSignUp() }
        }
    }

    private fun onDialogDismiss() = runMain {
        state = State.Idling(this::onSignInButtonClick)
    }

    private fun onProfileRetrieved(profile: Profile) = runMain {
        state = State.SignedIn
        CurrentProfile = profile
        displayToast(getString(R.string.toast_successful_sign_up, profile.nickname))
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    sealed class State {
        class Idling(
            val onSignInButtonClick: () -> Unit
        ) : State()
        object ContactingServer : State()
        class CreatingProfile(
            val profileType: Profile.Type,
            val onConfirm: (String, Profile.LangPref) -> Unit,
            val onDismiss: () -> Unit
        ) : State()
        object SigningUp : State()
        object SignedIn : State()
    }

    sealed class SignInError {
        object NoInternet : SignInError()

        object Timeout : SignInError()

        class BadResponse(
            val errorMessage: String
        ) : SignInError()

        object UnknownProviderId : SignInError()
    }

    private fun getSignInError(executeError: ExecuteError): SignInError {
        return when (executeError) {
            ExecuteError.NoInternet -> SignInError.NoInternet
            ExecuteError.Timeout -> SignInError.Timeout
        }
    }
}

private val MockScore = Score(
    parts = arrayOf(
        Part(
            id = "",
            measures = arrayOf(
                Measure(
                    attributes = Attributes(
                        divisions = 1,
                        key = Key(fifths = 0, mode = Key.Mode.MAJOR),
                        time = Time(4, 4),
                        staves = 1,
                        clefs = arrayOf(Clef(sign = Sign.G, line = 2, printObject = true))
                    ),
                    notes = arrayListOf(
                        Note(
                            printObject = false,
                            pitch = Pitch(step = Step.C, alter = 0, octave = 4),
                            duration = 1,
                            type = Type.Whole,
                            accidental = null,
                            chord = false,
                            staff = 1,
                            notations = null,
                        )
                    ),
                    barline = null
                )
            )
        )
    )
)

@Preview
@Composable
private fun RootPreview() = CustomTheme {
    Box(
        modifier = Modifier
            .width(1080.dp)
            .height(2000.dp)
            .background(MaterialTheme.colors.background)
    ) {
        SignInScreen(state = SignInActivity.State.Idling({}))
    }
}

@Composable
private fun SignInScreen(state: SignInActivity.State) = StandardScreen(
    showsFocusBackdrop = state is SignInActivity.State.CreatingProfile,
    focusContent = { FocusContent(state) },
    onBackDropTap = { (state as? SignInActivity.State.CreatingProfile)?.onDismiss?.invoke() }
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            StatusProgressRing(state)
            StatusText(state)
            SignInButton(state)
        }
        DebugSwitch(modifier = Modifier.align(Alignment.BottomCenter))
    }
    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DebugSwitch()
        StatusProgressRing(state)
        StatusText(state)
        SignInButton(state)
    }*/
}

@Composable
private fun DebugSwitch(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "Use Local Server",
            style = MaterialTheme.typography.body2
        )
        Switch(checked = debug, onCheckedChange = { debug = it })
    }
}

@Composable
private fun StatusProgressRing(state: SignInActivity.State) {
    when (state) {
        is SignInActivity.State.ContactingServer -> {
            CircularProgressIndicator()
        }
        else -> { Unit }
    }
}

@Composable
private fun StatusText(state: SignInActivity.State) {
    val statusText = when (state) {
        is SignInActivity.State.Idling -> stringResource(R.string.sign_in_to_continue_status)
        is SignInActivity.State.ContactingServer -> stringResource(R.string.contacting_server_status)
        is SignInActivity.State.CreatingProfile -> stringResource(R.string.creating_new_profile_status)
        is SignInActivity.State.SigningUp -> stringResource(R.string.signing_up_status)
        is SignInActivity.State.SignedIn -> stringResource(R.string.signed_in_status)
    }
    Text(
        text = statusText,
        style = MaterialTheme.typography.body2,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SignInButton(state: SignInActivity.State) {
    StandardTextButton(
        text = stringResource(R.string.sign_in),
        state = when (state) {
            is SignInActivity.State.Idling -> {
                ButtonState.Enabled(onClick = state.onSignInButtonClick)
            }
            else -> {
                ButtonState.Disabled
            }
        }
    )
}

@Composable
private fun BoxScope.FocusContent(state: SignInActivity.State) {
    if (state is SignInActivity.State.CreatingProfile) SignUpDialog(
        profileType = state.profileType,
        onConfirm = state.onConfirm,
        onDismiss = state.onDismiss
    )
}

private fun getProfileType(user: FirebaseUser): Profile.Type? {
    for (providerData in user.providerData) {
        val type = Profile.Type.fromProviderIdOrNull(providerData.providerId)
        if (type != null) return type
    }
    return null
}