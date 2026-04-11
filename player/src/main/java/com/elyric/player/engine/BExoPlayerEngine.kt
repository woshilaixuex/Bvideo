package com.elyric.player.engine

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

@UnstableApi
class BExoPlayerEngine(context: Context) : VideoPlayerEngine {
    val player = ExoPlayer.Builder(context.applicationContext).build()
    private var currentUrl: String? = null

    override fun setSource(url: String) {
        currentUrl = url
    }

    override fun prepare() {
        val source = currentUrl ?: return
        player.setMediaItem(MediaItem.fromUri(source))
        player.prepare()
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun release() {
        player.release()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun setSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun getDuration(): Long {
        val duration = player.duration
        return if (duration >= 0) duration else 0L
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    override fun setVolume(volume: Float) {
        player.volume = volume.coerceIn(0f, 1f)
    }
}
