package com.fabriik.common.ui.base

interface FabriikView<State: FabriikContract.State, Effect: FabriikContract.Effect> {
    fun render(state: State)
    fun handleEffect(effect: Effect)
}