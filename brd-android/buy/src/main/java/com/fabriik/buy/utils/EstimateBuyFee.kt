package com.fabriik.buy.utils

import com.breadwallet.ext.isZero
import com.breadwallet.repository.RatesRepository
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal

class EstimateBuyFee(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(quote: QuoteResponse?, amount: BigDecimal, walletCurrency: String, fiatCode: String) : FeeAmountData? {
        if (amount.isZero() || quote == null) {
            return null
        }

        val receivingFee = requireNotNull(quote.toFee)
        val receivingFeeCurrency = requireNotNull(quote.toFeeCurrency).currency

        val fiatFee = ratesRepository.getFiatForCrypto(
            cryptoAmount = receivingFee,
            cryptoCode = receivingFeeCurrency,
            fiatCode = fiatCode
        ) ?: return null

        return FeeAmountData(
            fiatAmount = fiatFee,
            fiatCurrency = fiatCode,
            cryptoAmount = receivingFee,
            cryptoCurrency = receivingFeeCurrency,
            isFeeInWalletCurrency = walletCurrency.equals(receivingFeeCurrency, true)
        )
    }
}