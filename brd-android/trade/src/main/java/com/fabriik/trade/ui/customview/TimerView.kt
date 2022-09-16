package com.fabriik.trade.ui.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.fabriik.common.utils.dp
import com.fabriik.trade.R

class TimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val timeFormat = "00:%02ds"
    private val animationViewSize = 20.dp

    private val textView = TextView(ContextThemeWrapper(context, R.style.TimerView_TextView))
    private val animationView = AnimationView(context)

    init {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL

        addView(textView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        addView(animationView, LayoutParams(animationViewSize, animationViewSize))
    }

    fun setProgress(max: Int, current: Int) {
        textView.text = timeFormat.format(current)
        animationView.setProgress(max, current)
    }

    private class AnimationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private var drawingRect = RectF()
        private val paintColor = ContextCompat.getColor(context, R.color.light_primary)
        private val marginFromBorder = 5.dp.toFloat()

        private val paintCircle = Paint().apply {
            isAntiAlias = true
            strokeWidth = 2.dp.toFloat()
            style = Paint.Style.STROKE
            color = paintColor
        }

        private val paintProgress = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = paintColor
        }

        private var sweepAngle: Float = 0f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // draw circle around progress view
            canvas.drawCircle(
                drawingRect.centerX(),
                drawingRect.centerY(),
                width / 2f - paintCircle.strokeWidth,
                paintCircle
            )

            // draw progress view
            canvas.drawArc(
                drawingRect, 270f, sweepAngle, true, paintProgress
            )
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            recalculateRectangle()
        }

        fun setProgress(max: Int, current: Int) {
            sweepAngle = (360f / max) * current
            invalidate()
        }

        private fun recalculateRectangle() {
            drawingRect = RectF(
                marginFromBorder,
                marginFromBorder,
                width - marginFromBorder,
                height - marginFromBorder
            )
        }
    }
}