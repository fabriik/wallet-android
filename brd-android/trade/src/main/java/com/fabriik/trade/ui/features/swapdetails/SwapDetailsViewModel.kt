package com.fabriik.trade.ui.features.swapdetails

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class SwapDetailsViewModel(
    application: Application
) :
    FabriikViewModel<SwapDetailsContract.State, SwapDetailsContract.Event, SwapDetailsContract.Effect>(
        application
    ) {
    override fun createInitialState(): SwapDetailsContract.State =
        SwapDetailsContract.State(
            status = SwapStatus.PENDING,
            orderId = "39246726y89e1ruhut7e3xy78e2xuih7y7y8y8y8y2782yx78x8382643j21",
            swapFromCurrency = "BSV",
            swapToCurrency = "BTC",
            swapFromID = "39246726y89e1ruhut7e3xy78e1xg17gx71x2xuih7y7y8y8y8y2782yx78x8382643j21",
            swapToId = "Pending",
            timestamp = "22 Feb 2022, 1:29pm",
            swapFromCurrencyValue = "50 / \$2,859.00 USD",
            swapToCurrencyValue = "0.095 / \$2,857.48 USD"
        )

    override fun handleEvent(event: SwapDetailsContract.Event) {
        when (event) {
            SwapDetailsContract.Event.DismissClicked ->
                setEffect { SwapDetailsContract.Effect.Dismiss }

            SwapDetailsContract.Event.OrderIdClicked ->
                setEffect { SwapDetailsContract.Effect.CopyOrderId }

            SwapDetailsContract.Event.TransactionIdClicked ->
                setEffect { SwapDetailsContract.Effect.CopyTransactionId }
        }
    }
}