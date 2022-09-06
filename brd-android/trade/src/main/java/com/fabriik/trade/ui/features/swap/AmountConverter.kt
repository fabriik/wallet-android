package com.fabriik.trade.ui.features.swap

import com.breadwallet.repository.RatesRepository
import com.breadwallet.util.isErc20
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.utils.EstimateReceivingFee
import com.fabriik.trade.utils.EstimateSendingFee
import java.math.BigDecimal
import java.math.RoundingMode

class AmountConverter(
    private val ratesRepository: RatesRepository,
    private val estimateSendingFee: EstimateSendingFee,
    private val estimateReceivingSwapFee: EstimateReceivingFee,
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

    fun convertSourceCryptoToDestinationCrypto(
        amount: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String,
        quoteResponse: QuoteResponse
    ): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val receivingFeeRate = requireNotNull(quoteResponse.toFeeCurrency).rate

        val sourceFee = estimateSendingFee(quoteResponse, amount, sourceCurrency, fiatCurrency)
        val sourceAmount =
            if (sourceFee?.isFeeInWalletCurrency == true) amount - sourceFee.cryptoAmount else amount

        val convertedAmount = sourceAmount.multiply(quoteResponse.exchangeRate)

        val destFee = estimateReceivingSwapFee(quoteResponse, convertedAmount, destinationCurrency, fiatCurrency)

        return when {
            // subtract receiving fee from amount if it should be included into calculations (bsv, btc, eth, ...)
            destFee?.isFeeInWalletCurrency == true ->
                Triple(sourceFee, destFee, convertedAmount - destFee.cryptoAmount)

            // convert ETH fee to erc20 fee and subtract from amount
            destFee != null && destinationCurrency.isErc20() -> {
                val erc20CurrencyFee = destFee.cryptoAmount.divide(receivingFeeRate, 20, RoundingMode.HALF_UP)
                Triple(sourceFee, destFee.copy(isFeeInWalletCurrency = true), convertedAmount - erc20CurrencyFee)
            }

            // otherwise return values
            else ->
                Triple(sourceFee, destFee, convertedAmount)
        }
    }

    fun convertDestinationCryptoToSourceCrypto(
        amount: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String,
        quoteResponse: QuoteResponse
    ): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val receivingFeeRate = requireNotNull(quoteResponse.toFeeCurrency).rate

        val destFee = estimateReceivingSwapFee(quoteResponse, amount, destinationCurrency, fiatCurrency)
        val destAmount = when {
            // add receiving fee to amount if it should be included into calculations (bsv, btc, eth, ...)
            destFee?.isFeeInWalletCurrency == true ->
                amount + destFee.cryptoAmount

            // convert ETH fee to erc20 fee and add to amount
            destFee != null && destinationCurrency.isErc20() -> {
                val erc20CurrencyFee = destFee.cryptoAmount.divide(receivingFeeRate, 20, RoundingMode.HALF_UP)
                amount + erc20CurrencyFee
            }

            // otherwise set entered amount
            else -> amount
        }

        val convertedAmount = destAmount.divide(quoteResponse.exchangeRate, 20, RoundingMode.HALF_UP)

        val sourceFee = estimateSendingFee(quoteResponse, convertedAmount, sourceCurrency, fiatCurrency)
        val sourceAmount =
            if (sourceFee?.isFeeInWalletCurrency == true) convertedAmount + sourceFee.cryptoAmount else convertedAmount

        return Triple(sourceFee, destFee, sourceAmount)
    }
}