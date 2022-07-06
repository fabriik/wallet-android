package com.fabriik.trade.utils

import com.breadwallet.repository.RatesRepository
import java.math.BigDecimal

class SwapAmountCalculator(private val ratesRepository: RatesRepository) {

    fun convertFiatToCrypto(fiatAmount: BigDecimal, cryptoCode: String, fiatCode: String): BigDecimal {
        TODO("Not yet implemented")
    }

    fun convertCryptoToFiat(cryptoAmount: BigDecimal, cryptoCode: String, fiatCode: String): BigDecimal {
        TODO("Not yet implemented")
    }

    fun convertCryptoToCrypto(cryptoAmount: BigDecimal, fromCryptoCode: String, toCryptoCode: String): BigDecimal {
        TODO("Not yet implemented")
    }
}