/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/14/20.
 * Copyright (c) 2020 breadwallet LLC
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

import com.spotify.mobius.Next

interface DeleteAccountInfoUpdateSpec {
    fun patch(model: DeleteAccountInfo.M, event: DeleteAccountInfo.E): Next<DeleteAccountInfo.M, DeleteAccountInfo.F> = when (event) {
        DeleteAccountInfo.E.OnDismissClicked -> onDismissClicked(model)
        DeleteAccountInfo.E.OnContinueClicked -> onContinueClicked(model)
        is DeleteAccountInfo.E.OnCheckboxChanged -> onCheckboxChanged(model, event)
    }

    fun onDismissClicked(model: DeleteAccountInfo.M): Next<DeleteAccountInfo.M, DeleteAccountInfo.F>

    fun onContinueClicked(model: DeleteAccountInfo.M): Next<DeleteAccountInfo.M, DeleteAccountInfo.F>

    fun onCheckboxChanged(model: DeleteAccountInfo.M, event: DeleteAccountInfo.E.OnCheckboxChanged): Next<DeleteAccountInfo.M, DeleteAccountInfo.F>
}