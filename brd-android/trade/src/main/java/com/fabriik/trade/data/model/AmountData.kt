package com.fabriik.trade.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class AmountData(
    val fiatAmount: BigDecimal,
    val fiatCurrency: String,
    val cryptoAmount: BigDecimal,
    val cryptoCurrency: String
): Parcelable