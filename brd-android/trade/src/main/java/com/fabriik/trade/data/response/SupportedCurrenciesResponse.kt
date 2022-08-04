package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupportedCurrenciesResponse(
    @Json(name = "supported_currencies")
    val currencies: List<String>
)