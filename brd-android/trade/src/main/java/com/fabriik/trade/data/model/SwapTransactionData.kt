package com.fabriik.trade.data.model

import android.os.Parcelable
import com.fabriik.trade.data.response.ExchangeOrderStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@JsonClass(generateAdapter = true)
data class SwapTransactionData(

    @Json(name = "transaction_id")
    val transactionId: String,

    @Json(name = "exchange_id")
    val exchangeId: String,

    @Json(name = "status")
    val status: ExchangeOrderStatus
): Parcelable