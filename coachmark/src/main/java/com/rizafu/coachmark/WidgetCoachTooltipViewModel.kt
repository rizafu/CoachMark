package com.rizafu.coachmark

import android.databinding.BindingAdapter
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import java.util.ArrayList


/**
 * Created by RizaFu on 11/9/16.
 */

class WidgetCoachTooltipViewModel {
    val tooltipChild: ObservableArrayList<View> = ObservableArrayList()
    val backgroundColor: ObservableInt = ObservableInt()
    val matchWidth: ObservableBoolean = ObservableBoolean()

    fun isEmptyValue(): Boolean = tooltipChild.isEmpty()

    companion object {

        @BindingAdapter("tooltipBackground", "tooltipMatchWidth")
        @JvmStatic fun setBackground(layout: LinearLayout, color: Int?, matchWidth: Boolean? = false) {
            val params = LinearLayout.LayoutParams(if (matchWidth == true) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layout.layoutParams = params

            color?.let {
                if (matchWidth == true) {
                    if (it == 0 || it < 0) {
                        layout.setBackgroundColor(ContextCompat.getColor(layout.context, android.R.color.white))
                    } else {
                        layout.setBackgroundColor(ContextCompat.getColor(layout.context, it))
                    }
                } else {// for rounded corner
                    val drawable = ContextCompat.getDrawable(layout.context, R.drawable.shp_card)
                    if (it == 0 || it < 0) {
                        drawable?.setColorFilter(ContextCompat.getColor(layout.context, android.R.color.white), PorterDuff.Mode.MULTIPLY)
                    } else {
                        drawable?.setColorFilter(ContextCompat.getColor(layout.context, it), PorterDuff.Mode.MULTIPLY)
                    }
                    drawable?.let { layout.background = it }
                }
            }


            layout.invalidate()
        }

        @BindingAdapter("tooltipTint")
        @JvmStatic fun setTint(view: ImageView, color: Int?) {
            color?.let {
                if (it == 0 || it < 0) {
                    view.setColorFilter(ContextCompat.getColor(view.context, android.R.color.white))
                } else {
                    view.setColorFilter(ContextCompat.getColor(view.context, it))
                }
            }
            view.invalidate()
        }

        @BindingAdapter("tooltipChild")
        @JvmStatic fun setChild(layout: LinearLayout, views: ArrayList<View>?) {
            layout.removeAllViews()
            views?.let {
                if (it.isNotEmpty()) {
                    for (i in it.indices) {
                        val child = it[i]
                        child.tag = i
                        layout.addView(child)
                    }
                }
            }
            layout.invalidate()
        }
    }
}
