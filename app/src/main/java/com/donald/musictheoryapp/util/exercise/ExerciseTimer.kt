package com.donald.musictheoryapp.util.exercise

import android.os.CountDownTimer
import com.donald.musictheoryapp.util.Time
import kotlinx.coroutines.*

interface ExerciseTimerListener {
    fun onTimerTick(millisTicked: Long)
    fun onTimerPause(/*millisElapsedSinceLastTick: Long*/)
}

// TODO: FIX THE BUG WHERE THE USER CAN JUST KEEP TAPPING THE PAUSE BUTTON TO GET INFINITE TIME

class ExerciseTimer(
    val listener: ExerciseTimerListener,
    val coroutineScope: CoroutineScope,
    private val tickMillis: Long = 1000,
    //private val updateMillis: Long = 100
) {
    private var running = false
    //private var latestMillisAtLastUpdate: Long = System.currentTimeMillis()

    /*init {
        coroutineScope.launch {
            //var currentMillisAccumulated = 0L
            while (true) {

                /*val millisBeforeDelay = System.currentTimeMillis()
                latestMillisAtLastUpdate = millisBeforeDelay
                delay(updateMillis)
                if (running) {
                    val millisElapsedSinceDelay = System.currentTimeMillis() - millisBeforeDelay
                    currentMillisAccumulated += millisElapsedSinceDelay
                    if (currentMillisAccumulated >= tickMillis) {
                        currentMillisAccumulated -= tickMillis
                        listener.onTimerTick(tickMillis)
                    }
                }*/
            }
        }
    }*/

    /*constructor(
        initialTimeRemaining: Time,
        onTick: (Long) -> Unit,
        onOutOfTime: () -> Unit
    ) : this(initialTimeRemaining, GlobalScope)*/

    fun pause() {
        //val millisAtPause = System.currentTimeMillis()
        //val millisElapsedSinceLastUpdate = millisAtPause - latestMillisAtLastUpdate
        running = false
        listener.onTimerPause(/*millisElapsedSinceLastUpdate*/)
        //latestMillisAtLastUpdate = millisAtPause
    }

    fun start() {
        running = true
        coroutineScope.launch {
            while (running) {
                delay(tickMillis)
                if (running) {
                    listener.onTimerTick(tickMillis)
                }
            }
        }
    }
}