package com.fabriik.buy.ui.features.buydetails

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class BuyDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) :
    FabriikViewModel<BuyDetailsContract.State, BuyDetailsContract.Event, BuyDetailsContract.Effect>(
        application, savedStateHandle
    ), KodeinAware {

    override val kodein by closestKodein { application }

    private val swapApi by kodein.instance<SwapApi>()

    private lateinit var arguments: BuyDetailsFragmentArgs

    private val currentLoadedState: BuyDetailsContract.State.Loaded?
        get() = state.value as BuyDetailsContract.State.Loaded?

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = BuyDetailsFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = BuyDetailsContract.State.Loading

    override fun handleEvent(event: BuyDetailsContract.Event) {
        when (event) {
            BuyDetailsContract.Event.LoadData ->
                loadData()

            BuyDetailsContract.Event.DismissClicked ->
                setEffect { BuyDetailsContract.Effect.Dismiss }

            BuyDetailsContract.Event.OrderIdClicked -> {
                val state = currentLoadedState ?: return
                setEffect { BuyDetailsContract.Effect.CopyToClipboard(state.data.orderId) }
            }

            BuyDetailsContract.Event.TransactionIdClicked -> {
                val transactionID = currentLoadedState?.data?.destination?.transactionId ?: return
                setEffect { BuyDetailsContract.Effect.CopyToClipboard(transactionID) }
            }
        }
    }

    private fun loadData() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { swapApi.getExchangeOrder(arguments.exchangeId) },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setState { BuyDetailsContract.State.Loaded(requireNotNull(it.data)) }

                    Status.ERROR -> {
                        setState { BuyDetailsContract.State.Error }
                        setEffect {
                            BuyDetailsContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                    }
                }
            }
        )
    }
}