package com.fabriik.trade.ui.features.swap

import android.app.Application
import com.breadwallet.tools.security.ProfileManager
import com.fabriik.common.ui.base.FabriikViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
), KodeinAware {

    override fun createInitialState() = SwapInputContract.State

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {

        }
    }
}