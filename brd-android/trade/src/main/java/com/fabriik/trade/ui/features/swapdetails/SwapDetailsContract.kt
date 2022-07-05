package com.fabriik.trade.ui.features.swapdetails

import com.fabriik.common.ui.base.FabriikContract
import org.jetbrains.annotations.Contract

interface SwapDetailsContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
    }

    data class State(
        val swapState: SwapStatus
    ) : FabriikContract.State
}

enum class SwapStatus {
    PENDING,
    COMPLETE,
    FAILED
}