package com.fabriik.trade.utils

import com.breadwallet.ext.isZero
import com.breadwallet.repository.RatesRepository
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.ui.features.swap.SwapInputContract
import java.math.BigDecimal
import java.math.RoundingMode

class SwapAmountCalculator(private val ratesRepository: RatesRepository) {

    fun convertFiatToCrypto(fiatAmount: BigDecimal, cryptoCode: String, fiatCode: String): BigDecimal {
        if (fiatAmount.isZero()) {
            return BigDecimal.ZERO
        }

        return ratesRepository.getCryptoForFiat(
            fiatAmount = fiatAmount,
            cryptoCode = cryptoCode,
            fiatCode = fiatCode
        ) ?: BigDecimal.ZERO
    }

    fun convertCryptoToFiat(cryptoAmount: BigDecimal, cryptoCode: String, fiatCode: String): BigDecimal {
        if (cryptoAmount.isZero()) {
            return BigDecimal.ZERO
        }

        return ratesRepository.getFiatForCrypto(
            cryptoAmount = cryptoAmount,
            cryptoCode = cryptoCode,
            fiatCode = fiatCode
        ) ?: BigDecimal.ZERO
    }

    fun convertCryptoToCrypto(cryptoAmount: BigDecimal, tradingPair: TradingPair, quoteState: SwapInputContract.QuoteState.Loaded, fromCryptoCode: String): BigDecimal {
        if (cryptoAmount.isZero()) {
            return BigDecimal.ZERO
        }

        return if (tradingPair.baseCurrency == fromCryptoCode) {
            cryptoAmount.multiply(quoteState.buyRate) //todo: minus sendingFee, minus receivingFee
        } else {
            cryptoAmount.divide(quoteState.sellRate, 5, RoundingMode.HALF_UP) //todo: plus sendingFee, plus receivingFee
        }
    }
}