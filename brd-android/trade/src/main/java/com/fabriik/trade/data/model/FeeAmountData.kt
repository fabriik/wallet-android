package com.fabriik.trade.data.model

import android.os.Parcelable
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.isEthereum
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
    fun cryptoAmountIfIncludedOrNull(): BigDecimal? = if (isFeeInWalletCurrency) cryptoAmount else null

    fun formatFiatForUi() = fiatAmount.formatFiatForUi(
        currencyCode = fiatCurrency,
        showCurrencyName = true
    )

    fun isEthereum() = cryptoCurrency.isEthereum()
}