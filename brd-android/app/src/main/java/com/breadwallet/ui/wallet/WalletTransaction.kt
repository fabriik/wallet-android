/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 1/21/20.
 * Copyright (c) 2020 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui.wallet

import android.content.Context
import com.breadwallet.R
import com.breadwallet.platform.entities.GiftMetaData
import com.fabriik.trade.data.model.SwapBuyTransactionData
import com.fabriik.trade.data.response.ExchangeOrderStatus
import dev.zacsweers.redacted.annotations.Redacted
import java.math.BigDecimal

data class WalletTransaction(
    @Redacted val txHash: String,
    val amount: BigDecimal,
    val amountInFiat: BigDecimal,
    @Redacted val toAddress: String,
    @Redacted val fromAddress: String,
    val isReceived: Boolean,
    @Redacted val timeStamp: Long,
    @Redacted val memo: String? = null,
    val fee: BigDecimal,
    val confirmations: Int,
    val isComplete: Boolean,
    val isPending: Boolean,
    val isErrored: Boolean,
    val progress: Int,
    val currencyCode: String,
    val feeToken: String = "",
    val confirmationsUntilFinal: Int,
    val gift: GiftMetaData? = null,
    val isStaking: Boolean = false,
    val exchangeData: ExchangeData? = null
) {
    val isFeeForToken: Boolean = feeToken.isNotBlank()
}

sealed class ExchangeData(
    val transactionData: SwapBuyTransactionData
) {

    abstract fun getIcon(): Int
    abstract fun getTransactionTitle(context: Context): String?

    class Deposit(transactionData: SwapBuyTransactionData) : ExchangeData(transactionData) {

        override fun getIcon() = R.drawable.ic_transaction_swap

        override fun getTransactionTitle(context: Context): String? {
            return when (transactionData.exchangeStatus) {
                ExchangeOrderStatus.PENDING -> context.getString(
                    R.string.Swap_TransactionItem_SwappingTo,
                    transactionData.getWithdrawalCurrencyUpperCase()
                )
                ExchangeOrderStatus.COMPLETE, ExchangeOrderStatus.MANUALLY_SETTLED -> context.getString(
                    R.string.Swap_TransactionItem_SwappedTo,
                    transactionData.getWithdrawalCurrencyUpperCase()
                )
                ExchangeOrderStatus.FAILED -> context.getString(
                    R.string.Swap_TransactionItem_SwapToFailed,
                    transactionData.getWithdrawalCurrencyUpperCase()
                )
                ExchangeOrderStatus.REFUNDED -> context.getString(
                    R.string.Swap_TransactionItem_Refunded
                )
                else -> null
            }
        }
    }

    class BuyWithdrawal(transactionData: SwapBuyTransactionData) : ExchangeData(transactionData) {

        override fun getIcon() = R.drawable.ic_transaction_buy

        override fun getTransactionTitle(context: Context): String? {
            return when (transactionData.exchangeStatus) {
                ExchangeOrderStatus.PENDING -> context.getString(
                    R.string.Swap_TransactionItem_PurchasePending
                )
                ExchangeOrderStatus.COMPLETE, ExchangeOrderStatus.MANUALLY_SETTLED -> context.getString(
                    R.string.Swap_TransactionItem_Purchased
                )
                ExchangeOrderStatus.FAILED -> context.getString(
                    R.string.Swap_TransactionItem_PurchaseFailed
                )
                ExchangeOrderStatus.REFUNDED -> context.getString(
                    R.string.Swap_TransactionItem_Refunded
                )
                else -> null
            }
        }
    }

    class SwapWithdrawal(transactionData: SwapBuyTransactionData) : ExchangeData(transactionData) {

        override fun getIcon() = R.drawable.ic_transaction_swap

        override fun getTransactionTitle(context: Context): String? {
            return when (transactionData.exchangeStatus) {
                ExchangeOrderStatus.PENDING -> context.getString(
                    R.string.Swap_TransactionItem_SwappingFrom,
                    transactionData.getDepositCurrencyUpperCase()
                )
                ExchangeOrderStatus.COMPLETE, ExchangeOrderStatus.MANUALLY_SETTLED -> context.getString(
                    R.string.Swap_TransactionItem_SwappedFrom,
                    transactionData.getDepositCurrencyUpperCase()
                )
                ExchangeOrderStatus.FAILED -> context.getString(
                    R.string.Swap_TransactionItem_SwapFromFailed,
                    transactionData.getDepositCurrencyUpperCase()
                )
                ExchangeOrderStatus.REFUNDED -> context.getString(
                    R.string.Swap_TransactionItem_Refunded
                )
                else -> null
            }
        }
    }
}