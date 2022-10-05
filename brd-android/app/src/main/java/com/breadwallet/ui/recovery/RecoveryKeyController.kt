/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/13/19.
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
package com.breadwallet.ui.recovery

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.breadwallet.R
import com.breadwallet.app.BreadApp
import com.breadwallet.databinding.ControllerRecoveryKeyBinding
import com.fabriik.common.utils.showErrorState
import com.breadwallet.tools.animation.BRDialog
import com.breadwallet.tools.animation.SpringAnimator
import com.breadwallet.tools.util.Utils
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.controllers.AlertDialogController
import com.breadwallet.ui.recovery.RecoveryKey.DIALOG_ACCOUNT_DELETED
import com.breadwallet.ui.recovery.RecoveryKey.DIALOG_WIPE
import com.breadwallet.ui.recovery.RecoveryKey.DIALOG_WIPE_NEGATIVE
import com.breadwallet.ui.recovery.RecoveryKey.DIALOG_WIPE_POSITIVE
import com.breadwallet.ui.recovery.RecoveryKey.E
import com.breadwallet.ui.recovery.RecoveryKey.F
import com.breadwallet.ui.recovery.RecoveryKey.M
import com.breadwallet.util.DefaultTextWatcher
import com.breadwallet.util.registerForGenericDialogResult
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.breadwallet.util.registerForGenericDialogResult
import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer
import drewcarlson.mobius.flow.FlowTransformer
import org.kodein.di.direct
import org.kodein.di.erased.instance

class RecoveryKeyController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args),
    AlertDialogController.Listener {

    constructor(
        mode: RecoveryKey.Mode,
        phrase: String? = null
    ) : this(
        bundleOf("mode" to mode.name)
    ) {
        launchPhrase = phrase
        if (launchPhrase != null) {
            eventConsumer.accept(E.OnNextClicked)
        }
    }

    private var launchPhrase: String? = null
    private val mode = arg("mode", RecoveryKey.Mode.RECOVER.name)

    override val windowSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

    override val defaultModel
        get() = M.createWithOptionalPhrase(
            mode = RecoveryKey.Mode.valueOf(mode),
            phrase = launchPhrase
        )
    override val update = RecoveryKeyUpdate
    override val flowEffectHandler: FlowTransformer<F, E>
        get() = createRecoveryKeyHandler(
            applicationContext as BreadApp,
            direct.instance(),
            direct.instance(),
            direct.instance()
        )

    private val binding by viewBinding(ControllerRecoveryKeyBinding::inflate)

    private val wordInputs: List<TextInputEditText>
        get() = with(binding) {
            listOf(
                etWord1, etWord2, etWord3,
                etWord4, etWord5, etWord6,
                etWord7, etWord8, etWord9,
                etWord10, etWord11, etWord12
            )
        }

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        val resources = resources!!

        // TODO: This needs a better home
        if (Utils.isUsingCustomInputMethod(applicationContext)) {
            BRDialog.showCustomDialog(
                activity!!,
                resources.getString(R.string.JailbreakWarnings_title),
                resources.getString(R.string.Alert_customKeyboard_android),
                resources.getString(R.string.Button_ok),
                resources.getString(R.string.JailbreakWarnings_close),
                { brDialogView ->
                    val imeManager =
                        applicationContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imeManager.showInputMethodPicker()
                    brDialogView.dismissWithAnimation()
                },
                { brDialogView -> brDialogView.dismissWithAnimation() },
                null,
                0
            )
        }

        registerForGenericDialogResult(DIALOG_WIPE) {resultKey, _ ->
            when (resultKey) {
                DIALOG_WIPE_POSITIVE -> eventConsumer.accept(E.OnWipeWalletConfirmed)
                DIALOG_WIPE_NEGATIVE -> eventConsumer.accept(E.OnWipeWalletCancelled)
            }
        }
    }

    override fun bindView(output: Consumer<E>): Disposable {
        val resources = resources!!
        with(binding) {
            when (currentModel.mode) {
                RecoveryKey.Mode.WIPE -> {
                    tvSubtitle.text = resources.getString(R.string.RecoveryKeyFlow_enterRecoveryKey)
                    tvDescription.text = resources.getString(R.string.WipeWallet_instruction)
                }
                RecoveryKey.Mode.DELETE_ACCOUNT -> {
                    tvSubtitle.text = resources.getString(R.string.RecoverWallet_header_delete_account)
                    tvDescription.text = resources.getString(R.string.RecoverWallet_subheader_delete_account)

                    registerForGenericDialogResult(DIALOG_ACCOUNT_DELETED) { _, _ ->
                        eventConsumer.accept(E.OnDeleteAccountDialogDismissed)
                    }
                }
                RecoveryKey.Mode.RESET_PIN -> {
                    tvSubtitle.text = resources.getString(R.string.RecoveryKeyFlow_enterRecoveryKey)
                    tvDescription.text =
                        resources.getString(R.string.RecoverWallet_subheader_reset_pin)
                }
                RecoveryKey.Mode.RECOVER -> Unit
            }

            toolbar.setShowBackButton(true)
            toolbar.setShowDismissButton(currentModel.mode == RecoveryKey.Mode.DELETE_ACCOUNT)
            btnFaq.isVisible = currentModel.mode != RecoveryKey.Mode.DELETE_ACCOUNT

            btnFaq.setOnClickListener {
                output.accept(E.OnFaqClicked)
            }

            btnNext.setOnClickListener {
                Utils.hideKeyboard(activity)
                output.accept(E.OnNextClicked)
            }

            toolbar.setBackButtonClickListener {
                output.accept(E.OnBackClicked)
            }

            toolbar.setDismissButtonClickListener {
                output.accept(E.OnDismissClicked)
            }

            btnContactSupport.setOnClickListener {
                output.accept(E.OnContactSupportClicked)
            }
        }

        // Bind keyboard enter event
        wordInputs.last().setOnEditorActionListener { _, actionId, event ->
            if (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                output.accept(E.OnNextClicked)
            }
            false
        }

        // Bind word input focus event
        wordInputs.forEachIndexed { index, input ->
            input.setOnFocusChangeListener { _, focused ->
                if (focused)
                    output.accept(E.OnFocusedWordChanged(index))
            }
        }

        wordInputs.zip(currentModel.phrase)
            .forEach { (input, word) ->
                input.setText(word, TextView.BufferType.EDITABLE)
            }

        // Bind word input text event
        val watchers = wordInputs.mapIndexed { index, input ->
            createTextWatcher(output, index, input)
        }

        return Disposable {
            wordInputs.zip(watchers)
                .forEach { (input, watcher) ->
                    input.removeTextChangedListener(watcher)
                }
        }
    }

    override fun M.render() {
        ifChanged(M::isLoading) {
            // TODO: Show loading msg
            binding.loadingView.root.isVisible = it
        }

        ifChanged(M::showContactSupport) {
            binding.btnContactSupport.isVisible = it
        }

        ifChanged(M::errors) { errors ->
            wordInputs.zip(errors)
                .forEach { (input, error) ->
                    (input.parent.parent as TextInputLayout).showErrorState(error)
                }
        }
    }

    override fun handleViewEffect(effect: ViewEffect) {
        when (effect) {
            is F.ErrorShake -> SpringAnimator.failShakeAnimation(applicationContext, view)
            is F.WipeWallet -> activity?.getSystemService<ActivityManager>()?.clearApplicationUserData()
        }
    }

    /** Creates a recovery word input text watcher and attaches it to [input]. */
    private fun createTextWatcher(
        output: Consumer<E>,
        index: Int,
        input: EditText
    ) = object : DefaultTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            val word = s?.toString() ?: ""
            output.accept(E.OnWordChanged(index, word))
        }
    }.also(input::addTextChangedListener)

    override fun handleBack(): Boolean = currentModel.isLoading
}
