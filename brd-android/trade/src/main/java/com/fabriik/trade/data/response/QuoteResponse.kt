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

    @Json(name = "close_ask")
    val closeAsk: BigDecimal,

    @Json(name = "close_bid")
    val closeBid: BigDecimal,

    @Json(name = "timestamp")
    val timestamp: Long,

    @Json(name = "out_fee_estimates")
    val outFeeEstimates: List<FeeEstimate>,

    @Json(name = "in_fee_estimates")
    val inFeeEstimates: List<FeeEstimate>
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