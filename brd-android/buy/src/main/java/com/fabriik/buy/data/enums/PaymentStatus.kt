package com.fabriik.buy.data.enums

import com.squareup.moshi.Json

enum class PaymentStatus {
    @Json(name = "Active")
    ACTIVE,

    @Json(name = "Requested")
    REQUESTED,

    @Json(name = "Pending")
    PENDING,

    @Json(name = "Authorized")
    AUTHORIZED,

    @Json(name = "Card Verified")
    CARD_VERIFIED,

    @Json(name = "Canceled")
    CANCELED,

    @Json(name = "Expired")
    EXPIRED,

    @Json(name = "Paid")
    PAID,

    @Json(name = "Declined")
    DECLINED,

    @Json(name = "Voided")
    VOIDED,

    @Json(name = "Partially Captured")
    PARTIALLY_CAPTURED,

    @Json(name = "Captured")
    CAPTURED,

    @Json(name = "Partially Refunded")
    PARTIALLY_REFUNDED,

    @Json(name = "Refunded")
    REFUNDED
}