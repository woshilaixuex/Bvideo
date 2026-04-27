package com.elyric.bredio.view.component.player

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.util.UnstableApi
import com.elyric.player.controller.BVideoPlayerController
import com.elyric.player.view.BPlayerView

@OptIn(UnstableApi::class)
class GlobalPlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val playerController = BVideoPlayerController(application)
    private var currentUrl: String? = null

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

    fun pause() {
        playerController.pause()
    }

    fun detach() {
        playerController.detach()
    }

    override fun onCleared() {
        playerController.release()
        super.onCleared()
    }
}
