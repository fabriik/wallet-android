package com.fabriik.common.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.fabriik.common.R
import com.google.android.material.snackbar.Snackbar

object FabriikToastUtil {

    fun showInfo(parentView: View, message: String) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            gravity = Gravity.BOTTOM,
            background = R.drawable.bg_info_prompt
        )
    }

    fun showRedInfo(parentView: View, message: String) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            gravity = Gravity.BOTTOM,
            background = R.drawable.bg_error_bubble
        )
    }

    fun showError(parentView: View, message: String) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            gravity = Gravity.TOP,
            background = R.drawable.bg_error_bubble
        )
    }

    private fun showCustomSnackBar(
        parentView: View,
        message: String,
        gravity: Int,
        @DrawableRes background: Int,
    ) {
        val view = TextView(parentView.context).apply {
            text = message
            setPadding(16.dp)
            setTextAppearance(R.style.FabriikToastTextAppearance)
        }

        val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG).apply {
            this.view.setBackgroundResource(background)
        }
        val height = getStatusBarHeight((parentView.context as Activity))

        // setup snackBar view
        (snackBar.view as Snackbar.SnackbarLayout).let {
            val params = it.layoutParams as ViewGroup.LayoutParams
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            if (params is CoordinatorLayout.LayoutParams) {
                params.gravity = gravity
                params.topMargin = height
            } else if (params is FrameLayout.LayoutParams) {
                params.gravity = gravity
                params.topMargin = height
            }

            it.layoutParams = params
            it.addView(view, 0)
        }

        snackBar.show()
    }

    private const val STATUS_BAR_NAME = "status_bar_height"
    private const val STATUS_BAR_TYPE = "dimen"
    private const val STATUS_BAR_PACKAGE = "android"

    private fun getStatusBarHeight(activity: Activity): Int {
        val resourceId =
            activity.resources.getIdentifier(STATUS_BAR_NAME, STATUS_BAR_TYPE, STATUS_BAR_PACKAGE)
        return if (resourceId > 0) activity.resources.getDimensionPixelSize(resourceId)
        else Rect().apply { activity.window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }
}