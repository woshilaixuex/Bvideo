package com.elyric.player.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.media3.common.util.UnstableApi
import com.elyric.player.R
import com.elyric.player.controller.BVideoPlayerController
import com.elyric.player.engine.BPlayerListener
import com.elyric.player.engine.BPlayerPlaybackState

@UnstableApi
class BPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val renderContainer = FrameLayout(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }
    private val topControls = LayoutInflater.from(context).inflate(R.layout.view_player_top_controls, this, false)
    private val bottomControls = LayoutInflater.from(context).inflate(R.layout.view_player_bottom_controls, this, false)
    private val backButton: ImageView = topControls.findViewById(R.id.ivBack)
    private val moreButton: ImageView = topControls.findViewById(R.id.ivMore)
    private val playButton: ImageView = bottomControls.findViewById(R.id.ivPlay)
    private val currentTimeView: TextView = bottomControls.findViewById(R.id.tvCurrent)
    private val totalTimeView: TextView = bottomControls.findViewById(R.id.tvTotal)
    private val seekBar: SeekBar = bottomControls.findViewById(R.id.seekBar)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var controller: BVideoPlayerController? = null
    private var controlsVisible = false
    private var isSeeking = false
    private var onBackClickListener: OnClickListener? = null
    private var onMoreClickListener: OnClickListener? = null
    private val autoHideRunnable = Runnable {
        hideControls()
    }
    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            if (controller != null) {
                mainHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }
    private val playerListener = object : BPlayerListener {
        override fun onPlaybackStateChanged(state: BPlayerPlaybackState) {
            updatePlayButton()
            updateProgress()
            when (state) {
                BPlayerPlaybackState.READY -> {
                    if (controller?.isPlaying() == true) {
                        scheduleAutoHide()
                    }
                }

                BPlayerPlaybackState.ENDED,
                BPlayerPlaybackState.ERROR -> {
                    showControls()
                    cancelAutoHide()
                }

                else -> Unit
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayButton()
            if (isPlaying) {
                startProgressUpdates()
                scheduleAutoHide()
            } else {
                stopProgressUpdates()
                cancelAutoHide()
            }
        }
    }

    init {
        clipChildren = false
        clipToPadding = false

        addView(renderContainer, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(topControls, buildOverlayLayoutParams(Gravity.TOP))
        addView(bottomControls, buildOverlayLayoutParams(Gravity.BOTTOM))
        hideControls()
        bindControlEvents()
    }

    fun getRenderContainer(): FrameLayout {
        return renderContainer
    }

    fun attachController(controller: BVideoPlayerController) {
        if (this.controller === controller) {
            updatePlayButton()
            updateProgress()
            if (controller.isPlaying()) {
                startProgressUpdates()
            }
            return
        }
        detachController()
        this.controller = controller
        controller.addListener(playerListener)
        updatePlayButton()
        updateProgress()
        startProgressUpdates()
    }

    fun detachController() {
        controller?.removeListener(playerListener)
        controller = null
        stopProgressUpdates()
        cancelAutoHide()
    }

    fun showControls() {
        controlsVisible = true
        topControls.visibility = View.VISIBLE
        bottomControls.visibility = View.VISIBLE
    }

    fun hideControls() {
        controlsVisible = false
        topControls.visibility = View.GONE
        bottomControls.visibility = View.GONE
    }

    fun toggleControls() {
        if (topControls.visibility == View.VISIBLE || bottomControls.visibility == View.VISIBLE) {
            hideControls()
        } else {
            showControls()
        }
    }

    fun getTopControls(): View {
        return topControls
    }

    fun getBottomControls(): View {
        return bottomControls
    }

    fun setOnBackClickListener(listener: OnClickListener?) {
        onBackClickListener = listener
    }

    fun setOnMoreClickListener(listener: OnClickListener?) {
        onMoreClickListener = listener
    }

    override fun onDetachedFromWindow() {
        controller?.onPlayerViewDetached(this)
        stopProgressUpdates()
        cancelAutoHide()
        super.onDetachedFromWindow()
    }

    private fun buildOverlayLayoutParams(gravity: Int): LayoutParams {
        return LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            this.gravity = gravity
        }
    }

    private fun bindControlEvents() {
        setOnClickListener {
            toggleControls()
            if (controlsVisible && controller?.isPlaying() == true) {
                scheduleAutoHide()
            }
        }

        playButton.setOnClickListener {
            controller?.togglePlayPause()
            showControls()
            if (controller?.isPlaying() == true) {
                scheduleAutoHide()
            }
        }

        backButton.setOnClickListener { view ->
            onBackClickListener?.onClick(view)
        }

        moreButton.setOnClickListener { view ->
            onMoreClickListener?.onClick(view)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                currentTimeView.text = formatTime(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
                cancelAutoHide()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 0
                controller?.seekTo(progress.toLong())
                isSeeking = false
                updateProgress()
                if (controller?.isPlaying() == true) {
                    scheduleAutoHide()
                }
            }
        })
    }

    private fun updatePlayButton() {
        val iconRes = if (controller?.isPlaying() == true) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
        playButton.setImageResource(iconRes)
    }

    private fun updateProgress() {
        val controller = controller ?: return
        val duration = controller.getDuration()
        val position = controller.getCurrentPosition()
        totalTimeView.text = formatTime(duration)
        if (!isSeeking) {
            currentTimeView.text = formatTime(position)
            seekBar.max = duration.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            seekBar.progress = position.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        mainHandler.post(progressRunnable)
    }

    private fun stopProgressUpdates() {
        mainHandler.removeCallbacks(progressRunnable)
    }

    private fun scheduleAutoHide() {
        cancelAutoHide()
        mainHandler.postDelayed(autoHideRunnable, AUTO_HIDE_DELAY_MS)
    }

    private fun cancelAutoHide() {
        mainHandler.removeCallbacks(autoHideRunnable)
    }

    private fun formatTime(timeMs: Long): String {
        val totalSeconds = (timeMs.coerceAtLeast(0L) / 1000L).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private companion object {
        const val AUTO_HIDE_DELAY_MS = 3_000L
        const val PROGRESS_UPDATE_INTERVAL_MS = 500L
    }
}
