package com.fabriik.trade.ui.features.assetselection

import com.breadwallet.breadbox.*
import com.breadwallet.model.TokenItem
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.util.TokenUtil
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.utils.contains
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class AssetSelectionHandler(
    private val breadBox: BreadBox,
    private val ratesRepository: RatesRepository,
    private val acctMetaDataProvider: AccountMetaDataProvider
) {

    private val fiatIso = BRSharedPrefs.getPreferredFiatIso()

    suspend fun getAssets(supportedCurrencies: Array<String>, sourceCurrency: String?): List<AssetSelectionAdapter.AssetSelectionItem> {
        val system = breadBox.system().first()
        val networks = system.networks
        val trackedWallets = acctMetaDataProvider.enabledWallets().first()

        return TokenUtil.getTokenItems()
            .filter { token ->
                val hasNetwork = networks.any { it.containsCurrency(token.currencyId) }
                val isErc20 = token.type == "erc20"
                val isSelectedSourceCurrency = sourceCurrency.equals(token.symbol, true)
                (token.isSupported && (isErc20 || hasNetwork) && !isSelectedSourceCurrency)
            }
            .map { tokenItem ->
                val enabled = trackedWallets.contains(tokenItem.currencyId)
                        && supportedCurrencies.contains(tokenItem.symbol, true)

                val wallet = when {
                    enabled -> system.wallets.findByCurrencyId(tokenItem.currencyId)
                    else -> null
                }

                val cryptoBalance = wallet?.balance?.toBigDecimal()
                val fiatBalance = cryptoBalance?.let {
                    ratesRepository.getFiatForCrypto(
                        cryptoAmount = it,
                        cryptoCode = tokenItem.symbol,
                        fiatCode = fiatIso
                    )
                }

                tokenItem.asAssetSelectionItem(
                    enabled = enabled,
                    cryptoBalance = cryptoBalance,
                    fiatBalance = fiatBalance
                )
            }
            .sortedByDescending { it.enabled }
    }

    private fun TokenItem.asAssetSelectionItem(
        enabled: Boolean, fiatBalance: BigDecimal?, cryptoBalance: BigDecimal?
    ): AssetSelectionAdapter.AssetSelectionItem {
        return AssetSelectionAdapter.AssetSelectionItem(
            title = name,
            enabled = enabled,
            subtitle = symbol,
            fiatBalance = fiatBalance?.formatFiatForUi(fiatIso),
            cryptoBalance = cryptoBalance?.formatCryptoForUi(symbol),
            cryptoCurrencyCode = symbol
        )
    }
}