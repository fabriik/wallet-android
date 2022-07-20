package com.fabriik.trade.ui.features.swap

import java.math.BigDecimal

class ConvertSourceFiatAmount(private val converter: AmountConverter) {
    suspend operator fun invoke(sourceFiatAmount: BigDecimal, exchangeRate: BigDecimal, sourceCryptoCurrency: String, destinationCryptoCurrency: String): SwapInputContract.Amounts {
        val sourceCryptoAmount = converter.fiatToCrypto(sourceFiatAmount, sourceCryptoCurrency)
        val destinationCryptoAmountData = converter.sourceCryptoToDestinationCrypto(sourceCryptoAmount, exchangeRate, sourceCryptoCurrency, destinationCryptoCurrency)
        val destinationCryptoAmount = destinationCryptoAmountData.first
        val destinationFiatAmount = converter.cryptoToFiat(destinationCryptoAmount, destinationCryptoCurrency)

        return SwapInputContract.Amounts(
            sourceFeeData = destinationCryptoAmountData.second,
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFeeData = destinationCryptoAmountData.third,
            destinationFiatAmount = destinationFiatAmount,
            destinationCryptoAmount = destinationCryptoAmount,
        )
    }
}

class ConvertSourceCryptoAmount(private val converter: AmountConverter) {
    suspend operator fun invoke(sourceCryptoAmount: BigDecimal, exchangeRate: BigDecimal, sourceCryptoCurrency: String, destinationCryptoCurrency: String): SwapInputContract.Amounts {
        val sourceFiatAmount = converter.cryptoToFiat(sourceCryptoAmount, sourceCryptoCurrency)
        val destinationCryptoAmountData = converter.sourceCryptoToDestinationCrypto(sourceCryptoAmount, exchangeRate, sourceCryptoCurrency, destinationCryptoCurrency)
        val destinationCryptoAmount = destinationCryptoAmountData.first
        val destinationFiatAmount = converter.cryptoToFiat(destinationCryptoAmount, destinationCryptoCurrency)

        return SwapInputContract.Amounts(
            sourceFeeData = destinationCryptoAmountData.second,
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFeeData = destinationCryptoAmountData.third,
            destinationFiatAmount = destinationFiatAmount,
            destinationCryptoAmount = destinationCryptoAmount
        )
    }
}

class ConvertDestinationFiatAmount(private val converter: AmountConverter) {
    suspend operator fun invoke(destinationFiatAmount: BigDecimal, exchangeRate: BigDecimal, sourceCryptoCurrency: String, destinationCryptoCurrency: String): SwapInputContract.Amounts {
        val destinationCryptoAmount = converter.fiatToCrypto(destinationFiatAmount, destinationCryptoCurrency)
        val sourceCryptoAmountData = converter.destinationCryptoToSourceCrypto(destinationCryptoAmount, exchangeRate, destinationCryptoCurrency, sourceCryptoCurrency)
        val sourceCryptoAmount = sourceCryptoAmountData.first
        val sourceFiatAmount = converter.cryptoToFiat(sourceCryptoAmount, sourceCryptoCurrency)

        return SwapInputContract.Amounts(
            sourceFeeData = sourceCryptoAmountData.second,
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFeeData = sourceCryptoAmountData.third,
            destinationFiatAmount = destinationFiatAmount,
            destinationCryptoAmount = destinationCryptoAmount
        )
    }
}

class ConvertDestinationCryptoAmount(private val converter: AmountConverter) {
    suspend operator fun invoke(destinationCryptoAmount: BigDecimal, exchangeRate: BigDecimal, sourceCryptoCurrency: String, destinationCryptoCurrency: String): SwapInputContract.Amounts {
        val destinationFiatAmount = converter.cryptoToFiat(destinationCryptoAmount, destinationCryptoCurrency)
        val sourceCryptoAmountData = converter.destinationCryptoToSourceCrypto(destinationCryptoAmount, exchangeRate, destinationCryptoCurrency, sourceCryptoCurrency)
        val sourceCryptoAmount = sourceCryptoAmountData.first
        val sourceFiatAmount = converter.cryptoToFiat(sourceCryptoAmount, sourceCryptoCurrency)

        return SwapInputContract.Amounts(
            sourceFeeData = sourceCryptoAmountData.second,
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFeeData = sourceCryptoAmountData.third,
            destinationFiatAmount = destinationFiatAmount,
            destinationCryptoAmount = destinationCryptoAmount
        )
    }
}