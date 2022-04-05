package com.donald.musictheoryapp.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*
import java.util.function.Supplier

private const val keepAliveTime = 1L
private val keepAliveTimeUnit = TimeUnit.SECONDS
private val cpuCount = Runtime.getRuntime().availableProcessors()
private val maxPoolSize = cpuCount * 2
private val handler = Handler(Looper.getMainLooper())

private val backgroundExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
    cpuCount,
    maxPoolSize,
    keepAliveTime,
    keepAliveTimeUnit,
    LinkedBlockingQueue(),
    Executors.defaultThreadFactory()
)

fun runBackground(runnable: Runnable) {
    backgroundExecutor.execute(runnable)
}

fun runMain(runnable: Runnable) {
    handler.post(runnable)
}

fun runMainDelayed(delay: Long, runnable: Runnable) {
    handler.postDelayed(runnable, delay)
}