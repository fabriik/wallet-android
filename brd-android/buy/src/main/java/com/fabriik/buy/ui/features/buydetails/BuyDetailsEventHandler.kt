package com.fabriik.buy.ui.features.buydetails

import com.fabriik.common.ui.base.FabriikEventHandler

interface BuyDetailsEventHandler: FabriikEventHandler<BuyDetailsContract.Event> {

    override fun handleEvent(event: BuyDetailsContract.Event) {
        return when (event) {
            is BuyDetailsContract.Event.LoadData -> onLoadData()
            is BuyDetailsContract.Event.BackClicked -> onBackClicked()
            is BuyDetailsContract.Event.DismissClicked -> onDismissClicked()
            is BuyDetailsContract.Event.OrderIdClicked -> onOrderIdClicked()
            is BuyDetailsContract.Event.TransactionIdClicked -> onTransactionIdClicked()
        }
    }

    fun onLoadData()

    fun onBackClicked()

    fun onDismissClicked()

    fun onOrderIdClicked()

    fun onTransactionIdClicked()
}