package com.fabriik.kyc.ui.features.countryselection

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

interface CountrySelectionContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object LoadCountries : Event()
        data class CountrySelected(val country: Country) : Event()
        data class SearchCountries(val query: String?) : Event()
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
        val countries: List<Country> = emptyList(),
        val selectedCountry: Country? = null
    ) : FabriikContract.State
}