package com.fabriik.trade.data.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "exchange_rate")
    val exchangeRate: BigDecimal,

    @Json(name = "timestamp")
    val timestamp: Long,

    @Json(name="minimum_value")
    val minimumValue: BigDecimal,

    @Json(name="buy_fees")
    val buyCardFeesPercent: Float?,

    @Json(name = "maximum_value")
    val maximumValue: BigDecimal,

    @Json(name="from_fee_currency")
    val fromFeeCurrency: FeeCurrency?,

    @Json(name="to_fee_currency")
    val toFeeCurrency: FeeCurrency?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class FeeCurrency(
    @Json(name = "fee_currency")
    val currency: String,

    @Json(name = "rate")
    val rate: BigDecimal
): Parcelable
