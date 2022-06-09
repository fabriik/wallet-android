package com.fabriik.kyc.ui.features.countryselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.FlagUtil
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.data.KycApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            is CountrySelectionContract.Event.LoadCountries -> {
                viewModelScope.launch(Dispatchers.IO) {
                    //show loading
                    setState { copy(initialLoadingVisible = true) }

                    val response = kycApi.getCountries()

                    //dismiss loading
                    setState { copy(initialLoadingVisible = false) }

                    when (response.status) {
                        Status.SUCCESS -> {
                            setState { copy(countries = response.data!!) }
                            applyFilters()
                        }

                        Status.ERROR ->
                            setEffect {
                                CountrySelectionContract.Effect.ShowToast(response.message!!)
                            }
                    }
                }
            }

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