package com.donald.musictheoryapp.Utils

import android.os.SystemClock
import android.view.View
import com.donald.musictheoryapp.Utils.OnDoubleClickListener

/**
 * @author     Srikanth Venkatesh
 * @version    1.0
 * @since      2014-09-15
 */
abstract class OnDoubleClickListener(
    private val doubleClickQualificationSpanInMillis: Long = 200
) : View.OnClickListener {
    private var timestampLastClick: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - timestampLastClick < doubleClickQualificationSpanInMillis) {
            onDoubleClick()
        }
        timestampLastClick = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick()
}