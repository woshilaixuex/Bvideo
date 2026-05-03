package com.elyric.bredio.view.component.player

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.util.UnstableApi
import com.elyric.player.controller.BVideoPlayerController
import com.elyric.player.engine.BPlayerListener
import com.elyric.player.engine.BPlayerPlaybackState
import com.elyric.player.data.BPlayerCacheStore
import com.elyric.player.data.BPlayerPreloadState
import com.elyric.player.data.BPlayerPreloadTask
import com.elyric.player.view.BPlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(UnstableApi::class)
class GlobalPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val playerController = BVideoPlayerController(application)
    private var currentUrl: String? = null
    private var currentPreloadUrl: String? = null
    private var currentPreloadTask: BPlayerPreloadTask? = null
    private val _playbackState = MutableStateFlow(BPlayerPlaybackState.IDLE)
    val playbackState: StateFlow<BPlayerPlaybackState> = _playbackState.asStateFlow()
    val preloadEvents: SharedFlow<BPlayerPreloadState> = BPlayerCacheStore.preloadEvents
    private val playerListener = object : BPlayerListener {
        override fun onPlaybackStateChanged(state: BPlayerPlaybackState) {
            _playbackState.value = state
        }

        override fun onPlayerError(message: String?) {
            _playbackState.value = BPlayerPlaybackState.ERROR
        }
    }

    init {
        playerController.addListener(playerListener)
    }

    fun attach(playerView: BPlayerView) {
        playerController.attach(playerView)
    }

    fun play(playerView: BPlayerView, url: String) {
        playerController.attach(playerView)
        if (currentUrl == url) {
            if (!playerController.isPlaying()) {
                playerController.start()
            }
            return
        }
        currentUrl = url
        playerController.play(url)
    }

    fun preload(url: String, seconds: Int = DEFAULT_PRELOAD_SECONDS) {
        if (currentPreloadUrl == url) {
            return
        }
        currentPreloadTask?.cancel()
        currentPreloadUrl = url
        currentPreloadTask = BPlayerCacheStore.preloadSeconds(
            context = getApplication(),
            url = url,
            seconds = seconds
        )
    }

    fun pause() {
        playerController.pause()
    }

    fun detach() {
        playerController.detach()
    }

    override fun onCleared() {
        currentPreloadTask?.cancel()
        playerController.removeListener(playerListener)
        playerController.release()
        super.onCleared()
    }

    private companion object {
        const val DEFAULT_PRELOAD_SECONDS = 5
    }
}
