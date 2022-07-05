package com.fabriik.trade.ui.features.assetselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle

class AssetSelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<AssetSelectionContract.State, AssetSelectionContract.Event, AssetSelectionContract.Effect>(
    application, savedStateHandle
) {

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
                        selectedAsset = event.asset
                    )
                }

            is AssetSelectionContract.Event.BackClicked ->
                setEffect {
                    AssetSelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedAsset = null
                    )
                }
        }
    }

    private fun loadAssets() {
        setState {
            copy(
                assets = listOf(
                    AssetSelectionAdapter.AssetSelectionItem(
                        title = "BSV",
                        subtitle = "BSV",
                        fiatBalance = "42.31 USD",
                        cryptoBalance = "2.312132 BSV",
                        cryptoCurrencyCode = "BSV"
                    ),
                    AssetSelectionAdapter.AssetSelectionItem(
                        title = "BTC",
                        subtitle = "BTC",
                        fiatBalance = "22142.31 USD",
                        cryptoBalance = "1.312132 BTC",
                        cryptoCurrencyCode = "BTC"
                    ),
                    AssetSelectionAdapter.AssetSelectionItem(
                        title = "Ethereum",
                        subtitle = "ETH",
                        fiatBalance = "2922.31 USD",
                        cryptoBalance = "2 ETH",
                        cryptoCurrencyCode = "ETH"
                    )
                )
            )
        }

        applyFilters()
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