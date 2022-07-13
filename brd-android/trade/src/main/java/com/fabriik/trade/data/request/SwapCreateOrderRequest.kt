package com.fabriik.trade.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class SwapCreateOrderRequest(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "quantity")
    val quantity: BigDecimal,

    @Json(name = "destination")
    val destination: String,

    @Json(name = "destination_currency")
    val destinationCurrency: String
)