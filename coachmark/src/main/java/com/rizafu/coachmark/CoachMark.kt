package com.rizafu.coachmark

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Rect
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.IntDef
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.rizafu.coachmark.databinding.WidgetCoachTooltipBinding
import java.util.*


/**
 * Created by RizaFu on 11/7/16.
 */

class CoachMark private constructor(builder: Builder) {
    private val animDuration: Int
    private val activity: Activity
    private val container: FrameLayout

    @TooltipAlignment private var tooltipAlignment: Long = 0
    @PointerTooltipAlignment private var tooltipPointerAlignment: Long = 0
    private val overlayPadding: Int
    private val tooltipMargin: Int
    private val backgroundColorResource: Int
    private val radius: Int
    private val dismissible: Boolean
    private val isCircleMark: Boolean
    var isShow: Boolean = false
        private set
    private val backgroundAlpha: Float
    private val tooltipViewModel: WidgetCoachTooltipViewModel
    private val tooltipBinding: WidgetCoachTooltipBinding
    private lateinit var coachMarkOverlay: CoachMarkOverlay
    private val targetView: View?
    private var targetOnClick: View.OnClickListener? = null
    private val onDismissListener: (() -> Unit)?
    private val onAfterDismissListener: (() -> Unit)?
    private val tooltipShowAnimation: Animation?
    private val tooltipDismissAnimation: Animation?


    private val statusBarHeight: Int
        get() {
            var result = 0
            val context = activity
            val resources = context.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    private val screenHeight: Int
        get() {
            val result: Int
            val context = activity
            val resources = context.resources
            result = resources.displayMetrics.heightPixels
            return result
        }

    private val screenWidth: Int
        get() {
            val result: Int
            val context = activity
            val resources = context.resources
            result = resources.displayMetrics.widthPixels
            return result
        }

    @IntDef(ROOT_TOP, ROOT_CENTER, ROOT_BOTTOM, TARGET_TOP, TARGET_BOTTOM, TARGET_TOP_LEFT, TARGET_BOTTOM_LEFT, TARGET_TOP_RIGHT, TARGET_BOTTOM_RIGHT, TARGET_FILL_IN, TARGET_LEFT, TARGET_RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TooltipAlignment

    @IntDef(POINTER_RIGHT, POINTER_MIDDLE, POINTER_LEFT, POINTER_GONE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PointerTooltipAlignment

    init {
        this.activity = builder.activity
        this.container = FrameLayout(activity)
        this.tooltipViewModel = WidgetCoachTooltipViewModel()
        this.tooltipBinding = DataBindingUtil.inflate(activity.layoutInflater, R.layout.widget_coach_tooltip, container, false)
        this.tooltipBinding.viewModel = this.tooltipViewModel

        this.backgroundColorResource = builder.backgroundColor
        this.isCircleMark = builder.isCircleMark
        this.targetView = builder.target
        this.overlayPadding = ViewUtils.dpToPx(builder.markerPadding)
        this.dismissible = builder.dismissible
        this.tooltipAlignment = builder.tooltipAlignment
        this.tooltipPointerAlignment = builder.pointerTooltipAlignment
        this.tooltipMargin = ViewUtils.dpToPx(builder.tooltipMargin)
        this.onDismissListener = builder.onDismissListener
        this.onAfterDismissListener = builder.onAfterDismissListener
        this.tooltipShowAnimation = builder.tooltipShowAnimation
        this.tooltipDismissAnimation = builder.tooltipDismissAnimation
        this.radius = builder.radius
        this.backgroundAlpha = builder.backgroundAlpha

        tooltipViewModel.backgroundColorString.set(builder.tooltipBackgroundColorString)
        tooltipViewModel.backgroundColor.set(builder.tooltipBackgroundColor)
        tooltipViewModel.tooltipChild.addAll(builder.tooltipChilds)
        tooltipViewModel.matchWidth.set(builder.tooltipMatchWidth)

        val window = activity.window
        if (window != null) {
            val decorView = window.decorView as ViewGroup?
            decorView?.let {
                val content = it.findViewById<View>(android.R.id.content) as ViewGroup?
                content?.let{
                    val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    container.setBackgroundColor(Color.TRANSPARENT)

                    decorView.addView(container, layoutParams)
                    coachMarkOverlay = CoachMarkOverlay(activity)
                    coachMarkOverlay.setBackgroundResource(backgroundColorResource)
                    coachMarkOverlay.alpha = backgroundAlpha
                    container.addView(coachMarkOverlay, layoutParams)
                    container.addView(tooltipBinding.root)
                }
            }
        }
        animDuration = container.resources.getInteger(android.R.integer.config_longAnimTime)
        container.isClickable = true
        container.visibility = View.GONE
        container.alpha = 0f

        addTarget()
    }

    private fun setTargetOnClick(targetOnClick: View.OnClickListener) {
        this.targetOnClick = targetOnClick
    }

    private fun setTooltipAlignment(@TooltipAlignment tooltipAlignment: Long, @PointerTooltipAlignment pointerTooltipAlignment: Long) {
        this.tooltipAlignment = tooltipAlignment
        this.tooltipPointerAlignment = pointerTooltipAlignment
        tooltipBinding.root.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                relocationTooltip(targetView, tooltipAlignment)
                pointerTooltipAlignment(targetView, pointerTooltipAlignment)
                tooltipBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
    }

    private fun addTarget() {
        if (targetView == null) {
            setTooltipAlignment(tooltipAlignment, tooltipPointerAlignment)
        } else {
            targetView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (isCircleMark) {
                        addCircleRect(targetView)
                    } else {
                        addRoundRect(targetView)
                    }
                    setTooltipAlignment(tooltipAlignment, tooltipPointerAlignment)
                    targetView.viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
            })
        }
    }

    private fun addRoundRect(view: View) {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        val radius = this.radius

        val x = rect.left
        val y = rect.top
        val width = rect.width()
        val height = rect.height()
        addTargetClick(rect, view)
        coachMarkOverlay.setBackgroundResource(backgroundColorResource)
        coachMarkOverlay.alpha = backgroundAlpha
        coachMarkOverlay.addRect(x, y, width, height, radius, overlayPadding, isCircleMark)
        coachMarkOverlay.postInvalidate()
    }

    private fun addCircleRect(view: View) {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        val cx = rect.centerX()
        val cy = rect.centerY()

        val radius = (Math.max(rect.width(), rect.height()) / 2f * CIRCLE_ADDITIONAL_RADIUS_RATIO).toInt()
        addTargetClick(rect, view)
        coachMarkOverlay.setBackgroundResource(backgroundColorResource)
        coachMarkOverlay.alpha = backgroundAlpha
        coachMarkOverlay.addRect(cx, cy, 0, 0, radius, overlayPadding, isCircleMark)
        coachMarkOverlay.postInvalidate()
    }

    private fun relocationTooltip(view: View?, @TooltipAlignment alignment: Long) {
        val tooltipView = tooltipBinding.root

        val tooltipHeight = tooltipView.height
        val padding = overlayPadding + tooltipMargin
        val triangleHeight = if (tooltipPointerAlignment != POINTER_GONE) ViewUtils.dpToPx(12) else ViewUtils.dpToPx(0)
        val triangleWidth = if (tooltipPointerAlignment != POINTER_GONE) ViewUtils.dpToPx(12) else ViewUtils.dpToPx(0)

        if (view != null && !tooltipViewModel.isEmptyValue()) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)

            val height = rect.height()
            val width = rect.width()

            if (alignment == TARGET_FILL_IN) {
                val x = rect.left - (width - rect.width()) / 2
                val y = rect.top - (height - rect.height()) / 2
                tooltipView.layoutParams = FrameLayout.LayoutParams(width, height)
                tooltipView.y = y.toFloat()
                tooltipView.x = x.toFloat()
                tooltipBinding.tooltip.layoutParams = LinearLayout.LayoutParams(width, height)
                tooltipView.setOnClickListener(targetOnClick)
            }

            val y = rect.top
            var result: Float
            // setY
            if (alignment == TARGET_BOTTOM || alignment == TARGET_BOTTOM_LEFT || alignment == TARGET_BOTTOM_RIGHT) {
                result = (y + height + padding).toFloat()
                if (isCircleMark) {
                    result += (tooltipMargin * CIRCLE_ADDITIONAL_RADIUS_RATIO).toFloat()
                }
                tooltipView.y = result
            } else if (alignment == TARGET_TOP || alignment == TARGET_TOP_LEFT || alignment == TARGET_TOP_RIGHT) {
                result = (y - tooltipHeight - padding - triangleHeight).toFloat()
                if (isCircleMark) {
                    result -= (tooltipMargin * CIRCLE_ADDITIONAL_RADIUS_RATIO).toFloat()
                }
                tooltipView.y = result
            } else if (!tooltipViewModel.matchWidth.get() && alignment == TARGET_LEFT || alignment == TARGET_RIGHT){
                result = (y + (height/2) - (tooltipHeight/2)).toFloat()
                tooltipView.y = result
            }

            // setX
            if (!tooltipViewModel.matchWidth.get()) {
                val x = rect.left
                val centerXTarget = x + width / 2
                val rightXTarget = x + width
                val margin = ViewUtils.dpToPx(4)

                result = if (alignment == TARGET_BOTTOM_RIGHT || alignment == TARGET_TOP_RIGHT) {
                    (rightXTarget - tooltipBinding.tooltip.width).toFloat()
                } else if (alignment == TARGET_BOTTOM_LEFT || alignment == TARGET_TOP_LEFT) {
                    x.toFloat()
                } else if (alignment == TARGET_LEFT){
                    (x - tooltipBinding.tooltip.width - margin - triangleWidth).toFloat()
                } else if (alignment == TARGET_RIGHT){
                    (rightXTarget + margin - triangleWidth).toFloat()
                } else {
                    (centerXTarget - tooltipBinding.tooltip.width / 2).toFloat()
                }

                val rTooltip = result + tooltipBinding.tooltip.width
                if (rTooltip > screenWidth) {
                    result = result - (rTooltip - screenWidth) - margin
                }
                if (result <= 0) {
                    result = margin.toFloat()
                }

                if (tooltipAlignment == ROOT_CENTER || tooltipAlignment == ROOT_TOP || tooltipAlignment == ROOT_BOTTOM) {
                    result = (screenWidth / 2 - tooltipBinding.tooltip.width / 2).toFloat()
                    tooltipBinding.tooltip.x = result
                } else {
                    tooltipBinding.tooltip.x = result
                }
            }
        }

        when (alignment) {
            ROOT_TOP -> tooltipView.y = statusBarHeight.toFloat()
            ROOT_BOTTOM -> tooltipView.y = (screenHeight - tooltipHeight).toFloat()
            ROOT_CENTER -> tooltipView.y = (screenHeight / 2 - tooltipHeight / 2 + statusBarHeight / 2).toFloat()
        }
        tooltipView.postInvalidate()
    }

    private fun pointerTooltipAlignment(view: View?, @PointerTooltipAlignment pointerTooltipAlignment: Long) {
        if (view == null) return
        val rect = Rect()
        view.getGlobalVisibleRect(rect)

        val tooltipView = tooltipBinding.tooltip

        val x = rect.left
        val width = rect.width()
        val margin = overlayPadding + ViewUtils.dpToPx(16)
        val triangleWidth = ViewUtils.dpToPx(24)
        val tooltipWidth = tooltipView.width
        val tooltipHeight = tooltipView.height
        val tooltipX = tooltipView.x
        var result = 0

        if (pointerTooltipAlignment != POINTER_GONE){
            if (this.tooltipAlignment == TARGET_RIGHT || this.tooltipAlignment == TARGET_LEFT) {

                tooltipBinding.container.orientation = LinearLayout.HORIZONTAL

                val triangle = if (this.tooltipAlignment == TARGET_LEFT)tooltipBinding.triangleBottom else tooltipBinding.triangleTop
                triangle.visibility = View.VISIBLE
                triangle.y = (tooltipView.y + tooltipHeight/2 - ViewUtils.dpToPx(6))

                if (this.tooltipAlignment == TARGET_RIGHT) {
                    triangle.rotation = (-90).toFloat()
                    triangle.x = tooltipX - triangleWidth / 2 + margin + ViewUtils.dpToPx(2)
                } else {
                    triangle.rotation = (90).toFloat()
                    triangle.x = tooltipX - ViewUtils.dpToPx(7)
                }
            } else if (tooltipAlignment != ROOT_CENTER && tooltipAlignment != ROOT_BOTTOM && tooltipAlignment != ROOT_TOP) {
                when (pointerTooltipAlignment) {
                    POINTER_LEFT -> result = if (tooltipViewModel.matchWidth.get()) x + margin else (tooltipX + margin).toInt()
                    POINTER_MIDDLE -> result = if (tooltipViewModel.matchWidth.get()) x + width / 2 else (tooltipX + tooltipWidth / 2).toInt()
                    POINTER_RIGHT -> result = if (tooltipViewModel.matchWidth.get()) x + (width - margin) else (tooltipX + tooltipWidth - margin).toInt()
                }

                val triangle = if (this.tooltipAlignment == TARGET_TOP || this.tooltipAlignment == TARGET_TOP_LEFT || this.tooltipAlignment == TARGET_TOP_RIGHT) tooltipBinding.triangleBottom else tooltipBinding.triangleTop
                triangle.visibility = View.VISIBLE
                triangle.x = (result - triangleWidth / 2).toFloat()
            }
        } else {
            tooltipBinding.triangleBottom.visibility = View.GONE
            tooltipBinding.triangleTop.visibility = View.GONE
        }

        tooltipBinding.root.postInvalidate()
    }

    private fun addTargetClick(rect: Rect, view: View) {
        val clickableView = View(view.context)
        val width = rect.width()
        val height = rect.height()
        val x = rect.left - (width - rect.width()) / 2
        val y = rect.top - (height - rect.height()) / 2
        clickableView.layoutParams = ViewGroup.MarginLayoutParams(width, height)
        clickableView.translationY = y.toFloat()
        clickableView.translationX = x.toFloat()
        clickableView.setOnClickListener(targetOnClick)

        val attrs = intArrayOf(R.attr.selectableItemBackground)
        val typedArray = view.context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        clickableView.setBackgroundResource(backgroundResource)
        typedArray.recycle()

        container.addView(clickableView)
        container.invalidate()
    }

    private fun animateTooltipShow() {
        tooltipBinding.root.visibility = if (tooltipViewModel.isEmptyValue()) View.GONE else View.VISIBLE
        if (!tooltipViewModel.isEmptyValue() && tooltipShowAnimation != null) {
            tooltipBinding.root.startAnimation(tooltipShowAnimation)
        }
    }

    private fun animateTooltipDismiss() {
        if (!tooltipViewModel.isEmptyValue() && tooltipDismissAnimation != null) {
            tooltipDismissAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    if (tooltipBinding.root.visibility == View.VISIBLE) {
                        tooltipBinding.root.visibility = View.GONE
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            tooltipBinding.root.startAnimation(tooltipDismissAnimation)
        }
    }

    fun show(): CoachMark {
        container.visibility = View.VISIBLE
        animateTooltipShow()
        ViewCompat.animate(container)
                .alpha(1f)
                .setDuration(animDuration.toLong())
                .start()

        isShow = true
        container.setOnClickListener { if (dismissible) dismiss() }
        return this
    }

    @JvmOverloads
    fun dismiss(afterDismiss: (() -> Unit)? = null) {
        onDismissListener?.invoke()
        animateTooltipDismiss()
        ViewCompat.animate(container)
                .alpha(0f)
                .setDuration(animDuration.toLong())
                .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                    override fun onAnimationEnd(view: View?) {
                        super.onAnimationEnd(view)
                        if (container.alpha == 0f) {
                            val parent = view?.parent
                            (parent as? ViewGroup)?.removeView(view)
                            isShow = false
                            afterDismiss?.invoke()
                            onAfterDismissListener?.invoke()
                        }
                    }
                }).start()
    }

    class Builder
    /**
     * this constructor for initial default value
     * @param activity for parent view
     */
    (internal val activity: Activity) {
        internal var target: View? = null
        internal var tooltipChilds: ArrayList<View> =  ArrayList()
        internal var markerPadding: Int = 0
        internal var tooltipMatchWidth: Boolean = false
        internal var backgroundAlpha: Float = 0.5f
        internal var backgroundColor: Int = android.R.color.black
        internal var dismissible: Boolean = false
        internal var isCircleMark: Boolean = false
        internal var tooltipMargin: Int = 5
        internal var tooltipBackgroundColor: Int = 0
        internal var tooltipBackgroundColorString: String? = null
        internal var tooltipAlignment: Long = CoachMark.ROOT_BOTTOM
        internal var pointerTooltipAlignment: Long = CoachMark.POINTER_MIDDLE
        internal var radius: Int = 5
        internal var onDismissListener: (() -> Unit)? = null
        internal var onAfterDismissListener: (() -> Unit)? = null
        internal var tooltipShowAnimation: Animation? = null
        internal var tooltipDismissAnimation: Animation? = null

        private var onClickTarget: ((CoachMark) -> Unit)? = null

        private fun findViewById(@IdRes viewId: Int): View? {
            return activity.findViewById<View?>(viewId)
        }

        fun setTarget(target: View): Builder {
            this.target = target
            return this
        }

        fun setTarget(@IdRes itemViewId: Int): Builder {
            this.target = findViewById(itemViewId)
            return this
        }

        fun setCircleMark(): Builder {
            isCircleMark = true
            return this
        }

        fun setMarkerPadding(dp: Int): Builder {
            this.markerPadding = dp
            return this
        }

        fun setOnClickTarget(onClickTarget: (CoachMark) -> Unit): Builder {
            this.onClickTarget = onClickTarget
            return this
        }

        fun setDismissible(): Builder {
            this.dismissible = true
            return this
        }

        fun setTooltipAlignment(@TooltipAlignment tooltipAlignment: Long): Builder {
            this.tooltipAlignment = tooltipAlignment
            return this
        }

        fun setTooltipPointer(@PointerTooltipAlignment pointerTooltipAlignment: Long): Builder {
            this.pointerTooltipAlignment = pointerTooltipAlignment
            return this
        }


        fun setTooltipBackgroundColor(colorResource: Int): Builder {
            this.tooltipBackgroundColor = colorResource
            return this
        }

        fun setTooltipBackgroundColor(colorString: String): Builder {
            this.tooltipBackgroundColorString = colorString
            return this
        }

        fun setTooltipMatchWidth(): Builder {
            this.tooltipMatchWidth = true
            return this
        }

        fun setTooltipChilds(tooltipChilds: ArrayList<View>): Builder {
            this.tooltipChilds = tooltipChilds
            return this
        }

        fun setTooltipMargin(dp: Int): Builder {
            this.tooltipMargin = dp
            return this
        }

        fun addTooltipChild(tooltipChild: View): Builder {
            this.tooltipChilds.add(tooltipChild)
            return this
        }

        private fun createTooltipChildText(context: Context, message: String = ""): TextView{
            val padding = ViewUtils.dpToPx(8)
            val textView = TextView(context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.text = message
            textView.setPadding(padding, padding, padding, padding)
            return textView
        }

        fun addTooltipChildText(context: Context, message: String, @ColorRes textColor: Int): Builder {
            val textView = createTooltipChildText(context, message)
            textView.setTextColor(ContextCompat.getColor(context, textColor))
            return addTooltipChild(textView)
        }

        fun addTooltipChildText(context: Context, message: String, textColorString: String): Builder {
            val textView = createTooltipChildText(context,message)
            try {
                textView.setTextColor(Color.parseColor(textColorString))
            } catch (e: IllegalArgumentException){
                textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
            return addTooltipChild(textView)
        }

        fun setOnDismissListener(onDismiss: () -> Unit): Builder {
            this.onDismissListener = onDismiss
            return this
        }

        fun setOnAfterDismissListener(onAfterDismiss: () -> Unit): Builder {
            this.onAfterDismissListener = onAfterDismiss
            return this
        }

        fun setTooltipShowAnimation(tooltipShowAnimation: Animation): Builder {
            this.tooltipShowAnimation = tooltipShowAnimation
            return this
        }

        fun setTooltipDismissAnimation(tooltipDismissAnimation: Animation): Builder {
            this.tooltipDismissAnimation = tooltipDismissAnimation
            return this
        }

        fun setBackgroundAlpha(backgroundAlpha: Float): Builder {
            this.backgroundAlpha = backgroundAlpha
            return this
        }

        fun setBackgroundColor(@ColorRes backgroundColor: Int): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun setRadius(radius: Int): Builder {
            this.radius = radius
            return this
        }

        fun build(): CoachMark {
            val coachMark = CoachMark(this)
            coachMark.setTargetOnClick(View.OnClickListener {
                if (onClickTarget != null) {
                    onClickTarget?.invoke(coachMark)
                } else {
                    coachMark.dismiss()
                }
            })
            return coachMark
        }

        fun show(): CoachMark {
            return build().show()
        }
    }

    companion object {

        const val ROOT_TOP :Long = 1
        const val ROOT_BOTTOM :Long = 2
        const val ROOT_CENTER :Long = 3
        const val TARGET_TOP :Long = 4
        const val TARGET_BOTTOM :Long = 5
        const val TARGET_TOP_LEFT :Long = 6
        const val TARGET_BOTTOM_LEFT :Long = 7
        const val TARGET_TOP_RIGHT :Long = 8
        const val TARGET_BOTTOM_RIGHT :Long = 9
        const val TARGET_FILL_IN :Long = 10
        const val TARGET_LEFT :Long = 11
        const val TARGET_RIGHT :Long = 12

        const val POINTER_RIGHT :Long = 1
        const val POINTER_MIDDLE :Long = 2
        const val POINTER_LEFT :Long = 3
        const val POINTER_GONE :Long = 4

        internal const val CIRCLE_ADDITIONAL_RADIUS_RATIO = 1.5
    }
}
