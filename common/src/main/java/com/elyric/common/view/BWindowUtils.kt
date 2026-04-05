package com.elyric.common.view

import android.content.Context
import android.view.WindowManager

object BWindowUtils {
    /**
     * @return the window`s width and height
     */
    fun getScreenSize(context: Context): Pair<Int, Int> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Android 11 及以上
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = windowManager.currentWindowMetrics
            val bounds = metrics.bounds
            Pair(bounds.width(), bounds.height())
        } else {
            // Android 11 以下
            val displayMetrics = context.resources.displayMetrics
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }
}