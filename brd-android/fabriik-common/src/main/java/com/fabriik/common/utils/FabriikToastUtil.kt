package com.fabriik.common.utils

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.fabriik.common.R
import com.google.android.material.snackbar.Snackbar

object FabriikToastUtil {

    fun show(parentView: View, message: String) {
        val view = TextView(parentView.context)
        view.text = message
        view.setPadding(16.dp)
        view.setTextAppearance(R.style.FabriikToastTextAppearance)

        val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundResource(R.drawable.bg_info_prompt)

        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        val parentParams = snackBarView.layoutParams as FrameLayout.LayoutParams
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        snackBarView.layoutParams = parentParams

        snackBarView.addView(view, 0)
        snackBar.show()
    }
}