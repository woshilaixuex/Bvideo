package com.elyric.player.engine

import android.widget.FrameLayout

/**
 * 将播放器能力抽离出来:
 * 1. 播放控制
 * 2. 渲染视图挂载
 * 3. 状态回调分发
 */
interface VideoPlayerEngine {
    fun attachToContainer(container: FrameLayout)

    fun detachFromContainer(container: FrameLayout? = null)

    fun switchContainer(from: FrameLayout, to: FrameLayout) {
        detachFromContainer(from)
        attachToContainer(to)
    }

    fun setSource(url: String)

    fun prepare()

    fun play()

    fun pause()

    fun stop()

    fun release()

    fun seekTo(positionMs: Long)

    fun setSpeed(speed: Float)

    fun isPlaying(): Boolean

    fun getDuration(): Long

    fun getCurrentPosition(): Long

    fun setVolume(volume: Float)

    fun addListener(listener: BPlayerListener)

    fun removeListener(listener: BPlayerListener)
}
