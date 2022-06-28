package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {

    }

    sealed class Effect : FabriikContract.Effect {

    }

    object State : FabriikContract.State
}