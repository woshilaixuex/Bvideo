package com.elyric.common.data

import android.content.Context
import android.text.Spanned
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat

/**
 * @author Elyr1c
 */
object BTextUtils {
    fun htmlToSpanned(resourceString: String): Spanned {
        return HtmlCompat.fromHtml(resourceString, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun htmlResToSpanned(context: Context, @StringRes resId: Int): Spanned {
        return htmlToSpanned(context.getString(resId))
    }

    fun htmlToPlainText(resourceString: String): String {
        return htmlToSpanned(resourceString).toString()
    }
}
