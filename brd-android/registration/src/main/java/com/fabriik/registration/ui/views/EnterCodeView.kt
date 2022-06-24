package com.fabriik.registration.ui.views

import android.content.Context
import android.graphics.*
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.fabriik.common.utils.dp
import com.fabriik.common.utils.sp
import com.fabriik.registration.R

class EnterCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val pinLength = 6
    private val boxRadius = 12.dp.toFloat()
    private val strokeWidth = 1.dp.toFloat()
    private val marginBetweenBoxes = 8.dp.toFloat()

    private val paintText = Paint().apply {
        color = ContextCompat.getColor(context, R.color.light_text_01)
        textSize = 16.sp.toFloat()
        typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
    }

    private val paintBackgroundDefault = Paint().apply {
        strokeWidth = this@EnterCodeView.strokeWidth
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.light_outline_01)
    }

    private val paintBackgroundError = Paint().apply {
        strokeWidth = this@EnterCodeView.strokeWidth
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.light_error)
    }

    private val textBounds = Rect()

    private var boxes = arrayOf<RectF>()
    private var showErrorState: Boolean = false

    init {
        setPadding(2.dp)
        setBackgroundResource(0)

        isCursorVisible = false
        imeOptions = EditorInfo.IME_ACTION_DONE
        filters = arrayOf(InputFilter.LengthFilter(pinLength))
        inputType = InputType.TYPE_CLASS_NUMBER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val availableWidth = measuredWidth - paddingEnd - paddingStart
        val boxSize = (availableWidth - marginBetweenBoxes * (pinLength - 1)) / pinLength
        val newHeight = boxSize + paddingTop + paddingBottom

        // change height to create square rectangles
        setMeasuredDimension(
            measuredWidth, newHeight.toInt()
        )

        initBoxes(boxSize)
    }

    private fun initBoxes(boxSize: Float) {
        boxes = IntRange(0, pinLength)
            .map {
                val startX = paddingStart + (boxSize + marginBetweenBoxes) * it

                RectF(
                    startX, paddingTop.toFloat(), startX + boxSize, paddingTop + boxSize
                )
            }
            .toTypedArray()
    }

    override fun onDraw(canvas: Canvas) {
        // draw boxes
        for (box in boxes) {
            canvas.drawRoundRect(
                box, boxRadius, boxRadius, if (showErrorState) paintBackgroundError else paintBackgroundDefault
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

    fun setErrorState(error: Boolean) {
        showErrorState = error
        invalidate()
    }
}