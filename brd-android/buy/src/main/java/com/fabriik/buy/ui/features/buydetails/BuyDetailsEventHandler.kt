package com.fabriik.buy.ui.features.buydetails

import com.fabriik.common.ui.base.FabriikEventHandler

interface BuyDetailsEventHandler: FabriikEventHandler<BuyDetailsContract.Event> {

    override fun handleEvent(event: BuyDetailsContract.Event) {
        return when (event) {
            BuyDetailsContract.Event.LoadData -> loadData()
            BuyDetailsContract.Event.BackClicked -> backClicked()
            BuyDetailsContract.Event.DismissClicked -> dismissClicked()
            BuyDetailsContract.Event.OrderIdClicked -> orderIdClicked()
            BuyDetailsContract.Event.TransactionIdClicked -> transactionIdClicked()
        }
    }

    fun loadData()

    fun backClicked()

    fun dismissClicked()

    fun orderIdClicked()

    fun transactionIdClicked()
}