package com.fabriik.registration.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.setPadding
import com.fabriik.common.utils.dp
import com.fabriik.common.utils.sp

class EnterCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val pinLength = 6
    private val boxRadius = 12.dp.toFloat()
    private val strokeWidth = 1.dp.toFloat()
    private val marginBetweenBoxes = 8.dp.toFloat()

    private var boxes = arrayOf<RectF>()

    private val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 16.sp.toFloat()
    }

    private val paintBackground = Paint().apply {
        strokeWidth = this@EnterCodeView.strokeWidth
        color = Color.BLACK
        style = Paint.Style.STROKE
    }

    private val textBounds = Rect()

    init {
        setPadding(2.dp)
        isCursorVisible = false
        imeOptions = EditorInfo.IME_ACTION_DONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initBoxes()
    }

    private fun initBoxes() {
        val viewRect = Rect(
            paddingStart, paddingTop, measuredWidth - paddingEnd - paddingStart, measuredHeight - paddingBottom
        )

        val boxWidth = (viewRect.width() - marginBetweenBoxes * (pinLength - 1)) / pinLength

        boxes = IntRange(0, pinLength)
            .map {
                val startX = paddingStart + (boxWidth + marginBetweenBoxes) * it

                RectF(
                    startX,
                    viewRect.top.toFloat(),
                    startX + boxWidth,
                    viewRect.bottom.toFloat()
                )
            }
            .toTypedArray()
    }

    override fun onDraw(canvas: Canvas) {
        // draw boxes
        for (box in boxes) {
            canvas.drawRoundRect(
                box, boxRadius, boxRadius, paintBackground
            )
        }

        // draw text
        for (index in 0 until length()) {
            val letter = getPin()[index].toString()
            val boxBounds = boxes[index]

            paintText.getTextBounds(letter, 0, 1, textBounds)

            canvas.drawText(
                letter,
                0,
                1,
                boxBounds.centerX() - textBounds.width() / 2,
                boxBounds.centerY() + textBounds.height() / 2,
                paintText
            )
        }
    }

    fun getPin() = text.toString()
}