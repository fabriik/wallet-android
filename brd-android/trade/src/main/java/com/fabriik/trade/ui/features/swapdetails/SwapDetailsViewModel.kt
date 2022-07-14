package com.fabriik.trade.ui.features.swapdetails

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

class SwapDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) :
    FabriikViewModel<SwapDetailsContract.State, SwapDetailsContract.Event, SwapDetailsContract.Effect>(
        application, savedStateHandle
    ), KodeinAware {

    override val kodein by closestKodein { application }

    private val swapApi by kodein.instance<SwapApi>()

    private lateinit var arguments: SwapDetailsFragmentArgs

    private val currentLoadedState: SwapDetailsContract.State.Loaded?
        get() = state.value as SwapDetailsContract.State.Loaded?

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = SwapDetailsFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = SwapDetailsContract.State.Loading

    override fun handleEvent(event: SwapDetailsContract.Event) {
        when (event) {
            SwapDetailsContract.Event.LoadData ->
                loadData()

            SwapDetailsContract.Event.DismissClicked ->
                setEffect { SwapDetailsContract.Effect.Dismiss }

            SwapDetailsContract.Event.OrderIdClicked -> {
                val state = currentLoadedState ?: return
                setEffect { SwapDetailsContract.Effect.CopyToClipboard(state.data.orderId) }
            }

            SwapDetailsContract.Event.TransactionIdClicked -> {
                val state = currentLoadedState ?: return
                setEffect { SwapDetailsContract.Effect.CopyToClipboard(state.data.orderId) } //todo: transaction id
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
                        setState { SwapDetailsContract.State.Loaded(requireNotNull(it.data)) }

                    Status.ERROR -> {
                        setState { SwapDetailsContract.State.Error }
                        setEffect {
                            SwapDetailsContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                    }
                }
            }
        )
    }
}