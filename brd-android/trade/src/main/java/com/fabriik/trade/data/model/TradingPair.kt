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

    @Json(name = "minimum_quantity")
    val minAmount: BigDecimal,

    @Json(name = "maximum_quantity")
    val maxAmount: BigDecimal,

    @Json(name = "name")
    val name: String
): Parcelable {

    fun invertCurrencies(): TradingPair {
        return TradingPair(
            baseCurrency = termCurrency,
            termCurrency = baseCurrency,
            minAmount = minAmount,
            maxAmount = maxAmount,
            name = name
        )
    }
}