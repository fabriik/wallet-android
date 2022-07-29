package com.fabriik.trade.ui.features.swap

import android.util.Log
import com.breadwallet.breadbox.BreadBox
import com.breadwallet.breadbox.estimateFee
import com.breadwallet.breadbox.isBitcoin
import com.breadwallet.breadbox.toBigDecimal
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.ext.isZero
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.trade.data.model.FeeAmountData
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.math.RoundingMode

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

    suspend fun convertSourceCryptoToDestinationCrypto(amount: BigDecimal, sourceCurrency: String, destinationCurrency: String, rate: BigDecimal, markup: BigDecimal): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val sourceFee = estimateFee(amount, sourceCurrency, fiatCurrency)
        val sourceAmount = if (sourceFee?.included == true) amount - sourceFee.cryptoAmount else amount

        val convertedAmount = sourceAmount.multiply(rate)

        val destFee = estimateFee(convertedAmount, destinationCurrency, fiatCurrency)
        val convertedDestAmount = if (destFee?.included == true) convertedAmount - destFee.cryptoAmount else convertedAmount
        val destAmount = convertedDestAmount.divide(markup, 20, RoundingMode.HALF_UP)

        return Triple(sourceFee, destFee, destAmount)
    }

    suspend fun convertDestinationCryptoToSourceCrypto(amount: BigDecimal, sourceCurrency: String, destinationCurrency: String, rate: BigDecimal, markup: BigDecimal): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val destFee = estimateFee(amount, destinationCurrency, fiatCurrency)
        val convertedDestAmount = if (destFee?.included == true) amount + destFee.cryptoAmount else amount
        val destAmount = convertedDestAmount.divide(markup, 20, RoundingMode.HALF_UP)

        val convertedAmount = destAmount.divide(rate, 20, RoundingMode.HALF_UP)

        val sourceFee = estimateFee(convertedAmount, sourceCurrency, fiatCurrency)
        val sourceAmount = if (sourceFee?.included == true) convertedAmount + sourceFee.cryptoAmount else convertedAmount

        return Triple(sourceFee, destFee, sourceAmount)
    }

    private suspend fun estimateFee(
        cryptoAmount: BigDecimal, currencyCode: String, fiatCode: String
    ): FeeAmountData? {
        if (cryptoAmount.isZero()) {
            return null
        }

        val wallet = breadBox.wallet(currencyCode).first()
        val amount = Amount.create(cryptoAmount.toDouble(), wallet.unit)
        val address = loadAddress(wallet.currency.code) ?: return null

        return try {
            val data = wallet.estimateFee(address, amount)
            val cryptoFee = data.fee.toBigDecimal()
            val cryptoCurrency = data.currency.code
            val fiatFee = ratesRepository.getFiatForCrypto(
                cryptoAmount = cryptoFee,
                cryptoCode = cryptoCurrency,
                fiatCode = fiatCode
            ) ?: return null

            return FeeAmountData(
                fiatAmount = fiatFee,
                fiatCurrency = fiatCode,
                cryptoAmount = cryptoFee,
                cryptoCurrency = cryptoCurrency,
                included = currencyCode.equals(cryptoCurrency, true)
            )
        } catch (e: Exception) {
            Log.d("AmountConverter", "Fee estimation failed: ${e.message}")
            null
        }
    }

    private suspend fun loadAddress(currencyCode: String): Address? {
        val wallet = breadBox.wallets()
            .first()
            .find {
                it.currency.code.equals(currencyCode, ignoreCase = true)
            } ?: return null

        return if (wallet.currency.isBitcoin()) {
            wallet.getTargetForScheme(
                when (BRSharedPrefs.getIsSegwitEnabled()) {
                    true -> AddressScheme.BTC_SEGWIT
                    false -> AddressScheme.BTC_LEGACY
                }
            )
        } else {
            wallet.target
        }
    }
}