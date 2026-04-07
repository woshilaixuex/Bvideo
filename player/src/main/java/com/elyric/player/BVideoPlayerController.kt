package com.elyric.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class BVideoPlayerController(context: Context) {
    private val playerDataSource = BPlayerDataSource()
    private var attachedPlayerView: BPlayerView? = null
    val player = ExoPlayer.Builder(context.applicationContext).build()

    fun attach(playerView: BPlayerView) {
        attachedPlayerView = playerView
        playerView.bind(player)
    }

    fun detach() {
        attachedPlayerView?.unbind()
        attachedPlayerView = null
    }

    fun setSource(name: String, url: String) {
        playerDataSource.setSource(name,url)
    }

    fun setSource(url: String) {
        setSource(url, url)
    }

    fun prepare() {
        val url = playerDataSource.urlMap.values.firstOrNull() ?: return
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun play(url: String) {
        setSource(url)
        prepare()
        start()
    }

    fun start() {
        player.play()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        detach()
        player.release()
    }
}
