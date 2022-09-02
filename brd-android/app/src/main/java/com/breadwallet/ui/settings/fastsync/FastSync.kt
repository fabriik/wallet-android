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
package com.breadwallet.ui.settings.fastsync

import com.breadwallet.R
import com.breadwallet.model.SyncMode
import com.breadwallet.tools.util.BRConstants
import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.util.CurrencyCode
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs

object FastSync {

    const val DIALOG_DISABLE_FAST_SYNC = "confirm_disable_fast_sync_dialog"
    const val DIALOG_DISABLE_FAST_SYNC_POSITIVE = "confirm_disable_fast_sync_dialog_positive"
    const val DIALOG_DISABLE_FAST_SYNC_NEGATIVE = "confirm_disable_fast_sync_dialog_negative"

    data class M(
        val currencyCode: CurrencyCode,
        val fastSyncEnable: Boolean = false,
        val currencyId: String = ""
    ) {

        companion object {
            fun createDefault(currencyCode: CurrencyCode): M {
                return M(currencyCode)
            }
        }
    }

    sealed class E {
        object OnBackClicked : E()
        object OnLearnMoreClicked : E()
        object OnDisableFastSyncConfirmed : E()
        object OnDisableFastSyncCanceled : E()
        data class OnFastSyncChanged(val enable: Boolean) : E()
        data class OnSyncModesUpdated(val modeMap: Map<String, SyncMode>) : E()
        data class OnCurrencyIdsUpdated(val currencyMap: Map<String, String>) : E()
    }

    sealed class F {
        object LoadCurrencyIds : F()
        object ShowDisableFastSyncDialog : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.FabriikGenericDialog(
                FabriikGenericDialogArgs(
                    descriptionRes = R.string.WalletConnectionSettings_confirmation,
                    positive = FabriikGenericDialogArgs.ButtonData(
                        titleRes = R.string.WalletConnectionSettings_turnOff,
                        resultKey = DIALOG_DISABLE_FAST_SYNC_POSITIVE
                    ),
                    negative = FabriikGenericDialogArgs.ButtonData(
                        titleRes = R.string.Button_cancel,
                        resultKey = DIALOG_DISABLE_FAST_SYNC_NEGATIVE
                    ),
                    requestKey = DIALOG_DISABLE_FAST_SYNC
                )
            )
        }

        sealed class Nav(
            override val navigationTarget: NavigationTarget
        ) : F(), NavigationEffect {
            object GoBack : Nav(NavigationTarget.Back)

            object GoToFaq : Nav(NavigationTarget.SupportPage(BRConstants.FAQ_FASTSYNC))
        }

        sealed class MetaData : F() {
            data class SetSyncMode(
                val currencyId: String,
                val mode: SyncMode
            ) : MetaData()

            object LoadSyncModes : MetaData()
        }
    }
}
