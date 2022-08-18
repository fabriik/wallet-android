package com.fabriik.buy.data.response

import com.fabriik.buy.data.enums.PaymentStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentStatusResponse(
    @Json(name = "payment_reference")
    val paymentReference: String,

    @Json(name = "status")
    val status: PaymentStatus
)