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

class CountrySelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<CountrySelectionContract.State, CountrySelectionContract.Event, CountrySelectionContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: CountrySelectionFragmentArgs

    private val kycApi = KycApi.create(application.applicationContext)

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = CountrySelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = CountrySelectionContract.State()

    override fun handleEvent(event: CountrySelectionContract.Event) {
        when (event) {
            is CountrySelectionContract.Event.LoadCountries ->
                loadCountries()

            is CountrySelectionContract.Event.SearchChanged -> {
                setState { copy(search = event.query ?: "") }
                applyFilters()
            }

            is CountrySelectionContract.Event.CountrySelected ->
                setEffect {
                    CountrySelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedCountry = event.country
                    )
                }

            is CountrySelectionContract.Event.BackClicked ->
                setEffect {
                    CountrySelectionContract.Effect.Back(
                        requestKey = arguments.requestKey,
                        selectedCountry = null
                    )
                }

            is CountrySelectionContract.Event.DismissClicked ->
                setEffect { CountrySelectionContract.Effect.Dismiss }
        }
    }

    private fun loadCountries() {
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