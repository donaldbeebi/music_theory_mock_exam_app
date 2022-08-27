package com.donald.musictheoryapp.listener

import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import com.donald.musictheoryapp.customview.scoreview.ScoreView

private val ZOOM_FACTOR = 2

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

class ImageViewOnDoubleClickListener(
    private val view: ImageView,
) : OnDoubleClickListener() {

    private val defaultWidth: Int = view.layoutParams.width
    private var zoomedIn = false

    override fun onDoubleClick() {
        val newLayoutParams = view.layoutParams
        newLayoutParams.width = if (zoomedIn) defaultWidth else (defaultWidth * ZOOM_FACTOR)
        view.layoutParams = newLayoutParams
        view.requestLayout()
        zoomedIn = !zoomedIn
    }

}

class ScoreViewOnDoubleClickListener(
    private val view: ScoreView,
) : OnDoubleClickListener() {

    private val defaultWidth: Int = view.layoutParams.width
    private var zoomedIn = false

    override fun onDoubleClick() {
        val newLayoutParams = view.layoutParams
        newLayoutParams.width = if (zoomedIn) defaultWidth else (defaultWidth * ZOOM_FACTOR)
        view.layoutParams = newLayoutParams
        view.requestLayout()
        zoomedIn = !zoomedIn
        view.zoomedIn = zoomedIn
    }

}