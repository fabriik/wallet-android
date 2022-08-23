package com.fabriik.buy.ui.features.billingaddress

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class BillingAddressViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<BillingAddressContract.State, BillingAddressContract.Event, BillingAddressContract.Effect>(
    application, savedStateHandle
), KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private lateinit var arguments: BillingAddressFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = BillingAddressFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = BillingAddressContract.State()

    override fun handleEvent(event: BillingAddressContract.Event) {
        when (event) {
            BillingAddressContract.Event.BackPressed ->
                setEffect { BillingAddressContract.Effect.Back }

            BillingAddressContract.Event.DismissClicked ->
                setEffect { BillingAddressContract.Effect.Dismiss }

            BillingAddressContract.Event.ConfirmClicked ->
                onConfirmClicked()

            BillingAddressContract.Event.CountryClicked ->
                setEffect { BillingAddressContract.Effect.CountrySelection }

            is BillingAddressContract.Event.ZipChanged ->
                setState { copy(zip = event.zip).validate() }

            is BillingAddressContract.Event.CityChanged ->
                setState { copy(city = event.city).validate() }

            is BillingAddressContract.Event.StateChanged ->
                setState { copy(state = event.state).validate() }

            is BillingAddressContract.Event.AddressChanged ->
                setState { copy(address = event.address).validate() }

            is BillingAddressContract.Event.LastNameChanged ->
                setState { copy(lastName = event.lastName).validate() }

            is BillingAddressContract.Event.FirstNameChanged ->
                setState { copy(firstName = event.firstName).validate() }

            is BillingAddressContract.Event.CountryChanged ->
                setState { copy(country = event.country).validate() }

            is BillingAddressContract.Event.BrowserResult ->
                checkPaymentStatus()
        }
    }

    private fun checkPaymentStatus() {
        val reference = currentState.paymentReference ?: return

        callApi(
            endState = { copy(loadingIndicatorVisible = false) },
            startState = { copy(loadingIndicatorVisible = true) },
            action = { buyApi.getPaymentStatus(reference) },
            callback = {
                setEffect { BillingAddressContract.Effect.PaymentMethod }
            }
        )
    }

    private fun onConfirmClicked() {
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
                                BillingAddressContract.Effect.PaymentMethod
                            } else {
                                BillingAddressContract.Effect.OpenWebsite(redirectUrl)
                            }
                        }
                    }

                    Status.ERROR ->
                        setEffect {
                            BillingAddressContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                }
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