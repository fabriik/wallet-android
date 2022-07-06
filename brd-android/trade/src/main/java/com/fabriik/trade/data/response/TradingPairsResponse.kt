package com.fabriik.trade.data.response

import com.fabriik.trade.data.model.TradingPair
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TradingPairsResponse(
    @Json(name = "trading_pairs")
    val pairs: List<TradingPair>
)