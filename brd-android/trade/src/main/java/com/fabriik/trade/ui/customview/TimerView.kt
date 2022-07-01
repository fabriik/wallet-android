package com.fabriik.trade.ui.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import com.fabriik.common.utils.dp
import com.fabriik.trade.R

class TimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val primaryColor = ContextCompat.getColor(context, R.color.light_primary)

    private var maxValue = 1
    private var currentValue = 1

    private val paddingFromStroke = 3.dp.toFloat()

    private val paintCircle = Paint().apply {
        isAntiAlias = true
        strokeWidth = 1.dp.toFloat()
        style = Paint.Style.STROKE
        color = primaryColor
    }

    private val paintProgress = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = primaryColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val sweepAngle = (360 / maxValue) * (maxValue - currentValue)

        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat() - 1.dp, paintCircle
        )

        val rect = RectF(paddingFromStroke, paddingFromStroke, width - paddingFromStroke, height - paddingFromStroke)
        canvas.drawArc(rect, 270f, sweepAngle.toFloat(), true, paintProgress)
    }

    fun setProgress(max: Int, current: Int) {
        maxValue = max
        currentValue = current
        invalidate()
    }
}