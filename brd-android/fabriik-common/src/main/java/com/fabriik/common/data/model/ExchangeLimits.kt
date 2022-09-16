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
    @Json(name = "swap_allowance_lifetime")
    val swapAllowanceLifetime: BigDecimal?,

    @Json(name = "swap_allowance_daily")
    val swapAllowanceDaily: BigDecimal?,

    @Json(name = "swap_allowance_per_exchange")
    val swapAllowancePerExchange: BigDecimal?,

    @Json(name = "used_swap_lifetime")
    val swapUsedLifetime: BigDecimal?,

    @Json(name = "used_swap_daily")
    val swapUsedDaily: BigDecimal?,
) : Parcelable