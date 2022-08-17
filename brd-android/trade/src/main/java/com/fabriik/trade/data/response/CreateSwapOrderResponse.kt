package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CreateSwapOrderResponse(
    @Json(name = "exchange_id")
    val exchangeId: String,

    @Json(name = "amount")
    val amount: BigDecimal,

    @Json(name = "currency")
    val currency: String,

    @Json(name = "address")
    val address: String
)