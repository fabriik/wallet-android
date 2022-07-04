package com.fabriik.trade.data.response

import com.fabriik.trade.data.model.SupportedTradingPair
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupportedTradingPairsResponse(
    @Json(name = "trading_pairs")
    val pairs: List<SupportedTradingPair>
)