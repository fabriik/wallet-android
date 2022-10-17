package com.fabriik.buy.ui.features.addcard

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.checkout.android_sdk.CheckoutAPIClient
import com.checkout.android_sdk.Request.CardTokenisationRequest
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.network.NetworkError
import com.fabriik.buy.R
import com.fabriik.buy.utils.formatCardNumber
import com.fabriik.buy.utils.formatDate
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class AddCardViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<AddCardContract.State, AddCardContract.Event, AddCardContract.Effect>(
    application, savedStateHandle
), AddCardEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val checkoutApi by kodein.instance<CheckoutAPIClient>()

    private lateinit var arguments: AddCardFragmentArgs

    override fun createInitialState() = AddCardContract.State()

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        super.parseArguments(savedStateHandle)

        arguments = AddCardFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun onBackClicked() {
        setEffect { AddCardContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { AddCardContract.Effect.Dismiss }
    }

    override fun onConfirmClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loadingIndicatorVisible = true) }

            checkoutApi.setTokenListener(object : CheckoutAPIClient.OnTokenGenerated {
                override fun onTokenGenerated(response: CardTokenisationResponse) {
                    setState { copy(loadingIndicatorVisible = false) }
                    setEffect { AddCardContract.Effect.BillingAddress(response.token, arguments.flow) }
                }

                override fun onError(error: CardTokenisationFail) {
                    setState { copy(loadingIndicatorVisible = false) }
                    setEffect { AddCardContract.Effect.ShowError(getString(R.string.Api_DefaultError)) }
                }

                override fun onNetworkError(error: NetworkError) {
                    setState { copy(loadingIndicatorVisible = false) }
                    setEffect { AddCardContract.Effect.ShowError(getString(R.string.Api_DefaultError)) }
                }
            })

            val expiryDate = currentState.expiryDate.split("/")

            checkoutApi.generateToken(
                CardTokenisationRequest(
                    currentState.cardNumber.replace("\\s+".toRegex(), ""),
                    null,
                    expiryDate[0],
                    expiryDate[1],
                    currentState.securityCode
                )
            )
        }
    }

    override fun onSecurityCodeInfoClicked() {
        setEffect { AddCardContract.Effect.ShowCvvInfoDialog }
    }

    override fun onCardNumberChanged(cardNumber: String) {
        setState {
            copy(
                cardNumber = formatCardNumber(cardNumber)
            ).validate()
        }
    }

    override fun onSecurityCodeChanged(securityCode: String) {
        setState {
            copy(
                securityCode = securityCode
            ).validate()
        }
    }

    override fun onExpirationDateChanged(expirationDate: String) {
        val formatDate = formatDate(expirationDate)

        setState {
            copy(
                expiryDate = formatDate
            ).validate()
        }

        if (formatDate.length == 5) {
            validateDate(formatDate)
        }
    }

    private fun validateDate(input: String?) {
        if (!isExpiryDateValid(input)) {
            setEffect { AddCardContract.Effect.ShowError(getString(R.string.Buy_InvalidExpirationDate)) }
        }
    }

    private fun isExpiryDateValid(input: String?): Boolean {
        if (input == null) {
            return false
        }

        val splitDate = if (input.length == 5) {
            input.split("/")
        } else return false

        return splitDate[0].toInt() in 1..12
    }

    private fun AddCardContract.State.validate() = copy(
        confirmButtonEnabled = cardNumber.length == 19
                && isExpiryDateValid(expiryDate)
                && securityCode.length == 3
    )
}