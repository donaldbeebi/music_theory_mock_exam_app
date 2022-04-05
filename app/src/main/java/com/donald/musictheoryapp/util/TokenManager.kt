package com.donald.musictheoryapp.util

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.SERVER_URL
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status

object TokenManager {

    private val debug = true

    lateinit var idToken: DecodedJWT
    private var accessToken: DecodedJWT? = null

    fun getIdToken(callback: (Result<DecodedJWT, Error>) -> Unit) {
        TODO()
    }

    fun getAccessToken(): DecodedJWT? {
        val idToken = idToken
        val accessToken = accessToken
        return when {

            accessToken == null || accessToken.expiresSoon() -> {
                val client = OkHttp()
                val request = Request(Method.GET, "$SERVER_URL/access-token")
                    .header("Authorization", "Bearer ${idToken.token}")
                val response = client(request)
                if (response.status == Status.OK) {
                    extractAccessToken(response.bodyString())
                } else {
                    null
                }
            }

            else -> accessToken

        }
    }

    fun getAccessTokenAsync(callback: (DecodedJWT?) -> Unit) {
        //val idToken = idToken
        val accessToken = accessToken
        when {

            accessToken == null || accessToken.expiresSoon() -> {
                val client = OkHttp()
                val request = if (debug) {
                    Request(Method.GET, "$SERVER_URL/access-token")
                } else {
                    Request(Method.GET, "$SERVER_URL/access-token")
                        .header("Authorization", "Bearer ${idToken.token}")
                }
                runBackground {
                    val response = client(request)
                    when (response.status) {
                        Status.OK -> {
                            runMain { callback(extractAccessToken(response.bodyString())) }
                            return@runBackground
                        }
                    }
                }
            }

            else -> {
                runMain { callback(accessToken) }
                return
            }

        }
    }

    private fun extractAccessToken(jsonString: String): DecodedJWT? {
        val jsonObject = parseJSONObjectOrNull(jsonString) ?: return null
        val tokenString = jsonObject.getStringOrNull("access_token") ?: return null
        val decodedJWT = decodeOrNull(tokenString) ?: return null
        accessToken = decodedJWT
        return accessToken
    }

    private fun decodeOrNull(token: String): DecodedJWT? {
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