package com.fabriik.buy.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PaymentInstrument(

    @Json(name = "id")
    val id: String,

    @Json(name = "fingerprint")
    val fingerprint: String,

    @Json(name = "expiryMonth")
    val expiryMonth: Int,

    @Json(name = "expiryYear")
    val expiryYear: Int,

    @Json(name = "scheme")
    val scheme: String,

    @Json(name = "last4")
    val last4Numbers: String
): Parcelable