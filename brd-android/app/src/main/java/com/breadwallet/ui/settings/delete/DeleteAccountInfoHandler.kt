package com.breadwallet.ui.settings.delete

import com.spotify.mobius.Connection
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.F
import com.breadwallet.util.errorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DeleteAccountInfoHandler : Connection<F>, CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Default + errorHandler()

    override fun accept(value: F) {
        when (value) {
        }
    }

    override fun dispose() {
        coroutineContext.cancel()
    }
}