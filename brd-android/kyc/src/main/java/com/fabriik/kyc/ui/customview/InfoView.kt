package com.fabriik.kyc.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialInfoViewBinding

class InfoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var popupTitle: String? = null
    private var popupDescription: String? = null

    init {
        parseAttributes(attrs)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        // disabled
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfoView)
        popupTitle = typedArray.getString(R.styleable.InfoView_infoTitle)
        popupDescription = typedArray.getString(R.styleable.InfoView_infoDescription)
        typedArray.recycle()
    }

    fun showPopupWindow() {
        val parentView = rootView as ViewGroup
        val inflater = LayoutInflater.from(context)

        val binding = PartialInfoViewBinding.inflate(inflater, parentView, false).apply {
            tvTitle.text = popupTitle
            tvDescription.text = popupDescription
        }

        val width = (parentView.measuredWidth * 0.85f).toInt()
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        val popupWindow = PopupWindow(
            binding.root, width, height, true
        )

        binding.btnDismiss.setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(
            this, Gravity.CENTER, 0, 0
        )
    }
}