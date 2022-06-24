package com.breadwallet.ui.verifyaccount

import com.breadwallet.ui.verifyaccount.VerifyScreen.M
import com.breadwallet.ui.verifyaccount.VerifyScreen.E
import com.breadwallet.ui.verifyaccount.VerifyScreen.F
import com.spotify.mobius.Next

interface VerifyScreenUpdateSpec {
    fun patch(model: M, event: E): Next<M, F> = when (event) {
        is E.OnVerifyClicked -> onVerifyClicked(model)
    }

    fun onVerifyClicked(model: M): Next<M, F>
}