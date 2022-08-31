package com.fabriik.buy.ui.features.addcard

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.checkout.android_sdk.CheckoutAPIClient
import com.checkout.android_sdk.Request.CardTokenisationRequest
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.Utils.Environment
import com.checkout.android_sdk.network.NetworkError
import com.fabriik.buy.R
import com.fabriik.buy.utils.formatCardNumber
import com.fabriik.buy.utils.formatDate
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCardViewModel(
    application: Application
) : FabriikViewModel<AddCardContract.State, AddCardContract.Event, AddCardContract.Effect>(
    application
), AddCardEventHandler {

    private val checkoutClient = CheckoutAPIClient(
        getApplication(),
        "pk_sbox_ees63clhrko6kta6j3cwloebg4#",
        Environment.SANDBOX //todo: change to prod env
    )

    override fun createInitialState() = AddCardContract.State()

    override fun onBackClicked() {
        setEffect { AddCardContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { AddCardContract.Effect.Dismiss }
    }

    override fun onConfirmClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loadingIndicatorVisible = true) }

            checkoutClient.setTokenListener(object : CheckoutAPIClient.OnTokenGenerated {
                override fun onTokenGenerated(response: CardTokenisationResponse) {
                    setState { copy(loadingIndicatorVisible = false) }
                    setEffect { AddCardContract.Effect.BillingAddress(response.token) }
                }

                override fun onError(error: CardTokenisationFail) {
                    setState { copy(loadingIndicatorVisible = false) }
                    setEffect { AddCardContract.Effect.ShowError(getString(R.string.FabriikApi_DefaultError)) }
                }

                override fun onNetworkError(error: NetworkError) {
                    setState { copy(loadingIndicatorVisible = false) }
                    setEffect { AddCardContract.Effect.ShowError(getString(R.string.FabriikApi_DefaultError)) }
                }
            })

            val expiryDate = currentState.expiryDate.split("/")

            checkoutClient.generateToken(
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
            setEffect { AddCardContract.Effect.ShowError(getString(R.string.Buy_AddCard_Error_WrongDate)) }
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