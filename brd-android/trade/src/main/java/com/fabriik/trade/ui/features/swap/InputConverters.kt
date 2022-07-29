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
        val sourceCryptoAmount = amountConverter.fiatToCrypto(
            amount = amount,
            cryptoCurrency = sourceCurrency
        )

        val destCryptoAmountData = amountConverter.convertSourceCryptoToDestinationCrypto(
            rate = exchangeRate,
            markup = markupFactor,
            amount = sourceCryptoAmount,
            sourceCurrency = sourceCurrency,
            destinationCurrency = destinationCurrency
        )

        val destCryptoAmount = destCryptoAmountData.third

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
        val sourceFiatAmount = amountConverter.cryptoToFiat(
            amount = amount,
            cryptoCurrency = sourceCurrency
        )

        val destCryptoAmountData = amountConverter.convertSourceCryptoToDestinationCrypto(
            rate = exchangeRate,
            markup = markupFactor,
            amount = amount,
            sourceCurrency = sourceCurrency,
            destinationCurrency = destinationCurrency
        )

        val destCryptoAmount = destCryptoAmountData.third

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
        val destCryptoAmount = amountConverter.fiatToCrypto(
            amount = amount,
            cryptoCurrency = destinationCurrency
        )

        val sourceCryptoAmountData = amountConverter.convertDestinationCryptoToSourceCrypto(
            amount = destCryptoAmount,
            destinationCurrency = destinationCurrency,
            sourceCurrency = sourceCurrency,
            rate = exchangeRate,
            markup = markupFactor
        )

        val sourceCryptoAmount = sourceCryptoAmountData.third

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
        val destFiatAmount = amountConverter.cryptoToFiat(
            amount = amount,
            cryptoCurrency = destinationCurrency
        )

        val sourceCryptoAmountData = amountConverter.convertDestinationCryptoToSourceCrypto(
            rate = exchangeRate,
            amount = amount,
            markup = markupFactor,
            sourceCurrency = sourceCurrency,
            destinationCurrency = destinationCurrency
        )

        val sourceCryptoAmount = sourceCryptoAmountData.third

        val sourceFiatAmount = amountConverter.cryptoToFiat(
            amount = sourceCryptoAmount,
            cryptoCurrency = destinationCurrency
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