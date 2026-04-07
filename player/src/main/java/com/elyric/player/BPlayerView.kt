package com.elyric.player

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView as Media3PlayerView

class BPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val innerPlayerView = Media3PlayerView(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        useController = true
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

    fun getInnerView(): Media3PlayerView {
        return innerPlayerView
    }
}
