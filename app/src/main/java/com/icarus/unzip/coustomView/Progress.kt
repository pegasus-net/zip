package com.icarus.unzip.coustomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.icarus.unzip.util.toPxF

class Progress(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val paint = Paint()
    var progress = 0
        set(value) {
            field = value
            postInvalidate()
        }

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas as Canvas
        paint.strokeWidth = 5.toPxF()
        paint.color = 0xFFF1F1F1.toInt()
        canvas.drawCircle(width / 2F, height / 2F, 55.toPxF(), paint)
        paint.strokeWidth = 1.5F
        paint.color = 0xFFD8D8D8.toInt()
        canvas.drawCircle(width / 2F, height / 2F, 57F.toPxF(), paint)

        paint.strokeWidth = 5.toPxF()
        paint.color = 0xFF027AFF.toInt()
        canvas.drawArc(
            width / 2F - 55.toPxF(),
            height / 2F - 55.toPxF(),
            width / 2F + 55.toPxF(),
            height / 2F + 55.toPxF(),
            -90F,
            0.36F * progress,
            false,
            paint
        )
    }
}