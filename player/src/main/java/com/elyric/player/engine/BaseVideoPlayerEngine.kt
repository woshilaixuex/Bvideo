package com.elyric.player.engine

import java.util.concurrent.CopyOnWriteArraySet

abstract class BaseVideoPlayerEngine : VideoPlayerEngine {
    private val listeners = CopyOnWriteArraySet<BPlayerListener>()

    override fun addListener(listener: BPlayerListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: BPlayerListener) {
        listeners.remove(listener)
    }

    protected fun dispatchPlaybackState(state: BPlayerPlaybackState) {
        listeners.forEach { it.onPlaybackStateChanged(state) }
    }

    protected fun dispatchIsPlayingChanged(isPlaying: Boolean) {
        listeners.forEach { it.onIsPlayingChanged(isPlaying) }
    }

    protected fun dispatchPlayerError(message: String?) {
        listeners.forEach { it.onPlayerError(message) }
    }
}
