package com.elyric.player.engine

interface BPlayerListener {
    fun onPlaybackStateChanged(state: BPlayerPlaybackState) {}

    fun onIsPlayingChanged(isPlaying: Boolean) {}

    fun onPlayerError(message: String?) {}
}
