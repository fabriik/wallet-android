package com.fabriik.kyc.ui.features.countryselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.FlagUtil
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.R
import com.fabriik.kyc.data.KycApi
import com.fabriik.kyc.data.model.Country

class CountrySelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<CountrySelectionContract.State, CountrySelectionContract.Event, CountrySelectionContract.Effect>(
    application, savedStateHandle
), CountrySelectionEventHandler {

    private lateinit var arguments: CountrySelectionFragmentArgs

    private val kycApi = KycApi.create(application.applicationContext)

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = CountrySelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = CountrySelectionContract.State()

    override fun onBackClicked() {
        setEffect {
            CountrySelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedCountry = null
            )
        }
    }

    override fun onCountrySelected(country: Country) {
        setEffect {
            CountrySelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedCountry = country
            )
        }
    }

    override fun onSearchChanged(query: String?) {
        setState { copy(search = query ?: "") }
        applyFilters()
    }

    override fun onLoadCountries() {
        callApi(
            endState = { copy(initialLoadingVisible = false) },
            startState = { copy(initialLoadingVisible = true) },
            action = { kycApi.getCountries() },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        setState { copy(countries = it.data!!) }
                        applyFilters()
                    }

                    Status.ERROR ->
                        setEffect {
                            CountrySelectionContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                }
            }
        )
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
                        icon = FlagUtil.getDrawableId(getApplication(), it.code),
                        country = it
                    )
                }
            )
        }
    }
}