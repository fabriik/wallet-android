package com.breadwallet.ui.resetpin

import com.breadwallet.tools.util.BRConstants
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import dev.zacsweers.redacted.annotations.Redacted

private const val PIN_LENGTH = 6

object ResetPinInput {

    data class M(
        val mode: Mode = Mode.NEW,
        @Redacted val pin: String = "",
        @Redacted val pinConfirmation: String = ""
    ) {

        companion object {
            fun createDefault() = M()
        }

        enum class Mode {
            NEW,     // Choose a new pin
            CONFIRM  // Confirm the new pin
        }
    }

    sealed class E {

        object OnFaqClicked : E()
        object OnPinLocked : E()
        object OnPinSaved : E()
        object OnPinSaveFailed : E()

        data class OnPinEntered(
            @Redacted val pin: String,
            val isPinCorrect: Boolean
        ) : E()
    }

    sealed class F {

        data class SetupPin(
            @Redacted val pin: String
        ) : F() {
            init {
                require(pin.length == PIN_LENGTH) {
                    "pin must contain $PIN_LENGTH digits"
                }
            }
        }

        object ResetPin : F(), ViewEffect
        object ErrorShake : F(), ViewEffect
        object ShowPinError : F(), ViewEffect

        object GoToResetCompleted : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.PinResetCompleted
        }

        object GoToDisabledScreen : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.DisabledScreen
        }

        object GoToFaq : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.SupportPage(BRConstants.FAQ_SET_PIN)
        }

        data class TrackEvent(val event: String) : F()
    }
}
