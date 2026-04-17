package com.elyric.player.controller

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.elyric.player.engine.BAndroidMediaPlayerEngine
import com.elyric.player.engine.BExoPlayerEngine
import com.elyric.player.engine.BPlayerListener
import com.elyric.player.engine.VideoPlayerEngine
import com.elyric.player.view.BPlayerView
import java.lang.ref.WeakReference

@UnstableApi
class BVideoPlayerController(
    private val engine: VideoPlayerEngine
) {
    private var attachedPlayerViewRef: WeakReference<BPlayerView>? = null

    constructor(context: Context) : this(BExoPlayerEngine(context))

    fun attach(playerView: BPlayerView) {
        val currentView = getAttachedPlayerView()
        if (currentView === playerView) {
            playerView.attachController(this)
            return
        }
        currentView?.detachController()
        if (currentView != null) {
            engine.switchContainer(currentView.getRenderContainer(), playerView.getRenderContainer())
        } else {
            engine.attachToContainer(playerView.getRenderContainer())
        }
        attachedPlayerViewRef = WeakReference(playerView)
        playerView.attachController(this)
    }

    fun detach() {
        val attachedView = getAttachedPlayerView()
        attachedView?.detachController()
        attachedView?.let { engine.detachFromContainer(it.getRenderContainer()) }
        attachedPlayerViewRef = null
    }

    fun onPlayerViewDetached(playerView: BPlayerView) {
        if (getAttachedPlayerView() !== playerView) {
            return
        }
        playerView.detachController()
        engine.detachFromContainer(playerView.getRenderContainer())
        attachedPlayerViewRef = null
    }

    fun setSource(url: String) {
        engine.setSource(url)
    }

    fun prepare() {
        engine.prepare()
    }

    fun play(url: String) {
        setSource(url)
        prepare()
        start()
    }

    fun start() {
        engine.play()
    }

    fun pause() {
        engine.pause()
    }

    fun togglePlayPause() {
        if (isPlaying()) {
            pause()
        } else {
            start()
        }
    }

    fun stop() {
        engine.stop()
    }

    fun release() {
        detach()
        engine.release()
    }

    fun seekTo(positionMs: Long) {
        engine.seekTo(positionMs)
    }

    fun setSpeed(speed: Float) {
        engine.setSpeed(speed)
    }

    fun isPlaying(): Boolean {
        return engine.isPlaying()
    }

    fun getDuration(): Long {
        return engine.getDuration()
    }

    fun getCurrentPosition(): Long {
        return engine.getCurrentPosition()
    }

    fun setVolume(volume: Float) {
        engine.setVolume(volume)
    }

    fun addListener(listener: BPlayerListener) {
        engine.addListener(listener)
    }

    fun removeListener(listener: BPlayerListener) {
        engine.removeListener(listener)
    }

    private fun getAttachedPlayerView(): BPlayerView? {
        return attachedPlayerViewRef?.get()
    }

    companion object {
        fun createWithExo(context: Context): BVideoPlayerController {
            return BVideoPlayerController(BExoPlayerEngine(context))
        }

        fun createWithMediaPlayer(context: Context): BVideoPlayerController {
            return BVideoPlayerController(BAndroidMediaPlayerEngine(context))
        }
    }
}
