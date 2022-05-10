package com.fabriik.kyc.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.StringRes
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialInfoPopupBinding

object InfoPopup {

    fun showPopupWindow(anchorView: View, parentView: ViewGroup, @StringRes title: Int, @StringRes description: Int) {
        val inflater = LayoutInflater.from(anchorView.context)
        val binding = PartialInfoPopupBinding.inflate(inflater, parentView, false).apply {
            tvTitle.setText(title)
            tvDescription.setText(description)
        }

        val width = (parentView.measuredWidth * 0.85f).toInt()
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val marginStart = anchorView.resources.getDimensionPixelSize(
            R.dimen.margin_start_info_prompt
        )

        val popupWindow = PopupWindow(
            binding.root, width, height, true
        )

        binding.ivDismiss.setOnClickListener {
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView, marginStart, 0)
    }
}