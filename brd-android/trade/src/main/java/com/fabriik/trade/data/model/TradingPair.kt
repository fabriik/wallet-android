package com.fabriik.trade.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@JsonClass(generateAdapter = true)
data class TradingPair(

    @Json(name = "base_currency")
    val baseCurrency: String,

    @Json(name = "term_currency")
    val termCurrency: String,

    @Json(name = "name")
    val name: String
): Parcelable