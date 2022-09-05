package com.fabriik.trade.ui.features.assetselection

import com.fabriik.common.ui.base.FabriikEventHandler

interface AssetSelectionEventHandler: FabriikEventHandler<AssetSelectionContract.Event> {

    override fun handleEvent(event: AssetSelectionContract.Event) {
        return when (event) {
            is AssetSelectionContract.Event.LoadAssets -> onLoadAssets()
            is AssetSelectionContract.Event.BackClicked -> onBackClicked()
            is AssetSelectionContract.Event.SearchChanged -> onSearchChanged(event.query)
            is AssetSelectionContract.Event.AssetSelected -> onAssetSelected(event.asset)
        }
    }

    fun onBackClicked()

    fun onLoadAssets()

    fun onSearchChanged(query: String?)

    fun onAssetSelected(asset: AssetSelectionAdapter.AssetSelectionItem)
}