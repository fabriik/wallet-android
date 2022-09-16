package com.fabriik.kyc.ui.features.countryselection

import com.fabriik.common.ui.base.FabriikEventHandler
import com.fabriik.kyc.data.model.Country

interface CountrySelectionEventHandler: FabriikEventHandler<CountrySelectionContract.Event> {

    override fun handleEvent(event: CountrySelectionContract.Event) {
        return when (event) {
            is CountrySelectionContract.Event.BackClicked -> onBackClicked()
            is CountrySelectionContract.Event.SearchChanged -> onSearchChanged(event.query)
            is CountrySelectionContract.Event.LoadCountries -> onLoadCountries()
            is CountrySelectionContract.Event.CountrySelected -> onCountrySelected(event.country)
        }
    }

    fun onBackClicked()

    fun onLoadCountries()

    fun onSearchChanged(query: String?)

    fun onCountrySelected(country: Country)
}