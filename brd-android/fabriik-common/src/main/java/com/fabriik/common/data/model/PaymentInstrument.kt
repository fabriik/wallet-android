package com.fabriik.common.data.model

import android.os.Parcelable
import com.fabriik.common.R
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

    @Json(name = "expiry_month")
    val expiryMonth: Int,

    @Json(name = "expiry_year")
    val expiryYear: Int,

    @Json(name = "scheme")
    val scheme: String,

    @Json(name = "last4")
    val last4Numbers: String
): Parcelable {

    val cardTypeIcon: Int
        get() = when {
            scheme.equals("visa", true) -> R.drawable.ic_visa
            scheme.equals("mastercard", true) -> R.drawable.ic_mastercard
            else -> R.drawable.ic_credit_card
        }

    val hiddenCardNumber: String
        get() = "**** **** **** $last4Numbers"

    val expiryDate: String
        get() = "$expiryMonth/${expiryYear % 100}"
}