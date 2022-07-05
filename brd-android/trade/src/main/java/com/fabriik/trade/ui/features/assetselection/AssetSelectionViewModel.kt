package com.fabriik.trade.ui.features.assetselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.BreadBox
import com.breadwallet.breadbox.containsCurrency
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.breadbox.toBigDecimal
import com.breadwallet.crypto.Wallet
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.util.TokenUtil
import com.breadwallet.ui.formatFiatForUi
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.math.BigDecimal
import java.util.*

class AssetSelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<AssetSelectionContract.State, AssetSelectionContract.Event, AssetSelectionContract.Effect>(
    application, savedStateHandle
), KodeinAware {

    override val kodein by closestKodein { application }

    private val fiatIso = BRSharedPrefs.getPreferredFiatIso()
    private val breadBox by kodein.instance<BreadBox>()
    private val ratesRepository by kodein.instance<RatesRepository>()

    private lateinit var arguments: AssetSelectionFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = AssetSelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = AssetSelectionContract.State()

    override fun handleEvent(event: AssetSelectionContract.Event) {
        when (event) {
            is AssetSelectionContract.Event.LoadAssets ->
                loadAssets()

            is AssetSelectionContract.Event.SearchChanged -> {
                setState { copy(search = event.query ?: "") }
                applyFilters()
            }

            is AssetSelectionContract.Event.AssetSelected ->
                setEffect {
                    AssetSelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedCurrency = event.asset.cryptoCurrencyCode
                    )
                }

            is AssetSelectionContract.Event.BackClicked ->
                setEffect {
                    AssetSelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedCurrency = null
                    )
                }
        }
    }

    private fun loadAssets() {
        val supportedCurrencies = arguments.currencies

        viewModelScope.launch(Dispatchers.IO) {
            val system = breadBox.system().first()
            val networks = system.networks
            val availableWallets = system.wallets

            val assets = TokenUtil.getTokenItems()
                .filter { token ->
                    val hasNetwork = networks.any { it.containsCurrency(token.currencyId) }
                    val isErc20 = token.type == "erc20"
                    token.isSupported && (isErc20 || hasNetwork)
                }
                .mapNotNull { tokenItem ->
                    val wallet = availableWallets.find {
                        it.currency.code.equals(tokenItem.symbol)
                    } ?: return@mapNotNull null

                    val currencyCode = supportedCurrencies.firstOrNull {
                        it.equals(tokenItem.symbol, ignoreCase = true)
                    } ?: return@mapNotNull null

                    mapToAssetSelectionItem(
                        currencyCode = currencyCode,
                        wallet = wallet
                    )
                }

            setState { copy(assets = assets) }
            applyFilters()
        }
    }

    private fun mapToAssetSelectionItem(
        currencyCode: String, wallet: Wallet
    ): AssetSelectionAdapter.AssetSelectionItem {
        val token = TokenUtil.tokenForCode(currencyCode)
        val currencyFullName = token?.name ?: currencyCode
        val cryptoBalance = wallet.balance.toBigDecimal()
        val fiatBalance = ratesRepository.getFiatForCrypto(
            cryptoBalance, currencyCode, fiatIso
        ) ?: BigDecimal.ZERO

        return AssetSelectionAdapter.AssetSelectionItem(
            title = currencyFullName,
            subtitle = currencyCode,
            fiatBalance = fiatBalance.formatFiatForUi(
                currencyCode = fiatIso
            ),
            cryptoBalance = cryptoBalance.formatCryptoForUi(
                currencyCode = currencyCode
            ),
            cryptoCurrencyCode = currencyCode
        )
    }

    private fun applyFilters() {
        setState {
            copy(
                adapterItems = currentState.assets.filter {
                    it.cryptoCurrencyCode.contains(
                        other = currentState.search,
                        ignoreCase = true
                    )
                }
            )
        }
    }
}