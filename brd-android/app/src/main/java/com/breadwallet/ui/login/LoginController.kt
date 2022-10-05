/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 10/10/19.
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
package com.breadwallet.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.breadwallet.R
import com.breadwallet.databinding.ControllerLoginBinding
import com.breadwallet.legacy.presenter.customviews.PinLayout
import com.breadwallet.tools.animation.SpringAnimator
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.auth.AuthenticationController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.login.LoginScreen.E
import com.breadwallet.ui.login.LoginScreen.F
import com.breadwallet.ui.login.LoginScreen.M
import com.fabriik.common.utils.FabriikToastUtil
import drewcarlson.mobius.flow.FlowTransformer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import org.kodein.di.direct
import org.kodein.di.erased.instance

private const val EXTRA_URL = "PENDING_URL"
private const val EXTRA_SHOW_HOME = "SHOW_HOME"

class LoginController(args: Bundle? = null) :
    BaseMobiusController<M, E, F>(args),
    AuthenticationController.Listener {

    constructor(intentUrl: String?) : this(
        bundleOf(EXTRA_URL to intentUrl)
    )

    constructor(showHome: Boolean) : this(
        bundleOf(EXTRA_SHOW_HOME to showHome)
    )

    override val defaultModel = M.createDefault(
        arg(EXTRA_URL, ""),
        arg(EXTRA_SHOW_HOME, true)
    )
    override val update = LoginUpdate
    override val init = LoginInit
    override val flowEffectHandler: FlowTransformer<F, E>
        get() = createLoginScreenHandler(
            checkNotNull(applicationContext),
            direct.instance()
        )

    private val binding by viewBinding(ControllerLoginBinding::inflate)

    private val biometricPrompt by resetOnViewDestroy {
        BiometricPrompt(
            activity as AppCompatActivity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    eventConsumer.accept(E.OnAuthenticationSuccess)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // Defer to pin authentication on error
                    if (errorCode != BiometricPrompt.ERROR_CANCELED) {
                        eventConsumer.accept(E.OnAuthenticationFailed(null))
                    }
                }

                // Ignored: only handle final events from onAuthenticationError
                override fun onAuthenticationFailed() = Unit
            }
        )
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            modelFlow.map { it.fingerprintEnable }
                .distinctUntilChanged()
                .onEach { fingerprintEnable ->
                    btnFingerprint.isVisible = fingerprintEnable
                }
                .launchIn(uiBindScope)
            merge(
                btnFingerprint.clicks().map { E.OnFingerprintClicked },
                tvReset.clicks().map { E.OnResetPinClicked },
                pinDigits.bindInput()
            )
        }
    }

    private fun PinLayout.bindInput() = callbackFlow<E> {
        val channel = channel
        val pinListener = object : PinLayout.PinLayoutListener {
            override fun onPinLocked() {
                channel.offer(E.OnPinLocked)
            }

            override fun onValidPinInserted(pin: String) {
                channel.offer(E.OnAuthenticationSuccess)
            }

            override fun onInvalidPinInserted(pin: String, attemptsLeft: Int) {
                binding.pinDigits.resetPin()
                channel.offer(E.OnAuthenticationFailed(attemptsLeft))
            }
        }
        setup(binding.keyboard, true, pinListener)
        awaitClose { cleanUp() }
    }

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        with(binding) {
            keyboard.setShowDecimal(false)
            keyboard.setDeleteButtonBackgroundColor(resources!!.getColor(android.R.color.transparent))
            keyboard.setDeleteImage(R.drawable.ic_delete_black)

            val pinDigitButtonColor = ContextCompat.getColor(root.context, R.color.fabriik_black)
            keyboard.setButtonTextColor(pinDigitButtonColor)
        }
    }

    override fun handleBack() =
        (router.backstackSize > 1 && !currentModel.isUnlocked) ||
            activity?.isTaskRoot == false

    override fun handleViewEffect(effect: ViewEffect) {
        when (effect) {
            F.AuthenticationSuccess -> unlockWallet()
            is F.AuthenticationFailed -> {
                val attemptsLeft = effect.attemptsLeft ?: return
                if (attemptsLeft <= 0) return

                val attemptsText = resources!!.getQuantityText(R.plurals.attempts, attemptsLeft)

                FabriikToastUtil.showError(
                    parentView = binding.root,
                    message = resources!!.getString(
                        R.string.LoginController_invalidPinError, attemptsLeft, attemptsText
                    )
                )
            }
            F.ShowFingerprintController -> {
                biometricPrompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(resources!!.getString(R.string.UnlockScreen_touchIdTitle_android))
                        .setNegativeButtonText(resources!!.getString(R.string.Prompts_TouchId_usePin_android))
                        .build()
                )
            }
        }
    }

    override fun onAuthenticationSuccess() {
        super.onAuthenticationSuccess()
        eventConsumer.accept(E.OnAuthenticationSuccess)
    }

    private fun unlockWallet() {
        eventConsumer.accept(E.OnUnlockAnimationEnd)
    }
}
