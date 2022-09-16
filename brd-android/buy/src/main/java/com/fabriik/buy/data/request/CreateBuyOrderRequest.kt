package com.fabriik.buy.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CreateBuyOrderRequest(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "deposit_quantity")
    val depositQuantity: BigDecimal,

    @Json(name = "withdrawal_quantity")
    val withdrawQuantity: BigDecimal,

    @Json(name = "destination")
    val destination: String,

    @Json(name = "source_instrument_id")
    val sourceInstrumentId: String,

    @Json(name = "nolog_cvv")
    val nologCvv: String,
)
