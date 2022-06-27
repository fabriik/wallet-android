package com.fabriik.common.utils

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.setPadding
import com.fabriik.common.R
import com.google.android.material.snackbar.Snackbar

object FabriikToastUtil {

    fun showInfo(parentView: View, message: String) {
        val view = TextView(parentView.context)
        view.text = message
        view.setPadding(16.dp)
        view.setTextAppearance(R.style.FabriikToastTextAppearance)

        val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundResource(R.drawable.bg_info_prompt)

        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        val parentParams = snackBarView.layoutParams as ViewGroup.LayoutParams
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        snackBarView.layoutParams = parentParams

        snackBarView.addView(view, 0)
        snackBar.show()
    }

    fun showError(parentView: View, message: String) {
        val view = TextView(parentView.context)
        view.text = message
        view.setPadding(16.dp)
        view.setTextAppearance(R.style.FabriikToastTextAppearance)

        val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundResource(R.drawable.bg_error_bubble)

        val snackBarView = snackBar.view as Snackbar.SnackbarLayout
        val parentParams = snackBarView.layoutParams as ViewGroup.LayoutParams
        parentParams.width = FrameLayout.LayoutParams.MATCH_PARENT

        if (parentParams is CoordinatorLayout.LayoutParams) {
            parentParams.gravity = Gravity.TOP
        } else if (parentParams is FrameLayout.LayoutParams) {
            parentParams.gravity = Gravity.TOP
        }

        snackBarView.layoutParams = parentParams
        snackBarView.addView(view, 0)
        snackBar.show()
    }
}