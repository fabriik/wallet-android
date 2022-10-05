package com.fabriik.trade.utils

import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.Wallet
import com.breadwallet.crypto.errors.FeeEstimationError
import com.breadwallet.ext.isZero
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.trade.data.model.FeeAmountData
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class EstimateSendingFee(
    private val breadBox: BreadBox,
    private val createFeeAmountData: CreateFeeAmountData
) {

    suspend operator fun invoke(amount: BigDecimal, walletCurrency: String, fiatCode: String): Result {
        if (amount.isZero()) {
            return Result.Unknown
        }

        val wallet = breadBox.wallet(walletCurrency).first()

        return loadFeeFromWalletKit(
            wallet, amount, walletCurrency, fiatCode
        )
    }

    private suspend fun loadFeeFromWalletKit(wallet: Wallet, cryptoAmount: BigDecimal, walletCurrency: String, fiatCode: String): Result {
        val amount = Amount.create(cryptoAmount.toDouble(), wallet.unit)
        val address = loadAddress(wallet.currency.code) ?: return Result.NetworkIssues
        val networkFee = wallet.feeForSpeed(TransferSpeed.Priority(walletCurrency))

        return try {
            val data = wallet.estimateFee(address, amount, networkFee)
            val fee = data.fee.toBigDecimal()
            val feeCurrency = data.currency.code

            val amountData = createFeeAmountData(
                fee = fee,
                feeCurrency = feeCurrency,
                fiatCode = fiatCode,
                walletCurrency = walletCurrency
            )

            if (amountData == null) {
                Result.NetworkIssues
            } else {
                Result.Estimated(amountData)
            }
        } catch (e: FeeEstimationError) {
            Result.InsufficientFunds(walletCurrency)
        } catch (e: IllegalStateException) {
            Result.NetworkIssues
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

    sealed class Result {
        object Unknown: Result()
        object NetworkIssues: Result()
        class InsufficientFunds(val currencyCode: String): Result()
        class Estimated(val data: FeeAmountData): Result() {
            fun cryptoAmountIfIncludedOrZero() = data.cryptoAmountIfIncludedOrZero()
            fun cryptoAmountIfIncludedOrNull() = data.cryptoAmountIfIncludedOrNull()
        }
    }
}