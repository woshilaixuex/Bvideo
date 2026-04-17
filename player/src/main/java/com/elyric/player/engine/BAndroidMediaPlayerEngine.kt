package com.elyric.player.engine

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout

class BAndroidMediaPlayerEngine(context: Context) : BaseVideoPlayerEngine() {
    private val mediaPlayer = MediaPlayer()
    private val appContext = context.applicationContext
    private var currentUrl: String? = null
    private var renderView: TextureView? = null
    private var surface: Surface? = null
    private var isPrepared = false
    private var isPreparing = false
    private var playWhenReady = false

    init {
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        mediaPlayer.setOnPreparedListener {
            isPrepared = true
            isPreparing = false
            dispatchPlaybackState(BPlayerPlaybackState.READY)
            if (playWhenReady) {
                it.start()
                dispatchIsPlayingChanged(true)
            }
        }
        mediaPlayer.setOnCompletionListener {
            playWhenReady = false
            dispatchPlaybackState(BPlayerPlaybackState.ENDED)
            dispatchIsPlayingChanged(false)
        }
        mediaPlayer.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> dispatchPlaybackState(BPlayerPlaybackState.BUFFERING)
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> dispatchPlaybackState(BPlayerPlaybackState.READY)
            }
            false
        }
        mediaPlayer.setOnErrorListener { _, what, extra ->
            isPrepared = false
            isPreparing = false
            playWhenReady = false
            dispatchPlaybackState(BPlayerPlaybackState.ERROR)
            dispatchPlayerError("MediaPlayer error: what=$what extra=$extra")
            dispatchIsPlayingChanged(false)
            true
        }
    }

    override fun attachToContainer(container: FrameLayout) {
        val view = renderView ?: createRenderView(container.context).also {
            renderView = it
        }
        attachRenderView(container, view)
        bindSurfaceIfReady(view.surfaceTexture)
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
        bindSurfaceIfReady(view.surfaceTexture)
    }

    override fun setSource(url: String) {
        currentUrl = url
    }

    override fun prepare() {
        val source = currentUrl ?: return
        isPrepared = false
        isPreparing = true
        playWhenReady = false
        dispatchPlaybackState(BPlayerPlaybackState.PREPARING)
        mediaPlayer.reset()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        surface?.let { mediaPlayer.setSurface(it) }
        mediaPlayer.setDataSource(appContext, android.net.Uri.parse(source))
        mediaPlayer.prepareAsync()
    }

    override fun play() {
        playWhenReady = true
        if (isPrepared) {
            mediaPlayer.start()
            dispatchIsPlayingChanged(true)
        }
    }

    override fun pause() {
        playWhenReady = false
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        dispatchIsPlayingChanged(false)
    }

    override fun stop() {
        playWhenReady = false
        if (isPrepared || isPreparing) {
            mediaPlayer.stop()
        }
        isPrepared = false
        isPreparing = false
        dispatchPlaybackState(BPlayerPlaybackState.IDLE)
        dispatchIsPlayingChanged(false)
    }

    override fun release() {
        detachFromContainer()
        renderView = null
        releaseSurface()
        mediaPlayer.release()
    }

    override fun seekTo(positionMs: Long) {
        if (isPrepared) {
            mediaPlayer.seekTo(positionMs.toInt())
        }
    }

    override fun setSpeed(speed: Float) {
        if (isPrepared || isPreparing) {
            mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
        }
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getDuration(): Long {
        return if (isPrepared) mediaPlayer.duration.toLong().coerceAtLeast(0L) else 0L
    }

    override fun getCurrentPosition(): Long {
        return if (isPrepared) mediaPlayer.currentPosition.toLong().coerceAtLeast(0L) else 0L
    }

    override fun setVolume(volume: Float) {
        val safeVolume = volume.coerceIn(0f, 1f)
        mediaPlayer.setVolume(safeVolume, safeVolume)
    }

    private fun createRenderView(context: Context): TextureView {
        return TextureView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surfaceTexture: SurfaceTexture,
                    width: Int,
                    height: Int
                ) {
                    bindSurfaceIfReady(surfaceTexture)
                }

                override fun onSurfaceTextureSizeChanged(
                    surfaceTexture: SurfaceTexture,
                    width: Int,
                    height: Int
                ) = Unit

                override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                    releaseSurface()
                    return true
                }

                override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit
            }
        }
    }

    private fun attachRenderView(container: FrameLayout, view: TextureView) {
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

    private fun bindSurfaceIfReady(surfaceTexture: SurfaceTexture?) {
        if (surfaceTexture == null) {
            return
        }
        releaseSurface()
        surface = Surface(surfaceTexture).also {
            mediaPlayer.setSurface(it)
        }
    }

    private fun releaseSurface() {
        mediaPlayer.setSurface(null)
        surface?.release()
        surface = null
    }
}
