package com.donald.musictheoryapp.screen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.MainActivity

abstract class Screen internal constructor(
    private val activity: MainActivity,
    viewResource: Int
    ) {

    val view: View

    protected val context: Context
    protected val layoutInflater: LayoutInflater
    protected var idToken: DecodedJWT
        get() = activity.idToken
        set(value) { activity.idToken = value }
    protected var accessToken: DecodedJWT
        get() = activity.accessToken
        set(value) { activity.accessToken = value }

    init {
        context = activity
        layoutInflater = LayoutInflater.from(context)
        view = layoutInflater.inflate(viewResource, null)
    }

    protected fun onAttach() {} //TODO: MAKE THIS ABSTRACT

    fun attachToFrame(frameLayout: FrameLayout) {
        frameLayout.addView(view)
        onAttach()
    }

}