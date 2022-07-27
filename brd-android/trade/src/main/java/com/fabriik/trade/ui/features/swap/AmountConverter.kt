package com.fabriik.trade.ui.features.swap

import com.breadwallet.breadbox.BreadBox
import com.breadwallet.repository.RatesRepository
import java.math.BigDecimal

class AmountConverter(
    private val breadBox: BreadBox,
    private val ratesRepository: RatesRepository,
    private val fiatCurrency: String
) {

    fun fiatToCrypto(amount: BigDecimal, cryptoCurrency: String): BigDecimal = ratesRepository.getCryptoForFiat(
        fiatAmount = amount,
        fiatCode = fiatCurrency,
        cryptoCode = cryptoCurrency
    )?: BigDecimal.ZERO

    fun cryptoToFiat(amount: BigDecimal, cryptoCurrency: String): BigDecimal = ratesRepository.getFiatForCrypto(
        fiatCode = fiatCurrency,
        cryptoCode = cryptoCurrency,
        cryptoAmount = amount
    )?: BigDecimal.ZERO
}