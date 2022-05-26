package com.fabriik.kyc.ui.features.countryselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.R
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
        selectedCountryCode = arguments.selectedCountry?.code
    )

    override fun handleEvent(event: CountrySelectionContract.Event) {
        when (event) {
            is CountrySelectionContract.Event.LoadCountries -> {
                //todo: loading indicator
                // todo: get data from API

                setState {
                    copy(
                        countries = listOf(
                            Country(
                                code = "ar",
                                name = "Argentina"
                            ),
                            Country(
                                code = "si",
                                name = "Slovenia"
                            ),
                            Country(
                                code = "gb",
                                name = "United Kingdom"
                            ),
                            Country(
                                code = "us",
                                name = "USA"
                            )
                        )
                    )
                }

                applyFilters()
            }

            is CountrySelectionContract.Event.SearchChanged -> {
                setState { copy(search = event.query ?: "") }
                applyFilters()
            }

            is CountrySelectionContract.Event.CountrySelected -> {
                setState { copy(selectedCountryCode = event.country.code) }
                applyFilters()
            }

            is CountrySelectionContract.Event.BackClicked ->
                setEffect {
                    CountrySelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedCountry = currentState.countries.find {
                            it.code == currentState.selectedCountryCode
                        }
                    )
                }

            is CountrySelectionContract.Event.DismissClicked ->
                setEffect { CountrySelectionContract.Effect.Dismiss }
        }
    }

    private fun applyFilters() {
        setState {
            copy(
                adapterItems = currentState.countries.filter {
                    it.name.contains(
                        other = currentState.search,
                        ignoreCase = true
                    )
                }.map {
                    CountrySelectionAdapter.Item(
                        icon = getFlag(it),
                        country = it,
                        selected = it.code == currentState.selectedCountryCode
                    )
                }
            )
        }
    }

    private fun getFlag(country: Country) = when(country.code) {
        "ar" -> R.drawable.ic_flag_ar
        "gb" -> R.drawable.ic_flag_gb
        "si" -> R.drawable.ic_flag_si
        "us" -> R.drawable.ic_flag_us
        else -> R.drawable.ic_flag_default
    }
}