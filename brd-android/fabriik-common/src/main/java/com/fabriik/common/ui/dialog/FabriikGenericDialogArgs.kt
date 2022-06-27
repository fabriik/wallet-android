package com.fabriik.common.ui.dialog

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
class FabriikGenericDialogArgs(
    val requestKey: String,
    val title: CharSequence? = null,
    val titleRes: Int? = null,
    val positive: ButtonData? = null,
    val negative: ButtonData? = null,
    val description: CharSequence? = null,
    val descriptionRes: Int? = null
) : Parcelable {

    @Parcelize
    data class ButtonData(
        @DrawableRes val icon: Int? = null,
        val title: CharSequence? = null,
        @StringRes val titleRes: Int? = null,
        val resultKey: String,
    ) : Parcelable
}