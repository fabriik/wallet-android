package com.fabriik.trade.ui.features.swap

import com.breadwallet.repository.RatesRepository
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.utils.EstimateSwapFee
import java.math.BigDecimal
import java.math.RoundingMode

class AmountConverter(
    private val ratesRepository: RatesRepository,
    private val estimateFee: EstimateSwapFee,
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

    suspend fun convertSourceCryptoToDestinationCrypto(
        amount: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String,
        rate: BigDecimal,
        markup: BigDecimal,
        sendingFeeRate: BigDecimal,
        receivingFeeRate: BigDecimal
    ): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val sourceFee = estimateFee(amount, sourceCurrency, fiatCurrency)
        val sourceAmount =
            if (sourceFee?.included == false) amount - sourceFee.cryptoAmount.divide(sendingFeeRate,20, RoundingMode.HALF_UP) else amount

        val convertedAmount = sourceAmount.multiply(rate)

        val destFee = estimateFee(convertedAmount, destinationCurrency, fiatCurrency)

        val convertedDestAmount =
            if (destFee?.included == false) {
                convertedAmount - destFee.cryptoAmount.divide(receivingFeeRate, 20, RoundingMode.HALF_UP)
            } else convertedAmount
        val destAmount = convertedDestAmount.divide(markup, 20, RoundingMode.HALF_UP)
        return Triple(sourceFee, destFee, destAmount)
    }

    suspend fun convertDestinationCryptoToSourceCrypto(
        amount: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String,
        rate: BigDecimal,
        markup: BigDecimal,
        sendingFeeRate: BigDecimal,
        receivingFeeRate: BigDecimal
    ): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val destFee = estimateFee(amount, destinationCurrency, fiatCurrency)
        val convertedDestAmount =
            if (destFee?.included == false) amount + destFee.cryptoAmount.divide(receivingFeeRate, 20, RoundingMode.HALF_UP) else amount
        val destAmount = convertedDestAmount.multiply(markup)

        val convertedAmount = destAmount.divide(rate, 20, RoundingMode.HALF_UP)

        val sourceFee = estimateFee(convertedAmount, sourceCurrency, fiatCurrency)
        val sourceAmount =
            if (sourceFee?.included == false) convertedAmount + sourceFee.cryptoAmount.divide(sendingFeeRate) else convertedAmount

        return Triple(sourceFee, destFee, sourceAmount)
    }
}