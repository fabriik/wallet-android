package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "security_id")
    val securityId: String,

    @Json(name = "exchange_rate")
    val exchangeRate: BigDecimal,

    @Json(name = "buy_markup")
    val buyMarkupFactor: BigDecimal,

    @Json(name = "sell_markup")
    val sellMarkupFactor: BigDecimal,

    @Json(name = "minimum_usd_value")
    val minUsdValue: BigDecimal,

    @Json(name = "timestamp")
    val timestamp: Long
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