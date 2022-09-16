package com.fabriik.kyc.ui.features.countryselection

import com.fabriik.kyc.data.model.Country
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CountrySelectionEventHandlerTest {

    @Mock lateinit var country: Country

    @Spy val handler = object : CountrySelectionEventHandler {
        override fun onBackClicked() {}
        override fun onLoadCountries() {}
        override fun onSearchChanged(query: String?) { }
        override fun onCountrySelected(country: Country) {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(CountrySelectionContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_searchChanged_callOnSearchChanged() {
        val search = "test_search"
        handler.handleEvent(CountrySelectionContract.Event.SearchChanged(search))
        verify(handler).onSearchChanged(search)
    }

    @Test
    fun handleEvent_loadCountries_callOnLoadCountries() {
        handler.handleEvent(CountrySelectionContract.Event.LoadCountries)
        verify(handler).onLoadCountries()
    }

    @Test
    fun handleEvent_countrySelected_callOnCountrySelected() {
        handler.handleEvent(CountrySelectionContract.Event.CountrySelected(country))
        verify(handler).onCountrySelected(country)
    }
}