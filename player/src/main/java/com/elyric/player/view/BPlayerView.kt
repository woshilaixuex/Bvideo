package com.elyric.player.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@UnstableApi
class BPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val innerPlayerView = PlayerView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        useController = false
    }

    init {
        addView(innerPlayerView)
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

    fun getInnerView(): PlayerView {
        return innerPlayerView
    }
}
