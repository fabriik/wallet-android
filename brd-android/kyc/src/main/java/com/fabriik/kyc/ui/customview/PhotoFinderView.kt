package com.fabriik.kyc.ui.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.fabriik.common.utils.dp
import com.fabriik.kyc.R

class PhotoFinderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var framingRectangle: RectF? = null
    private val minOffset = 16.dp
    private val borderWidth = 3.dp.toFloat()
    private val borderRadius = 12.dp.toFloat()

    private lateinit var type: Type

    private val paintMask = Paint().apply {
        color = Color.parseColor("#80000000")
    }

    private val paintBorder = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.light_contrast_02)
        strokeCap = Paint.Cap.ROUND
        strokeWidth = borderWidth
    }

    private val path = Path().apply {
        fillType = Path.FillType.WINDING
    }

    init {
        parseAttributes(attrs)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val rect = framingRectangle
        if (rect != null && canvas != null) {
            when (type) {
                Type.SELFIE ->
                    drawSelfieMask(rect, canvas)
                Type.DOCUMENT ->
                    drawDocumentMask(rect, canvas)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateFramingRectangle()
    }

    fun setType(type: Type) {
        this.type = type
        invalidate()
    }

    fun getFramingRectangle() = framingRectangle

    private fun parseAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PhotoFinderView)
            setType(Type.values()[typedArray.getInt(R.styleable.PhotoFinderView_type, 1)])
            typedArray.recycle()
        }
    }

    private fun drawSelfieMask(rect: RectF, canvas: Canvas) {
        // draw mask
        path.reset()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        path.addCircle(rect.centerX(), rect.centerY(), rect.width() / 2, Path.Direction.CCW)
        canvas.drawPath(path, paintMask)

        // draw border
        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paintBorder)
    }

    private fun drawDocumentMask(rect: RectF, canvas: Canvas) {
        // draw mask
        path.reset()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        path.addRoundRect(rect, borderRadius, borderRadius, Path.Direction.CCW)
        canvas.drawPath(path, paintMask)

        // draw border
        canvas.drawRoundRect(rect, borderRadius, borderRadius, paintBorder)
    }

    @Synchronized
    private fun recalculateFramingRectangle() {
        val size = getSize(width, height)
        val topOffset = (height - size.y) / 2
        val leftOffset = (width - size.x) / 2

        framingRectangle = RectF(
            leftOffset, topOffset, leftOffset + size.x, topOffset + size.y
        )
    }

    private fun getSize(width: Int, height: Int): PointF {
        val size = PointF()
        size.x = width * WIDTH_PERCENT

        when (type) {
            Type.SELFIE -> {
                size.y = size.x

                if (size.x >= width) {
                    size.x = width - minOffset.toFloat()
                    size.y = size.x
                }

                if (size.y >= height) {
                    size.y = height - minOffset.toFloat()
                    size.x = size.y
                }

                return size
            }

            Type.DOCUMENT -> {
                size.y = size.x / WIDTH_HEIGHT_RATIO

                when {
                    size.x >= width ->
                        size.x = width - minOffset.toFloat()
                    size.y >= height ->
                        size.y = height - minOffset.toFloat()
                }

                return size
            }
        }
    }

    companion object {
        const val WIDTH_PERCENT = 0.9f
        const val WIDTH_HEIGHT_RATIO = 1.54f
    }

    enum class Type {
        SELFIE,
        DOCUMENT
    }
}