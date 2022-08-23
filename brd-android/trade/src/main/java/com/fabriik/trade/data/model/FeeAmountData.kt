package com.fabriik.trade.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class FeeAmountData(
    val fiatAmount: BigDecimal,
    val fiatCurrency: String,
    val cryptoAmount: BigDecimal,
    val cryptoCurrency: String,
    val isFeeInWalletCurrency: Boolean
) : Parcelable {

    fun cryptoAmountIfIncludedOrZero(): BigDecimal = if (isFeeInWalletCurrency) cryptoAmount else BigDecimal.ZERO
}