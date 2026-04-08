package com.elyric.player

/**
 * 将视频播放器抽离出来,支持自定义或者替换播放内核
 */
interface VideoPlayerEngine {
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
}
