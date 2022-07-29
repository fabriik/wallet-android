package com.fabriik.trade.utils

import android.util.Log
import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.Wallet
import com.breadwallet.ext.isZero
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.data.Status
import com.fabriik.trade.data.SwapApi
import com.fabriik.trade.data.model.FeeAmountData
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class EstimateSwapFee(
    private val swapApi: SwapApi,
    private val breadBox: BreadBox,
    private val ratesRepository: RatesRepository
) {

    suspend operator fun invoke(amount: BigDecimal, walletCurrency: String, fiatCode: String) : FeeAmountData? {
        if (amount.isZero()) {
            return null
        }

        val wallet = breadBox.wallet(walletCurrency).first()

        return if (isEthereumOrErc20Token(wallet)) {
            // use API call for ethereum or erc20 tokens
            loadFeeFromApi(
                wallet, amount, walletCurrency, fiatCode
            )
        } else {
            // use WalletKit for other currencies
            loadFeeFromWalletKit(
                wallet, amount, walletCurrency, fiatCode
            )
        }
    }

    private fun isEthereumOrErc20Token(wallet: Wallet) =
        wallet.currency.isEthereum() || wallet.currency.isErc20()

    private suspend fun loadFeeFromWalletKit(wallet: Wallet, cryptoAmount: BigDecimal, walletCurrency: String, fiatCode: String) : FeeAmountData? {
        val amount = Amount.create(cryptoAmount.toDouble(), wallet.unit)
        val address = loadAddress(wallet.currency.code) ?: return null

        return try {
            val data = wallet.estimateFee(address, amount)
            val fee = data.fee.toBigDecimal()
            val feeCurrency = data.currency.code

            return mapFeeToResponse(
                fee = fee,
                feeCurrency = feeCurrency,
                fiatCurrency = fiatCode,
                walletCurrency = walletCurrency
            )
        } catch (e: Exception) {
            Log.d("AmountConverter", "Fee estimation failed: ${e.message}")
            null
        }
    }

    private suspend fun loadFeeFromApi(wallet: Wallet, amount: BigDecimal, walletCurrency: String, fiatCode: String): FeeAmountData? {
        val address = loadAddress(wallet.currency.code) ?: return null

        val response = swapApi.estimateEthFee(
            amount, walletCurrency, address.toString()
        )

        return when (response.status) {
            Status.SUCCESS ->
                return mapFeeToResponse(
                    requireNotNull(response.data), "eth", walletCurrency, fiatCode
                )
            Status.ERROR -> {
                Log.d(TAG, "estimate-fee failed: ${response.message ?: "unknown"}")
                null
            }
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

    private fun mapFeeToResponse(fee: BigDecimal, feeCurrency: String, walletCurrency: String, fiatCurrency: String): FeeAmountData? {
        val fiatFee = ratesRepository.getFiatForCrypto(
            cryptoAmount = fee,
            cryptoCode = feeCurrency,
            fiatCode = fiatCurrency
        ) ?: return null

        return FeeAmountData(
            fiatAmount = fiatFee,
            fiatCurrency = fiatCurrency,
            cryptoAmount = fee,
            cryptoCurrency = feeCurrency,
            included = walletCurrency.equals(feeCurrency, true)
        )
    }

    companion object {
        const val TAG = "EstimateSwapFee"
    }
}