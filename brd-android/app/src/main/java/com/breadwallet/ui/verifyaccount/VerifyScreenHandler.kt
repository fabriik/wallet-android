package com.breadwallet.ui.verifyaccount

import com.spotify.mobius.Connection
import com.breadwallet.ui.verifyaccount.VerifyScreen.F
import com.breadwallet.util.errorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class VerifyScreenHandler : Connection<F>, CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Default + errorHandler()

    override fun accept(value: F) {
        when (value) {
        }
    }

    override fun dispose() {
        coroutineContext.cancel()
    }
}