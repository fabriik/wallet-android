package com.fabriik.kyc.ui.features.exposedperson

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class ExposedPersonViewModel(
    application: Application
) : FabriikViewModel<ExposedPersonContract.State, ExposedPersonContract.Event, ExposedPersonContract.Effect>(application) {

    override fun createInitialState() = ExposedPersonContract.State()

    override fun handleEvent(event: ExposedPersonContract.Event) {
        when (event) {
            is ExposedPersonContract.Event.ConfirmClicked ->
                setEffect {
                    ExposedPersonContract.Effect.Dismiss
                }
        }
    }
}