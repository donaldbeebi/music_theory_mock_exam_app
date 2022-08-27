package com.donald.musictheoryapp.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.*

private const val keepAliveTime = 1L
private val keepAliveTimeUnit = TimeUnit.SECONDS
private val cpuCount = Runtime.getRuntime().availableProcessors()
private val maxPoolSize = cpuCount * 2
private val handler = Handler(Looper.getMainLooper())
private val diskIO: ExecutorService = CustomExecutor(1)//Executors.newSingleThreadExecutor()
private val networkIO: ExecutorService = CustomExecutor((cpuCount / 2).coerceAtLeast(1))//Executors.newFixedThreadPool((cpuCount / 2).coerceAtLeast(1))
private val CPUBound: ExecutorService = CustomExecutor((cpuCount / 2).coerceAtLeast(1))

private val backgroundExecutorOld: ThreadPoolExecutor = ThreadPoolExecutor(
    cpuCount,
    maxPoolSize,
    keepAliveTime,
    keepAliveTimeUnit,
    LinkedBlockingQueue(),
    Executors.defaultThreadFactory()
)

class CustomExecutor(threadCount: Int) : ThreadPoolExecutor(
    threadCount,
    threadCount,
    keepAliveTime,
    keepAliveTimeUnit,
    LinkedBlockingQueue(),
    Executors.defaultThreadFactory()
) {
    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        if (r is Future<*>) try {
            r.get()
        } catch (e: CancellationException) {}
        t?.let { throw it }
    }
}

fun <R> runDiskIO(runnable: Callable<R>): Future<R> {
    //diskIO.execute(runnable)
    return diskIO.submit(runnable)
}

fun <R> runNetworkIO(runnable: Callable<R>): Future<R> {
    return networkIO.submit(runnable)
}

fun <R> runCPUBound(runnable: Callable<R>): Future<R> {
    return CPUBound.submit(runnable)
}

/*
fun <R> awaitBackground(runnable: () -> R?): R? {
    var finished = false
    var result: R? = null
    diskIO.execute {
        result = runnable()
        finished = true
        Log.d("hello", "finish")
    }
    while (true) {
        //Log.d("awaitBackground", "waiting")
        Thread.sleep(10)
        if (finished) {
            Log.d("hey", "asd")
            return result
        }
    }
}

 */

fun runMain(runnable: Runnable) {
    handler.post(runnable)
}

fun runMainDelayed(delay: Long, runnable: Runnable) {
    handler.postDelayed(runnable, delay)
}