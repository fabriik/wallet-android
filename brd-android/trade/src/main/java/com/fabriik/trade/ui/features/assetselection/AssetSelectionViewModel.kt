package com.fabriik.trade.ui.features.assetselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.FlagUtil
import com.fabriik.common.utils.toBundle

class AssetSelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<AssetSelectionContract.State, AssetSelectionContract.Event, AssetSelectionContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: CountrySelectionFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = CountrySelectionFragmentArgs.fromBundle(
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
                        selectedAsset = event.country
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
        setState {
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
        }
    }
}