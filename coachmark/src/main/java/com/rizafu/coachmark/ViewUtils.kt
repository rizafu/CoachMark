package com.rizafu.coachmark

import android.content.res.Resources
import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by RizaFu on 11/12/16.
 */

internal object ViewUtils {

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun doAfterLayout(view: View, listen: () -> Unit) {
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val viewTreeObserver = view.viewTreeObserver
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                listen.invoke()
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }
}
