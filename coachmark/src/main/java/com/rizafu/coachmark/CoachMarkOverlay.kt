package com.rizafu.coachmark

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by RizaFu on 11/7/16.
 */

class CoachMarkOverlay : View {

    private lateinit var paint: Paint
    private var rectF: RectF? = null
    private var radius: Int = 0
    private var x: Int = 0
    private var y: Int = 0

    private var isCircle: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        isDrawingCacheEnabled = true

        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun addRect(x: Int, y: Int, width: Int, height: Int, radius: Int, padding: Int, isCircle: Boolean) {
        this.isCircle = isCircle
        this.radius = radius + padding
        this.x = x
        this.y = y
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        val r = x + width
        val b = y + height

        rectF = RectF((x - padding).toFloat(), (y - padding).toFloat(), (r + padding).toFloat(), (b + padding).toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isCircle) {
            canvas.drawCircle(x.toFloat(), y.toFloat(), radius.toFloat(), paint)
        } else {
            rectF?.let { canvas.drawRoundRect(it, radius.toFloat(), radius.toFloat(), paint) }
        }
    }
}
