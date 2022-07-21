package com.fabriik.trade.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "base_quantity")
    val baseQuantity: BigDecimal,

    @Json(name = "term_quantity")
    val termQuantity: BigDecimal,

    @Json(name = "trade_side")
    val tradeSide: TradeSide,

    @Json(name = "destination")
    val destination: String,
) {
    enum class TradeSide {
        @Json(name = "buy")
        BUY,

        @Json(name = "sell")
        SELL
    }
}
