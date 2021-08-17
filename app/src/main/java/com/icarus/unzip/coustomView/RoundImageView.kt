package com.icarus.unzip.coustomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.icarus.unzip.R

class RoundImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val defaultRadius = 0
    private var radius: Float
    private var leftTopRadius: Float
    private var rightTopRadius: Float
    private var rightBottomRadius: Float
    private var leftBottomRadius: Float

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        radius =
            array.getDimensionPixelOffset(R.styleable.RoundImageView_radius, defaultRadius)
                .toFloat();
        leftTopRadius = array.getDimensionPixelOffset(
            R.styleable.RoundImageView_left_top_radius,
            defaultRadius
        ).toFloat()
        rightTopRadius = array.getDimensionPixelOffset(
            R.styleable.RoundImageView_right_top_radius,
            defaultRadius
        ).toFloat()
        rightBottomRadius = array.getDimensionPixelOffset(
            R.styleable.RoundImageView_right_bottom_radius,
            defaultRadius
        ).toFloat()
        leftBottomRadius = array.getDimensionPixelOffset(
            R.styleable.RoundImageView_left_bottom_radius,
            defaultRadius
        ).toFloat()
        array.recycle()
        if (leftTopRadius == 0F) {
            leftTopRadius = radius;
        }
        if (rightTopRadius == 0F) {
            rightTopRadius = radius;
        }
        if (rightBottomRadius == 0F) {
            rightBottomRadius = radius;
        }
        if (leftBottomRadius == 0F) {
            leftBottomRadius = radius;
        }
    }

    private val path = Path()
    override fun onDraw(canvas: Canvas?) {
        val maxLeft = leftTopRadius.coerceAtLeast(leftBottomRadius)
        val maxRight = rightTopRadius.coerceAtLeast(rightBottomRadius)
        val minWidth = maxLeft + maxRight
        val maxTop = leftTopRadius.coerceAtLeast(rightTopRadius)
        val maxBottom = leftBottomRadius.coerceAtLeast(rightBottomRadius)
        val minHeight = maxTop + maxBottom
        if (width >= minWidth && height > minHeight) {
            path.moveTo(leftTopRadius, 0F);
            path.lineTo(width - rightTopRadius, 0F);
            path.quadTo(width.toFloat(), 0F, width.toFloat(), rightTopRadius);
            path.lineTo(width.toFloat(), height - rightBottomRadius);
            path.quadTo(width.toFloat(), height.toFloat(), width - rightBottomRadius, height.toFloat());
            path.lineTo(leftBottomRadius, height.toFloat());
            path.quadTo(0F, height.toFloat(), 0F, height - leftBottomRadius);
            path.lineTo(0F, leftTopRadius);
            path.quadTo(0F, 0F, leftTopRadius, 0F);
            canvas?.clipPath(path);
        }
        super.onDraw(canvas);
    }
}