package com.fabriik.trade.ui.features.authentication

import android.app.Application
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.isFingerPrintAvailableAndSetup
import com.fabriik.common.ui.base.FabriikViewModel

class SwapAuthenticationViewModel(
    application: Application
) : FabriikViewModel<SwapAuthenticationContract.State, SwapAuthenticationContract.Event, SwapAuthenticationContract.Effect>(
    application
) {

    override fun createInitialState(): SwapAuthenticationContract.State {
        val isFingerprintEnabled = isFingerprintEnabled()
        val authMode by lazy {
            val biometricManager = BiometricManager.from(getApplication())
            val hasBiometrics =
                biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            if (isFingerprintEnabled && hasBiometrics == BiometricManager.BIOMETRIC_SUCCESS) {
                SwapAuthenticationContract.AuthMode.USER_PREFERRED
            } else {
                SwapAuthenticationContract.AuthMode.PIN_REQUIRED
            }
        }

        return SwapAuthenticationContract.State(
            isFingerprintEnabled = isFingerprintEnabled,
            authMode = authMode
        )
    }

    override fun handleEvent(event: SwapAuthenticationContract.Event) {
        when (event) {
            SwapAuthenticationContract.Event.BackClicked ->
                setEffect { SwapAuthenticationContract.Effect.Back(RESULT_KEY_CANCELED) }

            SwapAuthenticationContract.Event.DismissClicked ->
                setEffect { SwapAuthenticationContract.Effect.Dismiss }

            is SwapAuthenticationContract.Event.PinValidated ->
                setEffect {
                    when (event.valid) {
                        true -> SwapAuthenticationContract.Effect.Back(RESULT_KEY_SUCCESS)
                        false -> SwapAuthenticationContract.Effect.ShakeError
                    }
                }

            is SwapAuthenticationContract.Event.AuthFailed -> when (currentState.authMode) {
                SwapAuthenticationContract.AuthMode.USER_PREFERRED ->
                    setState {
                        copy(authMode = SwapAuthenticationContract.AuthMode.PIN_REQUIRED)
                    }
                else ->
                    setEffect {
                        SwapAuthenticationContract.Effect.Back(
                            if (event.errorCode == BiometricPrompt.ERROR_CANCELED) {
                                RESULT_KEY_CANCELED
                            } else {
                                RESULT_KEY_FAILURE
                            }
                        )
                    }
            }

            SwapAuthenticationContract.Event.AuthSucceeded ->
                setEffect { SwapAuthenticationContract.Effect.Back(RESULT_KEY_SUCCESS) }
        }
    }

    private fun isFingerprintEnabled(): Boolean {
        return isFingerPrintAvailableAndSetup(getApplication())
                && BRSharedPrefs.sendMoneyWithFingerprint
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val REQUEST_KEY = "req_key_swap_auth"
        const val RESULT_KEY_SUCCESS = "res_key_swap_auth_success"
        const val RESULT_KEY_FAILURE = "res_key_swap_auth_failure"
        const val RESULT_KEY_CANCELED = "res_key_swap_auth_canceled"
    }
}