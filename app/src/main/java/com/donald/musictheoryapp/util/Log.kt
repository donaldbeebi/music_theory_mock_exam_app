package com.donald.musictheoryapp.util

import android.util.Log
import kotlin.reflect.KClass

fun KClass<*>.log(message: String) {
    Log.d(simpleName, message)
}