package com.donald.musictheoryapp.util

import android.content.Context
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.SERVER_URL
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import java.lang.Error

enum class TokenError { NoInternet, BadResponse, Timeout }

object TokenManager {
    lateinit var idToken: DecodedJWT
    private var accessToken: DecodedJWT? = null

    fun getIdToken(callback: (Result<DecodedJWT, Error>) -> Unit) {
        TODO()
    }

    fun tryGetAccessToken(context: Context): Result<DecodedJWT, TokenError> {
        val idToken = idToken
        val accessToken = accessToken
        return when {
            accessToken == null || accessToken.expiresSoon() -> {
                val request = Request(Method.GET, "$SERVER_URL/access-token")
                    .header("Authorization", "Bearer ${idToken.token}")
                val response = context.executeRequest(request) otherwise { executeError ->
                    return Result.Error(
                        when (executeError) {
                            ExecuteError.Timeout -> TokenError.Timeout
                            ExecuteError.NoInternet -> TokenError.NoInternet
                        }
                    )
                }
                val newToken = extractAccessToken(response.bodyString())

                if (response.status != Status.OK || newToken == null) {
                    Log.d("TokenManager", "Error while getting an access token from the server with status code: ${response.status.code} and body: ${response.bodyString()}")
                    Result.Error(TokenError.BadResponse)
                }
                else Result.Value(newToken).also { this.accessToken = newToken }
            }
            else -> Result.Value(accessToken)
        }
    }

    private fun extractAccessToken(jsonString: String): DecodedJWT? {
        val jsonObject = parseJSONObjectOrNull(jsonString) ?: return null
        val tokenString = jsonObject.getStringOrNull("access_token") ?: return null
        val decodedJWT = decodeJWTOrNull(tokenString) ?: return null
        accessToken = decodedJWT
        return accessToken
    }

    private fun decodeJWTOrNull(token: String): DecodedJWT? {
        return try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            null
        }
    }

    private fun DecodedJWT.expiresSoon(): Boolean {
        return expiresAt.time - (5 * 60 * 1000) < System.currentTimeMillis()
    }
}