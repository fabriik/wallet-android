package com.fabriik.buy.ui.addcard

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class AddCardViewModel(
    application: Application
) : FabriikViewModel<AddCardContract.State, AddCardContract.Event, AddCardContract.Effect>(
    application
) {

    override fun createInitialState() = AddCardContract.State

    override fun handleEvent(event: AddCardContract.Event) {
        when (event) {
            AddCardContract.Event.OnBackClicked ->
                setEffect { AddCardContract.Effect.Back }

            AddCardContract.Event.OnDismissClicked ->
                setEffect { AddCardContract.Effect.Dismiss }

            AddCardContract.Event.OnConfirmClicked ->
                setEffect { AddCardContract.Effect.Confirm }
        }
    }
}