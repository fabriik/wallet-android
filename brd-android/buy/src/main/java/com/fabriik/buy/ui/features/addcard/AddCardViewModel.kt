package com.fabriik.buy.ui.addcard

import android.app.Application
import com.fabriik.buy.R
import com.fabriik.buy.ui.formatCardNumber
import com.fabriik.buy.ui.formatDate
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString

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
                setEffect { AddCardContract.Effect.BillingAddress("test_token") } //todo: call checkout SDK

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