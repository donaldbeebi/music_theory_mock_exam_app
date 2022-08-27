package com.donald.musictheoryapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.android.billingclient.api.*
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.activitycomposables.profile.*
import com.donald.musictheoryapp.composables.general.CircularLoadingIndicator
import com.donald.musictheoryapp.composables.general.StandardAlertDialog
import com.donald.musictheoryapp.composables.general.StandardTextButton
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.profileTypography
import com.donald.musictheoryapp.util.*
import com.donald.musictheoryapp.util.Profile.CREATOR.tryGetProfile
import com.firebase.ui.auth.AuthUI
import de.hdodenhof.circleimageview.CircleImageView
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status

class ProfileActivity : AppCompatActivity() {
    private var profileState by mutableStateOf<ProfileState>(ProfileState.Loading)
    private var dialogState by mutableStateOf<DeleteAccountDialogState?>(null)

    private lateinit var profilePicture: CircleImageView
    private lateinit var nickname: TextView

    private lateinit var billingClient: BillingClient

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, R.string.toast_sign_out_successful, Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SignInActivity::class.java))
                ActivityCompat.finishAffinity(this)
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(this, R.string.toast_sign_out_failed , Toast.LENGTH_LONG).show()
            }
    }

    private fun showDeleteAccountDialog(
        profile: Profile
    ) {
        dialogState = DeleteAccountDialogState(
            profileType = profile.type,
            onDeleteAccount = this::deleteAccount,
            onDismiss = { dialogState = null }
        )
    }

    private fun handleExecuteError(executeError: ExecuteError) {
        when (executeError) {
            ExecuteError.Timeout -> displayToast(R.string.toast_timeout)
            ExecuteError.NoInternet -> displayToast(R.string.toast_no_internet)
        }
    }

    private fun deleteAccount() {
        val request = Request(Method.DELETE, "$SERVER_URL/user")
            .header("Authorization", "Bearer ${TokenManager.idToken.token}")

        val context = this
        runNetworkIO {
            val response = executeRequest(request) otherwise { return@runNetworkIO handleExecuteError(it) }
            runMain {
                when (response.status) {
                    Status.OK -> {
                        AuthUI.getInstance()
                            .delete(this)
                            .addOnCompleteListener {
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

    private fun getFreePoints(oldProfile: Profile) {
        runNetworkIO {
            val accessToken = TokenManager.tryGetAccessToken(this) otherwise { tokenError ->
                when (tokenError) {
                    TokenError.BadResponse -> runMain { displayToast(R.string.toast_bad_response_from_server) }
                    TokenError.NoInternet -> runMain { displayToast(R.string.toast_no_internet) }
                    TokenError.Timeout -> runMain { displayToast(R.string.toast_timeout) }
                }
                return@runNetworkIO
            }
            val request = Request(Method.POST, "$SERVER_URL/debug_free-points")
                .header("Authorization", "Bearer ${accessToken.token}")
            val response = executeRequest(request) otherwise { return@runNetworkIO handleExecuteError(it) }
            when (response.status) {
                Status.OK -> {
                    val newPoints = parseJSONObjectOrNull(response.bodyString())
                        ?.tryGetInt("points")
                        ?.otherwise {
                            this.displayToast(R.string.toast_bad_json_from_server)
                            Log.d("ProfileActivity", "Error getting value for key 'points'")
                            return@runNetworkIO
                        }
                        ?: run {
                            this.displayToast(R.string.toast_bad_json_from_server)
                            Log.d("ProfileActivity", "Response from server cannot be parsed into json")
                            return@runNetworkIO
                        }
                    runMain {
                        val newProfile = oldProfile.copy(points = newPoints)
                        CurrentProfile = newProfile
                        profileState = newReadyProfileState(newProfile)
                    }
                }
                else -> {
                    runMain {
                        this.displayToast(R.string.toast_bad_response_from_server)
                        Log.d("ProfileActivity", "Unexpected response code ${response.status}")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomTheme {
                ProfileScreen(
                    profileState = profileState,
                    deleteAccountDialogState = dialogState,
                    onBackPressed = this::onBackPressed
                )
            }
        }
        //loadProfile() TODO: MAYBE PUT THIS LINE BACK IN
        val profile = CurrentProfile ?: throw IllegalStateException("Profile not set")
        profileState = ProfileState.Ready(
            profilePicture = BitmapPainter(
                BitmapFactory.decodeResource(resources, R.drawable.profile_picture).asImageBitmap()
            ),
            profile = profile,
            onSignOut = this::signOut,
            onDeleteAccount = { showDeleteAccountDialog(profile) },
            onGetFreePoints = { getFreePoints(profile) }
        )
    }

    private fun startPurchaseConnection() {
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    // The BillingClient is ready. You can query purchases here.

                    TODO("Not yet implemented")
                }
                override fun onBillingServiceDisconnected() {
                    startPurchaseConnection()
                }
            }
        )
    }

    private fun querySkuDetails() {
        val skuList = listOf(
            "premium_upgrade",
            "gas"
        )
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skyDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {}
            }
        }
    }

    private fun launchPurchaseFlow() {
        val skuList = listOf(
            "premium_upgrade",
            "gas"
        )
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            Log.d("ProfileActivity", "hi bitch")
            Log.d("ProfileActivity", "firstSky is null = ${skuDetailsList?.size}")
            val firstSku = skuDetailsList?.get(0) ?: run {
                Log.d("ProfileActivity", "Sku details null")
                return@querySkuDetailsAsync
            }
            Log.d("ProfileActivity", firstSku.description)
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(firstSku)
                .build()

            when (val responseCode = billingClient.launchBillingFlow(this, flowParams).responseCode) {
                BillingClient.BillingResponseCode.OK -> Log.d("ProfileActivity", "Billing launched successfully")
                else -> Log.d("ProfileActivity", "Billing failed to launched with response code $responseCode")
            }
        }
    }

    override fun onBackPressed() = finish()

    private fun newReadyProfileState(
        profile: Profile
    ) = ProfileState.Ready(
        profilePicture = BitmapPainter(
            BitmapFactory.decodeResource(resources, R.drawable.profile_picture).asImageBitmap()
        ),
        profile = profile,
        onSignOut = this::signOut,
        onDeleteAccount = { showDeleteAccountDialog(profile) },
        onGetFreePoints = { getFreePoints(profile) }
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() = CustomTheme {
    ProfileScreen(
        /*profileState = ProfileState.Ready(
            profilePicture = painterResource(R.drawable.profile_picture),
            profile = Profile("Nickname", Profile.LangPref.English, Profile.Type.Google, 10000),
            onSignOut = {},
            onDeleteAccount = {},
            onGetFreePoints = {}
        ),*/
        profileState = ProfileState.Error({}),
        deleteAccountDialogState = null,
        onBackPressed = {}
    )
}

@Composable
private fun ProfileScreen(
    profileState: ProfileState,
    deleteAccountDialogState: DeleteAccountDialogState?,
    onBackPressed: () -> Unit
) = StandardScreen(
    activityTitle = stringResource(R.string.profile_activity_title),
    showsFocusBackdrop = deleteAccountDialogState != null,
    focusContent = { FocusContent(deleteAccountDialogState) },
    onBackDropTap = { deleteAccountDialogState?.onDismiss?.invoke() },
    onBackPressed = onBackPressed
) {
    when (profileState) {
        ProfileState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is ProfileState.Error -> ErrorScreen(profileState, modifier = Modifier.fillMaxSize())
        is ProfileState.Ready -> ReadyScreen(profileState, modifier = Modifier.fillMaxSize())
    }

}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        CircularLoadingIndicator(
            text = stringResource(R.string.profile_loading),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ErrorScreen(
    profileState: ProfileState.Error,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.profile_error),
            style = MaterialTheme.profileTypography.error,
            color = MaterialTheme.colors.onSurface
        )
        StandardTextButton(
            text = stringResource(R.string.profile_reload),
            onClick = profileState.onReload
        )
    }
}

@Composable
private fun ReadyScreen(
    profileState: ProfileState.Ready,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ReadyProfilePanel(
            profilePicture = profileState.profilePicture,
            profile = profileState.profile
        )
        StandardTextButton(
            text = stringResource(R.string.sign_out_button_text),
            onClick = profileState.onSignOut
        )
        StandardTextButton(
            text = stringResource(R.string.delete_account_button_text),
            onClick = profileState.onDeleteAccount
        )
        StandardTextButton(
            text = "Get 100 free points!",
            onClick = profileState.onGetFreePoints
        )
    }
}

@Composable
private fun DeleteAccountDialog(
    state: DeleteAccountDialogState
) = StandardAlertDialog(
    title = stringResource(R.string.delete_account_dialog_title),
    description = stringResource(R.string.delete_account_dialog_description, stringResource(state.profileType.stringResource)),
    positiveText = stringResource(R.string.ok_dialog_confirmation),
    onPositive = state.onDeleteAccount,
    negativeText = stringResource(R.string.no_thanks_dialog_confirmation),
    onNegative = state.onDismiss,
    onDismiss = state.onDismiss
)

@Composable
private fun FocusContent(
    deleteAccountDialogState: DeleteAccountDialogState?
) {
    if (deleteAccountDialogState != null) DeleteAccountDialog(
        state = deleteAccountDialogState
    )
}

class DeleteAccountDialogState(
    val profileType: Profile.Type,
    val onDeleteAccount: () -> Unit,
    val onDismiss: () -> Unit
)

sealed class ProfileState {
    object Loading : ProfileState()
    data class Error(
        val onReload: () -> Unit
    ) : ProfileState()
    class Ready(
        val profilePicture: Painter,
        val profile: Profile,
        val onSignOut: () -> Unit,
        val onDeleteAccount: () -> Unit,
        val onGetFreePoints: () -> Unit
    ) : ProfileState()
}