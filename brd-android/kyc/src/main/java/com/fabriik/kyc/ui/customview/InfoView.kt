package com.fabriik.kyc.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialInfoPopupBinding

class InfoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(ContextThemeWrapper(context, R.style.InfoView), attrs) {

    private var popupTitle: String? = null
    private var popupDescription: String? = null

    init {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_info_black, 0, 0, 0
        )
        parseAttributes(attrs)

        super.setOnClickListener {
            showPopupWindow()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        // disabled
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfoView)
        popupTitle = typedArray.getString(R.styleable.InfoView_popupTitle)
        popupDescription = typedArray.getString(R.styleable.InfoView_popupDescription)
        typedArray.recycle()
    }

    private fun showPopupWindow() {
        val parentView = rootView as ViewGroup
        val inflater = LayoutInflater.from(context)

        val binding = PartialInfoPopupBinding.inflate(inflater, parentView, false).apply {
            tvTitle.text = popupTitle
            tvDescription.text = popupDescription
        }

        val anchorViewLocation = IntArray(2).apply {
            getLocationOnScreen(this)
        }

        val width = (parentView.measuredWidth * 0.85f).toInt()
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val marginStart = resources.getDimensionPixelSize(
            R.dimen.margin_start_info_prompt
        )

        val popupWindow = PopupWindow(
            binding.root, width, height, true
        )

        binding.btnDismiss.setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(
            this, Gravity.TOP or Gravity.START, marginStart, anchorViewLocation[1]
        )
    }
}