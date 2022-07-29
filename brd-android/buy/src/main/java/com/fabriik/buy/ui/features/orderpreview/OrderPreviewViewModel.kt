package com.fabriik.buy.ui.features.orderpreview

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class OrderPreviewViewModel(
    application: Application
) : FabriikViewModel<OrderPreviewContract.State, OrderPreviewContract.Event, OrderPreviewContract.Effect>(
    application
) {
    override fun createInitialState() = OrderPreviewContract.State

    override fun handleEvent(event: OrderPreviewContract.Event) {
        when (event) {
            OrderPreviewContract.Event.OnBackPressed ->
                setEffect { OrderPreviewContract.Effect.Back }

            OrderPreviewContract.Event.OnDismissClicked ->
                setEffect { OrderPreviewContract.Effect.Dismiss }

            OrderPreviewContract.Event.OnCreditInfoClicked ->
                setEffect { OrderPreviewContract.Effect.ShowInfoDialog(DialogType.CREDIT_CARD_FEE) }

            OrderPreviewContract.Event.OnNetworkInfoClicked ->
                setEffect { OrderPreviewContract.Effect.ShowInfoDialog(DialogType.NETWORK_FEE) }

            OrderPreviewContract.Event.OnTermsAndConditionsCLicked -> TODO()

            OrderPreviewContract.Event.OnConfirmClicked -> TODO()
        }
    }
}