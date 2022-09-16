package com.fabriik.trade.ui.features.swap

import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.errors.FeeEstimationError
import com.breadwallet.ext.isZero
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.util.TokenUtil
import com.fabriik.trade.data.SwapTransactionsFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class SwapInputHelper(
    private val breadBox: BreadBox,
    private val transactionsFetcher: SwapTransactionsFetcher,
    private val acctMetaDataProvider: AccountMetaDataProvider
) {
    suspend fun isWalletEnabled(currencyCode: String): Boolean {
        val enabledWallets = acctMetaDataProvider.enabledWallets().first()
        val token = TokenUtil.tokenForCode(currencyCode) ?: return false
        return token.isSupported && enabledWallets.contains(token.currencyId)
    }

    fun isAnySwapPendingForSource(currency: String) =
        transactionsFetcher.transactionsRepository.isAnySwapPendingForSourceCurrency(currency)

    suspend fun loadCryptoBalance(currencyCode: String): BigDecimal? {
        val wallet = breadBox.wallets()
            .first()
            .find { it.currency.code.equals(currencyCode, ignoreCase = true) }

        return wallet?.balance?.toBigDecimal()
    }

    suspend fun loadAddress(currencyCode: String): Address? {
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

    suspend fun estimateFeeBasis(orderAddress: String, currency: String, orderAmount: BigDecimal) : Any {
        val wallet = breadBox.wallet(currency).first()

        // Skip if address is not valid
        val address = wallet.addressFor(orderAddress) ?: return SwapInputContract.ErrorMessage.NetworkIssues
        if (wallet.containsAddress(address)) return SwapInputContract.ErrorMessage.NetworkIssues

        val amount = Amount.create(orderAmount.toDouble(), wallet.unit)
        val networkFee = wallet.feeForSpeed(TransferSpeed.Priority(currency))

        return try {
            val data = wallet.estimateFee(address, amount, networkFee)
            val fee = data.fee.toBigDecimal()

            if (fee.isZero()) {
                SwapInputContract.ErrorMessage.InsufficientFundsForFee
            } else {
                data
            }
        } catch (e: FeeEstimationError) {
            SwapInputContract.ErrorMessage.InsufficientFundsForFee
        } catch (e: IllegalStateException) {
            SwapInputContract.ErrorMessage.NetworkIssues
        }
    }

    fun updateSwapTransactions() {
        transactionsFetcher.refreshData()
    }
}