package com.donald.musictheoryapp.listener

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import com.donald.musictheoryapp.util.runMainDelayed

abstract class QuickPeekSingleClickListener(
    private val downToPeekThreshold: Long,
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())
    var state = State.Idling

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> reactToEvent(Event.Down)
            MotionEvent.ACTION_UP -> reactToEvent(Event.Up)
        }
        return true
    }

    private fun reactToEvent(event: Event) {
        when (state) {
            State.Idling -> {
                when (event) {
                    Event.Down -> switchToState(State.FirstDownHeld)
                    else -> {}
                }
            }
            State.FirstDownHeld -> {
                when (event) {
                    Event.Up -> switchToState(State.Peeking)
                    Event.Delayed -> switchToState(State.Peeking)
                    else -> {}
                }
            }
            State.Peeking -> {
                if (event == Event.Up) switchToState(State.Idling)
            }
        }
    }

    private fun switchToState(state: State) {
        exitState(this.state)
        when (state) {
            State.Idling -> {

            }
            State.FirstDownHeld -> {
                handler.postDelayed(downToPeekThreshold) { reactToEvent(Event.Delayed) }
            }
            State.Peeking -> {
                onPeek()
            }
        }
        this.state = state
    }

    private fun exitState(state: State) {
        when (state) {
            State.Idling -> {

            }
            State.FirstDownHeld -> {
                handler.removeCallbacksAndMessages(null)
            }
            State.Peeking -> {
                onReturn()
            }
        }
    }

    abstract fun onPeek()

    abstract fun onReturn()

    private fun Handler.postDelayed(delay: Long, runnable: Runnable) = postDelayed(runnable, delay)

    enum class State {
        Idling,
        FirstDownHeld,
        Peeking
    }

    enum class Event {
        Up,
        Down,
        Delayed
    }

}