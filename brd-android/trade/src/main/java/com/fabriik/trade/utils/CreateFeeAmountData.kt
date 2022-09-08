package com.fabriik.trade.utils

import com.breadwallet.repository.RatesRepository
import com.fabriik.trade.data.model.FeeAmountData
import java.math.BigDecimal

class CreateFeeAmountData(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(fee: BigDecimal, feeCurrency: String, walletCurrency: String, fiatCode: String) : FeeAmountData? {
        val fiatFee = ratesRepository.getFiatForCrypto(
            cryptoAmount = fee,
            cryptoCode = feeCurrency,
            fiatCode = fiatCode
        ) ?: return null

        return FeeAmountData(
            fiatAmount = fiatFee,
            fiatCurrency = fiatCode,
            cryptoAmount = fee,
            cryptoCurrency = feeCurrency,
            isFeeInWalletCurrency = walletCurrency.equals(feeCurrency, true)
        )
    }
}