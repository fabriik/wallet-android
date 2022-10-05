package com.fabriik.common.ui.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
class FabriikGenericDialogArgs(
    val requestKey: String,
    val iconRes: Int? = null,
    val title: CharSequence? = null,
    val titleRes: Int? = null,
    val titleTextGravity: Int = Gravity.START,
    val positive: ButtonData? = null,
    val negative: ButtonData? = null,
    val description: CharSequence? = null,
    val descriptionRes: Int? = null,
    val showDismissButton: Boolean = false,
    val messageArgs: List<String>? = null,
    val extraData: Bundle? = null,
) : Parcelable {

    @Parcelize
    data class ButtonData(
        @DrawableRes val icon: Int? = null,
        val title: CharSequence? = null,
        @StringRes val titleRes: Int? = null,
        val resultKey: String,
    ) : Parcelable
}