package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class ExchangeOrder(
    @Json(name = "orderid")
    val orderId: String,

    @Json(name = "status")
    val status: ExchangeOrderStatus,

    @Json(name = "source")
    val source: ExchangeSource,

    @Json(name = "destination")
    val destination: ExchangeSource,

    @Json(name = "timestamp")
    val timestamp: Long,
)

@JsonClass(generateAdapter = true)
data class ExchangeSource(
    @Json(name = "currency")
    val currency: String,

    @Json(name = "usdamount")
    val usdAmount: BigDecimal?,

    @Json(name = "currencyamount")
    val currencyAmount: BigDecimal,

    @Json(name = "transactionid")
    val transactionId: String?
)

enum class ExchangeOrderStatus {
    @Json(name = "PENDING")
    PENDING,

    @Json(name = "FAILED")
    FAILED,

    @Json(name = "COMPLETE")
    COMPLETE,

    @Json(name = "REFUNDED")
    REFUNDED,
}