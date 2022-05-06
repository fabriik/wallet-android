package com.fabriik.kyc.ui.features.completed

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class CompletedViewModel(
    application: Application
) : FabriikViewModel<CompletedContract.State, CompletedContract.Event, CompletedContract.Effect>(application) {

    override fun createInitialState() = CompletedContract.State()

    override fun handleEvent(event: CompletedContract.Event) {
        when (event) {
            is CompletedContract.Event.GotItClicked ->
                setEffect {
                    CompletedContract.Effect.Dismiss
                }
        }
    }
}