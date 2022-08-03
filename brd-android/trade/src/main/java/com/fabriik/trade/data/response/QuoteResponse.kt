package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "exchange_rate")
    val exchangeRate: BigDecimal,

    @Json(name = "markup")
    val markup: BigDecimal,

    @Json(name = "timestamp")
    val timestamp: Long,

    @Json(name="minimum_value")
    val minimumValue: BigDecimal,

    @Json(name = "maximum_value")
    val maximumValue: BigDecimal,
)

@JsonClass(generateAdapter = true)
data class FeeEstimate(
    @Json(name = "estimated_confirmation_in")
    val estimatedConfirmationIn: Long,

    @Json(name = "tier")
    val tier: String,

    @Json(name = "fee")
    val fee: Amount
)

@JsonClass(generateAdapter = true)
data class Amount(
    @Json(name = "currency_id")
    val currencyId: String,

    @Json(name = "amount")
    val amount: String
)
