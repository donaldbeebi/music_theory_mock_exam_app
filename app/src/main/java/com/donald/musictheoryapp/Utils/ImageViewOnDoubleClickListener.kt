package com.donald.musictheoryapp.Utils

import android.widget.ImageView
import com.donald.musictheoryapp.music.scoreview.ScoreView

class ImageViewOnDoubleClickListener(
    private val view: ImageView,
) : OnDoubleClickListener() {

    private val defaultWidth: Int = view.layoutParams.width
    private var zoomedIn = false

    override fun onDoubleClick() {
        val newLayoutParams = view.layoutParams
        newLayoutParams.width = if (zoomedIn) defaultWidth else (defaultWidth * 1.5).toInt()
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
        newLayoutParams.width = if (zoomedIn) defaultWidth else (defaultWidth * 1.5).toInt()
        view.layoutParams = newLayoutParams
        view.requestLayout()
        zoomedIn = !zoomedIn
        view.zoomedIn = zoomedIn
    }

}