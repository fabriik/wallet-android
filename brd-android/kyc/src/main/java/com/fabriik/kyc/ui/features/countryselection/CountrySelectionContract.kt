package com.fabriik.kyc.ui.features.countryselection

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

interface CountrySelectionContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object LoadCountries : Event()
        data class SearchChanged(val query: String?) : Event()
        data class CountrySelected(val country: Country) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        data class ShowToast(val message: String): Effect()
        data class Back(
            val requestKey: String,
            val selectedCountry: Country?
        ) : Effect()
    }

    data class State(
        val search: String = "",
        val countries: List<Country> = emptyList(),
        val adapterItems: List<CountrySelectionAdapter.Item> = emptyList(),
        val initialLoadingVisible: Boolean = false
    ) : FabriikContract.State
}