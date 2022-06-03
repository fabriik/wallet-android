package com.fabriik.kyc.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Country(
    val code: String,
    val name: String
): Parcelable