package com.fabriik.common.ui.dialog

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
class FabriikGenericDialogArgs(
    val requestKey: String,
    val title: CharSequence? = null,
    val positive: ButtonData? = null,
    val negative: ButtonData? = null,
    val description: CharSequence? = null
) : Parcelable {

    @Parcelize
    data class ButtonData(
        @DrawableRes val icon: Int? = null,
        val title: CharSequence,
        val resultKey: String,
    ) : Parcelable
}