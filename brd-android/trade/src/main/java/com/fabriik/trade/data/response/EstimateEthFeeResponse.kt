package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class EstimateEthFeeResponse(
    @Json(name = "native_fee")
    val fee: BigDecimal,

    @Json(name = "currency")
    val currency: String
)