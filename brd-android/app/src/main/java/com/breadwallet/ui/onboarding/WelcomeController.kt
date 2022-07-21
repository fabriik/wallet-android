/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/12/19.
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
package com.breadwallet.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.breadwallet.R
import com.breadwallet.databinding.ControllerWelcomeBinding
import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.onboarding.OnBoarding.E
import com.breadwallet.ui.onboarding.OnBoarding.F
import com.breadwallet.ui.onboarding.OnBoarding.M
import org.kodein.di.direct
import org.kodein.di.erased.instance

class WelcomeController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    override val defaultModel = M.DEFAULT
    override val init = OnBoardingInit
    override val update = OnBoardingUpdate

    override val flowEffectHandler
        get() = createOnBoardingHandler(direct.instance())

    private val binding by viewBinding(ControllerWelcomeBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        with(binding) {
            buttonRecoverWallet.setOnClickListener {
                EventUtils.pushEvent(EventUtils.EVENT_LANDING_PAGE_RESTORE_WALLET)
                router.pushController(
                    RouterTransaction.with(IntroRecoveryController())
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
                )
            }
            buttonNewWallet.setOnClickListener {
                eventConsumer.accept(E.OnBrowseClicked)
            }
        }
    }
}
