package com.fabriik.kyc.ui.features.countryselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.data.model.Country

class CountrySelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<CountrySelectionContract.State, CountrySelectionContract.Event, CountrySelectionContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: CountrySelectionFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = CountrySelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = CountrySelectionContract.State(
        selectedCountry = arguments.selectedCountry
    )

    override fun handleEvent(event: CountrySelectionContract.Event) {
        when (event) {
            is CountrySelectionContract.Event.LoadCountries ->
                //todo: loading indicator
                // todo: get data from API
                setState {
                    copy(
                        countries = listOf(
                            Country(
                                code = "us",
                                name = "USA"
                            ),
                            Country(
                                code = "uk",
                                name = "United Kingdom"
                            ),
                            Country(
                                code = "ar",
                                name = "Argentina"
                            ),
                            Country(
                                code = "es",
                                name = "Spain"
                            ),
                            Country(
                                code = "si",
                                name = "Slovenia"
                            )
                        )
                    )
                }

            is CountrySelectionContract.Event.BackClicked ->
                setEffect {
                    CountrySelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedCountry = currentState.selectedCountry
                    )
                }

            is CountrySelectionContract.Event.DismissClicked ->
                setEffect { CountrySelectionContract.Effect.Dismiss }
        }
    }
}