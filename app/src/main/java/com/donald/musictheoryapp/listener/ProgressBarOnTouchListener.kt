package com.donald.musictheoryapp.listener

import android.widget.SeekBar
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.View

class ProgressBarOnTouchListener(private val seekBar: SeekBar) : OnTouchListener {

    private var initialX = 0f
    private var initialProgress = 0

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // TODO: IMPLEMENT APPLE-STYLE DRAG, FURTHER UP SLOWS DOWN DRAG
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialProgress = seekBar.progress
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x
                val distance = currentX - initialX
                val newProgress = (initialProgress + (distance * 2.5f / (seekBar.max)).toInt()).coerceAtLeast(0).coerceAtMost(seekBar.max)
                seekBar.progress = newProgress
                true
            }
            MotionEvent.ACTION_UP -> {
                true
            }
            else -> {
                view.performClick()
            }
        }
    }

}