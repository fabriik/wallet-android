/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 9/23/19.
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
package com.breadwallet.ui.resetpin

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.breadwallet.R
import com.breadwallet.databinding.ControllerResetPinInputBinding
import com.breadwallet.legacy.presenter.customviews.PinLayout
import com.breadwallet.legacy.presenter.customviews.PinLayout.PinLayoutListener
import com.breadwallet.tools.animation.SpringAnimator
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.resetpin.ResetPinInput.E
import com.breadwallet.ui.resetpin.ResetPinInput.F
import com.breadwallet.ui.resetpin.ResetPinInput.M
import drewcarlson.mobius.flow.FlowTransformer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.kodein.di.direct
import org.kodein.di.erased.instance

class ResetPinInputController(args: Bundle? = null) : BaseMobiusController<M, E, F>(args) {

    override val defaultModel = M.createDefault()
    override val update = ResetPinInputUpdate
    override val flowEffectHandler: FlowTransformer<F, E>
        get() = createInputPinHandler(direct.instance())

    private val binding by viewBinding(ControllerResetPinInputBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)

        val pinDigitButtonColor = ContextCompat.getColor(view.context, R.color.fabriik_black)
        binding.keyboard.setButtonTextColor(pinDigitButtonColor)
        binding.keyboard.setShowDecimal(false)
        binding.keyboard.setDeleteImage(R.drawable.ic_delete_black)
    }

    override fun handleViewEffect(effect: ViewEffect) {
        when (effect) {
            F.ErrorShake -> SpringAnimator.failShakeAnimation(applicationContext, binding.pinDigits)
            F.ShowPinError -> toastLong(R.string.UpdatePin_setPinError)
        }
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return merge(
            binding.btnFaq.clicks().map { E.OnFaqClicked },
            binding.pinDigits.bindInput()
        )
    }

    private fun PinLayout.bindInput() = callbackFlow<E> {
        val channel = channel
        setup(binding.keyboard, object : PinLayoutListener {
            override fun onInvalidPinInserted(pin: String, attemptsLeft: Int) {
                channel.offer(E.OnPinEntered(pin, false))
            }

            override fun onValidPinInserted(pin: String) {
                channel.offer(E.OnPinEntered(pin, true))
            }

            override fun onPinLocked() {
                channel.offer(E.OnPinLocked)
            }
        })
        awaitClose { cleanUp() }
    }

    override fun M.render() {
        ifChanged(M::mode) {
            binding.tvSubtitle.setText(
                when (mode) {
                    M.Mode.NEW -> R.string.ResetPin_subtitleNewPin
                    M.Mode.CONFIRM -> R.string.ResetPin_subtitleConfirmNewPin
                }
            )
        }
    }
}
