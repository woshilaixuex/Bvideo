package com.elyric.player

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

/**
 * 屏幕缩放状态
 */
enum class ScreenState{
    SCREEN_NORMAL,
    SCREEN_FULLSCREEN,
    SCREEN_TINY
}

/**
 * 核心生命周期
 */
enum class BPlayerLifecycle{
    IDLE,
    PREPARING,
    PLAYING,
    PAUSED,
    BUFFERING,
    COMPLETED,
    ERROR,
}
/**
 *  播放器核心,主要负责
 *  1. 手势控制
 *  2. 状态机与生命周期控制
 *  3. 可替换播放内核
 */
class BPlayerCore @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener, OnSeekBarChangeListener, OnTouchListener {
    override fun onClick(v: View?) {
    }

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {


        return true
    }
    companion object{
        const val TAG = "BPlayerCore"

    }
}
