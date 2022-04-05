package com.donald.musictheoryapp.screen

import android.app.Activity
import android.view.LayoutInflater
import android.view.View

abstract class Screen internal constructor(
    protected val activity: Activity,
    viewResource: Int
) {
    protected val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
    val view: View = layoutInflater.inflate(viewResource, null)
}