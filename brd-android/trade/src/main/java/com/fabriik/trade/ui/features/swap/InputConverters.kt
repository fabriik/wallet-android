package com.fabriik.trade.ui.features.swap

import com.fabriik.trade.data.model.FeeAmountData
import java.math.BigDecimal

interface InputConverter {
    suspend operator fun invoke(
        amount: BigDecimal,
        changeByUser: Boolean,
        exchangeRate: BigDecimal,
        markupFactor: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String,
    ): Result

    data class Result(
        val sourceFiatAmount: BigDecimal,
        val sourceCryptoAmount: BigDecimal,
        val sourceNetworkFee: FeeAmountData?,
        val destinationFiatAmount: BigDecimal,
        val destinationCryptoAmount: BigDecimal,
        val destinationNetworkFee: FeeAmountData?,
        val sourceFiatAmountChangedByUser: Boolean = false,
        val sourceCryptoAmountChangedByUser: Boolean = false,
        val destinationFiatAmountChangedByUser: Boolean = false,
        val destinationCryptoAmountChangedByUser: Boolean = false
    )
}

class ConvertSourceFiatAmount(private val amountConverter: AmountConverter) : InputConverter {
    override suspend fun invoke(
        amount: BigDecimal,
        changeByUser: Boolean,
        exchangeRate: BigDecimal,
        markupFactor: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String
    ): InputConverter.Result {
        // convert sending fiat to sending crypto
        val sourceCryptoAmount = amountConverter.fiatToCrypto(
            amount = amount,
            cryptoCurrency = sourceCurrency
        )

        // convert sending crypto to receiving crypto
        val destCryptoAmountData = amountConverter.convertSourceCryptoToDestinationCrypto(
            rate = exchangeRate,
            markup = markupFactor,
            amount = sourceCryptoAmount,
            sourceCurrency = sourceCurrency,
            destinationCurrency = destinationCurrency
        )

        val destCryptoAmount = destCryptoAmountData.third

        // convert receiving crypto to receiving fiat
        val destFiatAmount = amountConverter.cryptoToFiat(
            amount = destCryptoAmount,
            cryptoCurrency = destinationCurrency
        )

        return InputConverter.Result(
            sourceFiatAmount = amount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFiatAmount = destFiatAmount,
            destinationCryptoAmount = destCryptoAmount,
            sourceNetworkFee = destCryptoAmountData.first,
            destinationNetworkFee = destCryptoAmountData.second,
            sourceFiatAmountChangedByUser = changeByUser
        )
    }
}

class ConvertSourceCryptoAmount(private val amountConverter: AmountConverter) : InputConverter {
    override suspend fun invoke(
        amount: BigDecimal,
        changeByUser: Boolean,
        exchangeRate: BigDecimal,
        markupFactor: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String
    ): InputConverter.Result {
        // convert sending crypto to sending fiat
        val sourceFiatAmount = amountConverter.cryptoToFiat(
            amount = amount,
            cryptoCurrency = sourceCurrency
        )

        // convert sending crypto to receiving crypto
        val destCryptoAmountData = amountConverter.convertSourceCryptoToDestinationCrypto(
            rate = exchangeRate,
            markup = markupFactor,
            amount = amount,
            sourceCurrency = sourceCurrency,
            destinationCurrency = destinationCurrency
        )

        val destCryptoAmount = destCryptoAmountData.third

        // convert receiving crypto to receiving fiat
        val destFiatAmount = amountConverter.cryptoToFiat(
            amount = destCryptoAmount,
            cryptoCurrency = destinationCurrency
        )

        return InputConverter.Result(
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = amount,
            destinationFiatAmount = destFiatAmount,
            destinationCryptoAmount = destCryptoAmount,
            sourceNetworkFee = destCryptoAmountData.first,
            destinationNetworkFee = destCryptoAmountData.second,
            sourceCryptoAmountChangedByUser = changeByUser
        )
    }
}

class ConvertDestinationFiatAmount(private val amountConverter: AmountConverter) : InputConverter {
    override suspend fun invoke(
        amount: BigDecimal,
        changeByUser: Boolean,
        exchangeRate: BigDecimal,
        markupFactor: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String
    ): InputConverter.Result {
        // convert receiving fiat to receiving crypto
        val destCryptoAmount = amountConverter.fiatToCrypto(
            amount = amount,
            cryptoCurrency = destinationCurrency
        )

        // convert receiving crypto to sending crypto
        val sourceCryptoAmountData = amountConverter.convertDestinationCryptoToSourceCrypto(
            amount = destCryptoAmount,
            destinationCurrency = destinationCurrency,
            sourceCurrency = sourceCurrency,
            rate = exchangeRate,
            markup = markupFactor
        )

        val sourceCryptoAmount = sourceCryptoAmountData.third

        // convert sending crypto to sending fiat
        val sourceFiatAmount = amountConverter.cryptoToFiat(
            amount = sourceCryptoAmount,
            cryptoCurrency = sourceCurrency
        )

        return InputConverter.Result(
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFiatAmount = amount,
            destinationCryptoAmount = destCryptoAmount,
            sourceNetworkFee = sourceCryptoAmountData.first,
            destinationNetworkFee = sourceCryptoAmountData.second,
            destinationFiatAmountChangedByUser = changeByUser
        )
    }
}

class ConvertDestinationCryptoAmount(private val amountConverter: AmountConverter) :
    InputConverter {
    override suspend fun invoke(
        amount: BigDecimal,
        changeByUser: Boolean,
        exchangeRate: BigDecimal,
        markupFactor: BigDecimal,
        sourceCurrency: String,
        destinationCurrency: String
    ): InputConverter.Result {
        // convert receiving crypto to receiving fiat
        val destFiatAmount = amountConverter.cryptoToFiat(
            amount = amount,
            cryptoCurrency = destinationCurrency
        )

        // convert receiving crypto to sending crypto
        val sourceCryptoAmountData = amountConverter.convertDestinationCryptoToSourceCrypto(
            rate = exchangeRate,
            amount = amount,
            markup = markupFactor,
            sourceCurrency = sourceCurrency,
            destinationCurrency = destinationCurrency
        )

        val sourceCryptoAmount = sourceCryptoAmountData.third

        // convert sending crypto to sending fiat
        val sourceFiatAmount = amountConverter.cryptoToFiat(
            amount = sourceCryptoAmount,
            cryptoCurrency = sourceCurrency
        )

        return InputConverter.Result(
            sourceFiatAmount = sourceFiatAmount,
            sourceCryptoAmount = sourceCryptoAmount,
            destinationFiatAmount = destFiatAmount,
            destinationCryptoAmount = amount,
            sourceNetworkFee = sourceCryptoAmountData.first,
            destinationNetworkFee = sourceCryptoAmountData.second,
            destinationCryptoAmountChangedByUser = changeByUser
        )
    }
}