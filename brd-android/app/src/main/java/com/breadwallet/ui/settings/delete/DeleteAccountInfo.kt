/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 10/25/19.
 * Copyright (c) 2019 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui.settings.delete

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.recovery.RecoveryKey

object DeleteAccountInfo {

    data class M(
        val checkboxEnable: Boolean = false,
        val continueEnabled: Boolean = false
    ) {

        companion object {
            fun createDefault(): M {
                return M()
            }
        }
    }

    sealed class E {
        object OnDismissClicked : E()
        object OnContinueClicked : E()
        data class OnCheckboxChanged(val enable: Boolean) : E()
    }

    sealed class F {
        sealed class Nav(
            override val navigationTarget: NavigationTarget
        ) : F(), NavigationEffect {
            object GoBack : Nav(NavigationTarget.Back)
            object GoToRecoveryPhrase: Nav(NavigationTarget.GoToRecoveryKey(RecoveryKey.Mode.DELETE_ACCOUNT))
        }
    }
}
