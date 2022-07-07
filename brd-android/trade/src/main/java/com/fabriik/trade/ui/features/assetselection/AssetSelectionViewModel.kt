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
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
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

    private val handler = AssetSelectionHandler(
        direct.instance(),
        direct.instance(),
        direct.instance()
    )

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
            val assets = handler.getAssets(supportedCurrencies)

            setState { copy(assets = assets) }
            applyFilters()
        }
    }

    private fun applyFilters() {
        setState {
            copy(
                adapterItems = currentState.assets.filter {
                    it.title.contains(currentState.search, true) ||
                            it.subtitle.contains(currentState.search, true)
                }
            )
        }
    }
}