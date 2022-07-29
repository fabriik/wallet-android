package com.fabriik.trade.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class EstimateEthFeeRequest(
    @Json(name = "amount")
    val amount: BigDecimal,

    @Json(name = "currency")
    val currency: String,

    @Json(name = "destination_address")
    val destination: String
)
