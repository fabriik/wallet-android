package com.fabriik.common.utils

import android.content.Context
import androidx.annotation.DrawableRes
import com.fabriik.common.R
import java.util.Locale

object FlagUtil {

    @DrawableRes
    fun getDrawableId(context: Context, countryCode: String): Int {
        val drawableName = "ic_flag_${countryCode.toLowerCase(Locale.ROOT)}"
        val resourceId = context.resources.getIdentifier(
            drawableName, "drawable", context.packageName
        )

        return if (resourceId == 0) {
            R.drawable.ic_flag_default
        } else {
            resourceId
        }
    }
}