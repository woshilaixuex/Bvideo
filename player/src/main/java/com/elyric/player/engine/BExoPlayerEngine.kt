package com.elyric.player.engine

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.elyric.player.data.BPlayerCacheStore

@UnstableApi
class BExoPlayerEngine(context: Context) : BaseVideoPlayerEngine() {
    private val appContext = context.applicationContext
    private val trackSelector = DefaultTrackSelector(appContext).apply {
        parameters = buildUponParameters()
            .setAllowVideoMixedMimeTypeAdaptiveness(true)
            .setAllowVideoNonSeamlessAdaptiveness(true)
            .build()
    }
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            MIN_BUFFER_MS,
            MAX_BUFFER_MS,
            BUFFER_FOR_PLAYBACK_MS,
            BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        )
        .build()
    private val player = ExoPlayer.Builder(appContext)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        }
    private var currentUrl: String? = null
    private var renderView: PlayerView? = null
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            dispatchPlaybackState(
                when (playbackState) {
                    Player.STATE_IDLE -> BPlayerPlaybackState.IDLE
                    Player.STATE_BUFFERING -> BPlayerPlaybackState.BUFFERING
                    Player.STATE_READY -> BPlayerPlaybackState.READY
                    Player.STATE_ENDED -> BPlayerPlaybackState.ENDED
                    else -> BPlayerPlaybackState.IDLE
                }
            )
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            dispatchIsPlayingChanged(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            dispatchPlaybackState(BPlayerPlaybackState.ERROR)
            dispatchPlayerError(error.message)
        }
    }

    init {
        player.addListener(playerListener)
    }

    override fun attachToContainer(container: FrameLayout) {
        val view = renderView ?: createRenderView(container.context).also {
            renderView = it
        }
        attachRenderView(container, view)
    }

    override fun detachFromContainer(container: FrameLayout?) {
        val view = renderView ?: return
        val parent = view.parent as? ViewGroup ?: return
        if (container == null || parent === container) {
            parent.removeView(view)
        }
    }

    override fun switchContainer(from: FrameLayout, to: FrameLayout) {
        val view = renderView ?: createRenderView(to.context).also {
            renderView = it
        }
        detachFromContainer(from)
        attachRenderView(to, view)
    }

    override fun setSource(url: String) {
        currentUrl = url
    }

    override fun prepare() {
        val source = currentUrl ?: return
        dispatchPlaybackState(BPlayerPlaybackState.PREPARING)
        player.playWhenReady = true
        player.setMediaSource(BPlayerCacheStore.createMediaSource(appContext, source))
        player.prepare()
    }

    override fun play() {
        if (player.playbackState == Player.STATE_IDLE && currentUrl != null) {
            prepare()
            return
        }
        player.play()
    }

    override fun pause() {
        player.pause()
        dispatchIsPlayingChanged(false)
    }

    override fun stop() {
        player.stop()
        dispatchPlaybackState(BPlayerPlaybackState.IDLE)
        dispatchIsPlayingChanged(false)
    }

    override fun release() {
        detachFromContainer()
        player.removeListener(playerListener)
        player.release()
        renderView = null
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

    private fun createRenderView(context: Context): PlayerView {
        return PlayerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            useController = false
            setKeepContentOnPlayerReset(true)
            player = this@BExoPlayerEngine.player
        }
    }

    private fun attachRenderView(container: FrameLayout, view: PlayerView) {
        val parent = view.parent as? ViewGroup
        if (parent === container) {
            return
        }
        parent?.removeView(view)
        if (container.childCount > 0) {
            container.removeAllViews()
        }
        container.addView(view)
    }

    private companion object {
        const val MIN_BUFFER_MS = 2_500
        const val MAX_BUFFER_MS = 10_000
        const val BUFFER_FOR_PLAYBACK_MS = 250
        const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 500
    }
}
