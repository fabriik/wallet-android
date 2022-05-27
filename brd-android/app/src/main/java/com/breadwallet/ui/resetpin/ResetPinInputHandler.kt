package com.breadwallet.ui.resetpin

import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.resetpin.ResetPinInput.E
import com.breadwallet.ui.resetpin.ResetPinInput.F
import drewcarlson.mobius.flow.subtypeEffectHandler

fun createInputPinHandler(
    userManager: BrdUserManager
) = subtypeEffectHandler<F, E> {
    addFunction<F.SetupPin> { effect ->
        try {
            userManager.configurePinCode(effect.pin)
            E.OnPinSaved
        } catch (e: Exception) {
            E.OnPinSaveFailed
        }
    }

    addConsumer<F.TrackEvent> { (event) ->
        EventUtils.pushEvent(event)
    }
}

