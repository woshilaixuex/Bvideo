package com.elyric.bredio.view.component.player

import android.os.SystemClock
import android.util.Log
import com.elyric.nav_api.TraceTime

object BFirstFrameTracer {
    private const val TAG = "BFirstFrame"
    private var url: String? = null
    private var clickTimeMs = 0L
    private var playRequestTimeMs = 0L
    private var finished = false

    @TraceTime(name = "BFirstFrameTracer.start")
    fun start(url: String) {
        this.url = url
        clickTimeMs = SystemClock.elapsedRealtime()
        playRequestTimeMs = 0L
        finished = false
        Log.d(TAG, "start url=$url")
    }

    @TraceTime(name = "BFirstFrameTracer.onPlayRequested")
    fun onPlayRequested(url: String) {
        if (this.url != url || finished) {
            start(url)
        }
        playRequestTimeMs = SystemClock.elapsedRealtime()
        Log.d(TAG, "playRequested url=$url clickToPlay=${playRequestTimeMs - clickTimeMs}ms")
    }

    @TraceTime(name = "BFirstFrameTracer.onReady")
    fun onReady(url: String?) {
        val currentUrl = this.url ?: return
        if (url != null && url != currentUrl) {
            return
        }
        if (finished || clickTimeMs <= 0L) {
            return
        }
        val readyTimeMs = SystemClock.elapsedRealtime()
        val clickToFirstFrameMs = readyTimeMs - clickTimeMs
        val playToFirstFrameMs = if (playRequestTimeMs > 0L) {
            readyTimeMs - playRequestTimeMs
        } else {
            -1L
        }
        finished = true
        Log.d(
            TAG,
            "firstFrameReady url=$currentUrl clickToFirstFrame=${clickToFirstFrameMs}ms playToFirstFrame=${playToFirstFrameMs}ms"
        )
    }

    @TraceTime(name = "BFirstFrameTracer.onError")
    fun onError(url: String?, message: String?) {
        val currentUrl = this.url ?: return
        if (url != null && url != currentUrl) {
            return
        }
        Log.d(TAG, "firstFrameError url=$currentUrl message=$message")
        finished = true
    }
}
