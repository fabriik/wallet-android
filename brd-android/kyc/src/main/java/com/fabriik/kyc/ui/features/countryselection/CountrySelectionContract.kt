package com.fabriik.kyc.ui.features.countryselection

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

interface CountrySelectionContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object LoadCountries : Event()
        object DismissClicked : Event()
        data class SearchChanged(val query: String?) : Event()
        data class CountrySelected(val country: Country) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class Back(
            val requestKey: String,
            val selectedCountry: Country?
        ) : Effect()

        data class SelectListItem(val selectedCountry: String) : Effect()
    }

    data class State(
        val search: String = "",
        val countries: List<Country> = emptyList(),
        val adapterItems: List<CountrySelectionAdapter.Item> = emptyList(),
        val selectedCountryCode: String? = null
    ) : FabriikContract.State
}