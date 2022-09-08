package com.fabriik.trade.utils

import com.breadwallet.ext.isZero
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal

class EstimateReceivingFee(
    private val createFeeData: CreateFeeAmountData
) {

    operator fun invoke(quote: QuoteResponse?, amount: BigDecimal, walletCurrency: String, fiatCode: String) : FeeAmountData? {
        if (amount.isZero() || quote == null) {
            return null
        }

        return createFeeData(
            fee = requireNotNull(quote.toFee),
            feeCurrency = requireNotNull(quote.toFeeCurrency).currency,
            walletCurrency = walletCurrency,
            fiatCode = fiatCode,
        )
    }
}