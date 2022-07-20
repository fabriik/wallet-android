package com.fabriik.trade.ui.features.swap

import android.util.Log
import com.breadwallet.breadbox.BreadBox
import com.breadwallet.breadbox.estimateFee
import com.breadwallet.breadbox.isBitcoin
import com.breadwallet.breadbox.toBigDecimal
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import kotlinx.coroutines.flow.first
import java.lang.IllegalStateException
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

    suspend fun sourceCryptoToDestinationCrypto(amount: BigDecimal, exchangeRate: BigDecimal, fromCryptoCurrency: String, toCryptoCurrency: String): Triple<BigDecimal, SwapInputContract.FeeData, SwapInputContract.FeeData> {
        val sourceFee = estimateFee(amount, fromCryptoCurrency) ?: throw IllegalStateException("Source fee not returned")
        val destAmount = amount.multiply(exchangeRate - sourceFee.cryptoConvertedFeeAmount)
        val destFee = estimateFee(destAmount, toCryptoCurrency) ?: throw IllegalStateException("Destination fee not returned")
        return Triple(destAmount - destFee.cryptoConvertedFeeAmount, sourceFee, destFee)
    }

    suspend fun destinationCryptoToSourceCrypto(amount: BigDecimal, exchangeRate: BigDecimal, fromCryptoCurrency: String, toCryptoCurrency: String): Triple<BigDecimal, SwapInputContract.FeeData, SwapInputContract.FeeData> {
        val destFee = estimateFee(amount, fromCryptoCurrency) ?: throw IllegalStateException("Destination fee not returned")
        val sourceAmount = amount.divide(exchangeRate, 20, RoundingMode.HALF_UP)
        val sourceFee = estimateFee(sourceAmount, toCryptoCurrency) ?: throw IllegalStateException("Source fee not returned")
        return Triple(sourceAmount + sourceFee.cryptoConvertedFeeAmount + destFee.cryptoConvertedFeeAmount, sourceFee, destFee)
    }

    private suspend fun estimateFee(value: BigDecimal, cryptoCurrency: String) : SwapInputContract.FeeData? {
        if (value == BigDecimal.ZERO) {
            return SwapInputContract.FeeData(
                fiatFeeAmount = BigDecimal.ZERO,
                fiatFeeCurrency = fiatCurrency,
                cryptoOriginalFeeAmount = BigDecimal.ZERO,
                cryptoOriginalFeeCurrency = cryptoCurrency,
                cryptoConvertedFeeAmount = BigDecimal.ZERO,
                cryptoConvertedFeeCurrency = cryptoCurrency
            )
        }

        val wallet = breadBox.wallet(cryptoCurrency).first()
        val amount = Amount.create(value.toDouble(), wallet.unit)
        val address = loadAddress(wallet.currency.code) ?: return null

        return try {
            val data = wallet.estimateFee(address, amount)
            val cryptoFee = data.fee.toBigDecimal()
            val cryptoFeeCurrency = data.currency.code
            val fiatFee = cryptoToFiat(cryptoFee, cryptoFeeCurrency)
            val convertedCrypto = when {
                cryptoFeeCurrency != cryptoCurrency -> fiatToCrypto(fiatFee, cryptoCurrency)
                else -> cryptoFee
            }

            return SwapInputContract.FeeData(
                fiatFeeAmount = fiatFee,
                fiatFeeCurrency = fiatCurrency,
                cryptoOriginalFeeAmount = cryptoFee,
                cryptoOriginalFeeCurrency = cryptoFeeCurrency,
                cryptoConvertedFeeAmount = convertedCrypto,
                cryptoConvertedFeeCurrency = cryptoCurrency
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