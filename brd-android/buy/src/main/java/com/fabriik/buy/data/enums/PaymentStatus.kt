package com.fabriik.buy.data.enums

import com.squareup.moshi.Json

enum class PaymentStatus {
    @Json(name = "ACTIVE")
    ACTIVE,

    @Json(name = "REQUESTED")
    REQUESTED,

    @Json(name = "PENDING")
    PENDING,

    @Json(name = "AUTHORIZED")
    AUTHORIZED,

    @Json(name = "CARD_VERIFIED")
    CARD_VERIFIED,

    @Json(name = "CANCELED")
    CANCELED,

    @Json(name = "EXPIRED")
    EXPIRED,

    @Json(name = "PAID")
    PAID,

    @Json(name = "DECLINED")
    DECLINED,

    @Json(name = "VOIDED")
    VOIDED,

    @Json(name = "PARTIALLY_CAPTURED")
    PARTIALLY_CAPTURED,

    @Json(name = "CAPTURED")
    CAPTURED,

    @Json(name = "PARTIALLY_REFUNDED")
    PARTIALLY_REFUNDED,

    @Json(name = "REFUNDED")
    REFUNDED
}