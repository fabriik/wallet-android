package com.fabriik.common.data.model

import android.os.Parcelable
import com.fabriik.common.data.enums.KycStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
@JsonClass(generateAdapter = false)
data class ExchangeLimits(
    @Json(name = "allowance_lifetime")
    val allowanceLifetime: BigDecimal,

    @Json(name = "allowance_daily")
    val allowanceDaily: BigDecimal,

    @Json(name = "allowance_per_exchange")
    val allowancePerExchange: BigDecimal,

    @Json(name = "used_lifetime")
    val usedLifetime: BigDecimal,

    @Json(name = "used_daily")
    val usedDaily: BigDecimal,

    @Json(name = "next_exchange_limit")
    val nextExchangeLimit: BigDecimal
) : Parcelable {

    fun availableDaily() = allowanceDaily - usedDaily

    fun availableLifetime() = allowanceLifetime - usedLifetime
}