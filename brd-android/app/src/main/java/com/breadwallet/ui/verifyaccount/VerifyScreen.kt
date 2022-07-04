package com.breadwallet.ui.verifyaccount

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget

object VerifyScreen {

    object M

    sealed class E {
        object OnVerifyClicked : E()
        object OnDismissClicked : E()
    }

    sealed class F {
        object GoToAccountVerification : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToKyc
        }

        object Dismiss : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Home
        }
    }
}