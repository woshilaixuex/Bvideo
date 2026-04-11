package com.elyric.player.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.elyric.player.R

@UnstableApi
class BPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val topControls = LayoutInflater.from(context).inflate(R.layout.view_player_top_controls, this, false)
    private val bottomControls = LayoutInflater.from(context).inflate(R.layout.view_player_bottom_controls, this, false)
    private val innerPlayerView = PlayerView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        useController = false
    }

    init {
        clipChildren = false
        clipToPadding = false

        addView(innerPlayerView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(topControls, buildOverlayLayoutParams(Gravity.TOP))
        addView(bottomControls, buildOverlayLayoutParams(Gravity.BOTTOM))
        hideControls()
    }

    fun bind(player: Player?) {
        innerPlayerView.player = player
    }

    fun unbind() {
        innerPlayerView.player = null
    }

    fun setUseController(useController: Boolean) {
        innerPlayerView.useController = useController
    }

    fun showControls() {
        topControls.visibility = View.VISIBLE
        bottomControls.visibility = View.VISIBLE
    }

    fun hideControls() {
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

    fun getInnerView(): PlayerView {
        return innerPlayerView
    }

    private fun buildOverlayLayoutParams(gravity: Int): LayoutParams {
        return LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            this.gravity = gravity
        }
    }
}
