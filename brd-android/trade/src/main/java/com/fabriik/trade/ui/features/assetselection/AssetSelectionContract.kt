package com.fabriik.trade.ui.features.assetselection

import com.fabriik.common.ui.base.FabriikContract

interface AssetSelectionContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object LoadAssets : Event()
        data class SearchChanged(val query: String?) : Event()
        data class AssetSelected(val asset: AssetSelectionAdapter.AssetSelectionItem) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        data class ShowToast(val message: String): Effect()
        data class Back(
            val requestKey: String,
            val selectedCurrency: String?
        ) : Effect()
    }

    data class State(
        val search: String = "",
        val assets: List<AssetSelectionAdapter.AssetSelectionItem> = emptyList(),
        val adapterItems: List<AssetSelectionAdapter.AssetSelectionItem> = emptyList(),
        val initialLoadingVisible: Boolean = false
    ) : FabriikContract.State
}