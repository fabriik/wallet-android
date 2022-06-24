package com.breadwallet.ui.resetpin

import com.spotify.mobius.Next

interface ResetPinInputUpdateSpec {
    fun patch(model: ResetPinInput.M, event: ResetPinInput.E): Next<ResetPinInput.M, ResetPinInput.F> = when (event) {
        ResetPinInput.E.OnFaqClicked -> onFaqClicked(model)
        ResetPinInput.E.OnPinLocked -> onPinLocked(model)
        ResetPinInput.E.OnPinSaved -> onPinSaved(model)
        ResetPinInput.E.OnPinSaveFailed -> onPinSaveFailed(model)
        is ResetPinInput.E.OnPinEntered -> onPinEntered(model, event)
    }

    fun onFaqClicked(model: ResetPinInput.M): Next<ResetPinInput.M, ResetPinInput.F>

    fun onPinLocked(model: ResetPinInput.M): Next<ResetPinInput.M, ResetPinInput.F>

    fun onPinSaved(model: ResetPinInput.M): Next<ResetPinInput.M, ResetPinInput.F>

    fun onPinSaveFailed(model: ResetPinInput.M): Next<ResetPinInput.M, ResetPinInput.F>

    fun onPinEntered(model: ResetPinInput.M, event: ResetPinInput.E.OnPinEntered): Next<ResetPinInput.M, ResetPinInput.F>
}