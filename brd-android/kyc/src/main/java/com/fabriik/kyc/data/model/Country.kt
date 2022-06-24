package com.fabriik.kyc.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "iso2")
    val code: String,

    @Json(name = "localizedName")
    val name: String
): Parcelable