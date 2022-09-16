package com.fabriik.trade.ui.features.assetselection

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AssetSelectionEventHandlerTest {

    @Mock lateinit var assetItem: AssetSelectionAdapter.AssetSelectionItem

    @Spy val handler = object : AssetSelectionEventHandler {
        override fun onBackClicked() {}
        override fun onLoadAssets() {}
        override fun onSearchChanged(query: String?) {}
        override fun onAssetSelected(asset: AssetSelectionAdapter.AssetSelectionItem) {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(AssetSelectionContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_loadAssets_callOnLoadAssets() {
        handler.handleEvent(AssetSelectionContract.Event.LoadAssets)
        verify(handler).onLoadAssets()
    }

    @Test
    fun handleEvent_searchChanged_callOnSearchChanged() {
        val search = "test"
        handler.handleEvent(AssetSelectionContract.Event.SearchChanged(search))
        verify(handler).onSearchChanged(search)
    }

    @Test
    fun handleEvent_assetSelected_callOnAssetSelected() {
        handler.handleEvent(AssetSelectionContract.Event.AssetSelected(assetItem))
        verify(handler).onAssetSelected(assetItem)
    }
}