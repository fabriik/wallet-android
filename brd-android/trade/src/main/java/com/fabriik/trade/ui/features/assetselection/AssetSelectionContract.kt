package com.fabriik.trade.ui.features.assetselection

import com.fabriik.common.ui.base.FabriikContract

interface AssetSelectionContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object LoadAssets : Event()
        data class SearchChanged(val query: String?) : Event()
        data class AssetSelected(val asset: Country) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        data class ShowToast(val message: String): Effect()
        data class Back(
            val requestKey: String,
            val selectedAsset: Country?
        ) : Effect()
    }

    data class State(
        val search: String = "",
        val assets: List<Country> = emptyList(),
        val adapterItems: List<AssetSelectionAdapter.Item> = emptyList(),
        val initialLoadingVisible: Boolean = false
    ) : FabriikContract.State
}