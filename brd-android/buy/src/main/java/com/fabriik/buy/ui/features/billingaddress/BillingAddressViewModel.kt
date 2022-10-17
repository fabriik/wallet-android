package com.fabriik.buy.ui.features.billingaddress

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.data.model.Country
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class BillingAddressViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<BillingAddressContract.State, BillingAddressContract.Event, BillingAddressContract.Effect>(
    application, savedStateHandle
), BillingAddressEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private lateinit var arguments: BillingAddressFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = BillingAddressFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = BillingAddressContract.State()

    override fun onBackClicked() {
        setEffect { BillingAddressContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { BillingAddressContract.Effect.Dismiss }
    }

    override fun onConfirmClicked() {
        callApi(
            endState = { copy(loadingIndicatorVisible = false) },
            startState = { copy(loadingIndicatorVisible = true) },
            action = {
                buyApi.addPaymentInstrument(
                    token = arguments.cardToken,
                    firstName = currentState.firstName,
                    lastName = currentState.lastName,
                    zip = currentState.zip,
                    city = currentState.city,
                    state = currentState.state,
                    address = currentState.address,
                    countryCode = requireNotNull(currentState.country).code,
                )
            },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        val reference = requireNotNull(it.data).paymentReference
                        val redirectUrl = requireNotNull(it.data).redirectUrl

                        setState { copy(paymentReference = reference) }

                        setEffect {
                            if (redirectUrl.isNullOrBlank()) {
                                BillingAddressContract.Effect.PaymentMethod(arguments.flow)
                            } else {
                                BillingAddressContract.Effect.OpenWebsite(redirectUrl)
                            }
                        }
                    }

                    Status.ERROR ->
                        setEffect {
                            BillingAddressContract.Effect.ShowError(
                                it.message ?: getString(R.string.Api_DefaultError)
                            )
                        }
                }
            }
        )
    }

    override fun onCountryClicked() {
        setEffect { BillingAddressContract.Effect.CountrySelection }
    }

    override fun onZipChanged(zip: String) {
        setState { copy(zip = zip).validate() }
    }

    override fun onCityChanged(city: String) {
        setState { copy(city = city).validate() }
    }

    override fun onStateChanged(state: String) {
        setState { copy(state = state).validate() }
    }

    override fun onAddressChanged(address: String) {
        setState { copy(address = address).validate() }
    }

    override fun onCountryChanged(country: Country) {
        setState { copy(country = country).validate() }
    }

    override fun onFirstNameChanged(firstName: String) {
        setState { copy(firstName = firstName).validate() }
    }

    override fun onLastNameChanged(lastName: String) {
        setState { copy(lastName = lastName).validate() }
    }

    override fun onPaymentChallengeResult(result: Int) {
        val reference = currentState.paymentReference ?: return

        callApi(
            endState = { copy(loadingIndicatorVisible = false) },
            startState = { copy(loadingIndicatorVisible = true) },
            action = { buyApi.getPaymentStatus(reference) },
            callback = {
                setEffect { BillingAddressContract.Effect.PaymentMethod(arguments.flow) }
            }
        )
    }

    private fun BillingAddressContract.State.validate() = copy(
        confirmEnabled = country != null &&
                zip.isNotBlank() &&
                city.isNotBlank() &&
                address.isNotBlank() &&
                lastName.isNotBlank() &&
                firstName.isNotBlank()
    )
}