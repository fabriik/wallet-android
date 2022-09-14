/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 10/29/19.
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

import android.view.View
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.breadwallet.databinding.ControllerDeleteAccountBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.checked
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.M
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.E
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.F
import kotlinx.coroutines.flow.*

class DeleteAccountInfoController: BaseMobiusController<M, E, F>() {

    private val binding by viewBinding(ControllerDeleteAccountBinding::inflate)

    override val defaultModel = M.createDefault()
    override val update = DeleteAccountInfoUpdate
    override val init = DeleteAccountInfoInit

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        modelFlow
            .map { it.checkboxEnable }
            .distinctUntilChanged()
            .onEach { isChecked ->
                binding.cbConfirmation.isChecked = isChecked
            }
            .launchIn(uiBindScope)

        return with(binding) {
            merge(
                btnClose.clicks().map { E.OnDismissClicked },
                btnContinue.clicks().map { E.OnContinueClicked },
                cbConfirmation.checked().map { E.OnCheckboxChanged(it) }
            )
        }
    }

    override fun M.render() {
        with(binding) {
            ifChanged(M::continueEnabled) {
                btnContinue.isEnabled = it
            }

            ifChanged(M::checkboxEnable) {
                cbConfirmation.isChecked = it
            }
        }
    }
}
