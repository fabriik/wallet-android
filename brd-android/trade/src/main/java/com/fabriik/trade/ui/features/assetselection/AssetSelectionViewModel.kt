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
                        fiatBalance = "42.31",
                        fiatCurrencyCode = "USD",
                        cryptoBalance = "2.312132",
                        cryptoCurrencyCode = "BSV"
                    ),
                    AssetSelectionAdapter.AssetSelectionItem(
                        fiatBalance = "22142.31",
                        fiatCurrencyCode = "USD",
                        cryptoBalance = "1.312132",
                        cryptoCurrencyCode = "BTC"
                    ),
                    AssetSelectionAdapter.AssetSelectionItem(
                        fiatBalance = "2922.31",
                        fiatCurrencyCode = "USD",
                        cryptoBalance = "2",
                        cryptoCurrencyCode = "ETH"
                    )
                )
            )
        }

        /*callApi(
            endState = { copy(initialLoadingVisible = false) },
            startState = { copy(initialLoadingVisible = true) },
            action = { kycApi.getCountries() },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        setState { copy(countries = it.data!!) }
                        applyFilters()
                    }

                    Status.ERROR ->
                        setEffect {
                            AssetSelectionContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                }
            }
        )*/
    }

    private fun applyFilters() {
        /*setState {
            copy(
                adapterItems = currentState.assets.filter {
                    it.name.contains(
                        other = currentState.search,
                        ignoreCase = true
                    )
                }.map {
                    AssetSelectionAdapter.Item(
                        icon = FlagUtil.getDrawableId(getApplication(), it.code),
                        country = it
                    )
                }
            )
        }*/
    }
}