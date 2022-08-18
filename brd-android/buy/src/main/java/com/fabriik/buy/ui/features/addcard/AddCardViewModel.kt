package com.fabriik.buy.ui.addcard

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.checkout.android_sdk.CheckoutAPIClient
import com.checkout.android_sdk.Request.CardTokenisationRequest
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.Utils.Environment
import com.checkout.android_sdk.network.NetworkError
import com.fabriik.buy.R
import com.fabriik.buy.ui.formatCardNumber
import com.fabriik.buy.ui.formatDate
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCardViewModel(
    application: Application
) : FabriikViewModel<AddCardContract.State, AddCardContract.Event, AddCardContract.Effect>(
    application
) {

    override fun createInitialState() = AddCardContract.State()

    override fun handleEvent(event: AddCardContract.Event) {
        when (event) {
            AddCardContract.Event.BackClicked ->
                setEffect { AddCardContract.Effect.Back }

            AddCardContract.Event.DismissClicked ->
                setEffect { AddCardContract.Effect.Dismiss }

            AddCardContract.Event.ConfirmClicked ->
                onConfirmClicked()

            AddCardContract.Event.SecurityCodeInfoClicked -> {} //todo: show info dialog

            is AddCardContract.Event.OnCardNumberChanged ->
                setState {
                    copy(cardNumber = formatCardNumber(event.number))
                }

            is AddCardContract.Event.OnDateChanged -> {
                val formatDate = formatDate(event.date)

                setState { copy(date = formatDate) }

                if (formatDate?.length == 5) {
                    validateDate(formatDate)
                }
            }
        }
    }

    private fun onConfirmClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy() } //todo: show loading

            val checkoutClient = CheckoutAPIClient(
                getApplication(), "pk_sbox_ees63clhrko6kta6j3cwloebg4#", Environment.SANDBOX
            )

            checkoutClient.setTokenListener(object : CheckoutAPIClient.OnTokenGenerated {
                override fun onTokenGenerated(response: CardTokenisationResponse) {
                    setState { copy() } //todo: dismiss loading
                    setEffect { AddCardContract.Effect.BillingAddress(response.token) }
                }

                override fun onError(error: CardTokenisationFail?) {
                    setState { copy() } //todo: dismiss loading
                    TODO("Not yet implemented")
                }

                override fun onNetworkError(error: NetworkError?) {
                    setState { copy() } //todo: dismiss loading
                    TODO("Not yet implemented")
                }
            })

            checkoutClient.generateToken(
                CardTokenisationRequest(
                    "4242424242424242",
                    "name",
                    "06",
                    "25",
                    "100"
                )
            )
        }
    }

    private fun validateDate(input: String?) {
        if (input == null) {
            return
        }

        val splitDate = if (input.length > 3) {
            input.split("/")
        } else return

        if (splitDate[0].toInt() > 12) {
            setEffect { AddCardContract.Effect.ShowToast(getString(R.string.Buy_AddCard_Error_WrongDate)) }
        }
    }
}