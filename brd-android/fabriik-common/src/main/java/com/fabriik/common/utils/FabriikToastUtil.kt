package com.fabriik.common.utils

import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.setPadding
import com.fabriik.common.R
import com.google.android.material.snackbar.Snackbar

object FabriikToastUtil {

    fun showInfo(parentView: View, message: String) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            background = R.drawable.bg_info_prompt
        )
    }

    fun showError(parentView: View, message: String) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            background = R.drawable.bg_error_bubble
        )
    }

    private fun showCustomSnackBar(
        parentView: View,
        message: String,
        gravity: Int = Gravity.TOP,
        @DrawableRes background: Int
    ) {
        val view = TextView(parentView.context).apply {
            text = message
            setPadding(16.dp)
            setTextAppearance(R.style.FabriikToastTextAppearance)
        }

        val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG).apply {
            this.view.setBackgroundResource(background)
        }

        val topMargin = when {
            gravity == Gravity.TOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                parentView.rootWindowInsets?.displayCutout?.safeInsetTop ?: 0
            else -> 0
        }

        // setup snackBar view
        (snackBar.view as Snackbar.SnackbarLayout).let {
            val params = it.layoutParams as ViewGroup.LayoutParams
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            if (params is CoordinatorLayout.LayoutParams) {
                params.gravity = gravity
                params.topMargin = topMargin
            } else if (params is FrameLayout.LayoutParams) {
                params.gravity = gravity
                params.topMargin = topMargin
            }

            it.layoutParams = params
            it.addView(view, 0)
            snackBar.show()
        }
    }
}