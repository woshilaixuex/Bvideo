package com.elyric.player.controller

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.elyric.player.engine.BExoPlayerEngine
import com.elyric.player.view.BPlayerView
import java.lang.ref.WeakReference

@UnstableApi
class BVideoPlayerController(context: Context) {
    private val engine = BExoPlayerEngine(context)
    private var attachedPlayerViewRef: WeakReference<BPlayerView>? = null

    fun attach(playerView: BPlayerView) {
        val currentView = getAttachedPlayerView()
        if (currentView === playerView) {
            playerView.attachController(this)
            return
        }
        currentView?.detachController()
        if (currentView != null) {
            PlayerView.switchTargetView(engine.player, currentView.getInnerView(), playerView.getInnerView())
        } else {
            playerView.bind(engine.player)
        }
        attachedPlayerViewRef = WeakReference(playerView)
        playerView.attachController(this)
    }

    fun detach() {
        getAttachedPlayerView()?.detachController()
        getAttachedPlayerView()?.unbind()
        attachedPlayerViewRef = null
    }

    fun onPlayerViewDetached(playerView: BPlayerView) {
        if (getAttachedPlayerView() !== playerView) {
            return
        }
        playerView.detachController()
        playerView.unbind()
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

    fun addListener(listener: Player.Listener) {
        engine.player.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        engine.player.removeListener(listener)
    }

    private fun getAttachedPlayerView(): BPlayerView? {
        return attachedPlayerViewRef?.get()
    }
}
