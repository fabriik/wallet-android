/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 9/10/19.
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
package com.breadwallet.ui.home

import android.os.Bundle
import android.view.View
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadwallet.BuildConfig
import com.breadwallet.R
import com.breadwallet.databinding.ControllerHomeBinding
import com.breadwallet.databinding.VerifyPromptBinding
import com.breadwallet.legacy.presenter.customviews.BRButton
import com.breadwallet.legacy.presenter.customviews.BaseTextView
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.home.HomeScreen.E
import com.breadwallet.ui.home.HomeScreen.F
import com.breadwallet.ui.home.HomeScreen.M
import com.breadwallet.ui.home.HomeScreen.SUPPORT_FORM_DIALOG
import com.breadwallet.ui.home.HomeScreen.SUPPORT_FORM_DIALOG_POSITIVE
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.registerForGenericDialogResult
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericModelAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.direct
import org.kodein.di.erased.instance
import com.fabriik.registration.ui.RegistrationActivity

private const val NETWORK_TESTNET = "TESTNET"
private const val NETWORK_MAINNET = "MAINNET"

class HomeController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    override val defaultModel = M.createDefault()
    override val update = HomeScreenUpdate
    override val init = HomeScreenInit
    override val flowEffectHandler
        get() = createHomeScreenHandler(
            checkNotNull(applicationContext),
            direct.instance(),
            RatesRepository.getInstance(applicationContext!!),
            direct.instance(),
            direct.instance(),
            direct.instance(),
            direct.instance(),
            direct.instance(),
            direct.instance()
    )

    private val binding by viewBinding(ControllerHomeBinding::inflate)
    private var fastAdapter: GenericFastAdapter? = null
    private var walletAdapter: ModelAdapter<Wallet, WalletListItem>? = null
    private var addWalletAdapter: ItemAdapter<AddWalletItem>? = null
    private val userManager by instance<BrdUserManager>()

    override fun bindView(output: Consumer<E>): Disposable {
        return with (binding) {
            buyLayout.setOnClickListener { output.accept(E.OnBuyClicked) }
            menuLayout.setOnClickListener { output.accept(E.OnMenuClicked) }
            tradeLayout.setOnClickListener { output.accept(E.OnTradeClicked) }
            profileLayout.setOnClickListener { output.accept(E.OnProfileClicked) }

            val fastAdapter = checkNotNull(fastAdapter)
            fastAdapter.onClickListener = { _, _, item, _ ->
                val event = when (item) {
                    is AddWalletItem -> E.OnAddWalletsClicked
                    is WalletListItem -> E.OnWalletClicked(item.model.currencyCode)
                    else -> error("Unknown item clicked.")
                }
                output.accept(event)
                true
            }

            Disposable {
                fastAdapter.onClickListener = null
            }
        }
    }

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        setUpBuildInfoLabel()

        walletAdapter = ModelAdapter(::WalletListItem)
        addWalletAdapter = ItemAdapter()

        fastAdapter = FastAdapter.with(listOf(walletAdapter!!, addWalletAdapter!!))

        val dragCallback = SimpleDragCallback(DragEventHandler(fastAdapter!!, eventConsumer))
        val touchHelper = ItemTouchHelper(dragCallback)
        with(binding) {
            touchHelper.attachToRecyclerView(rvWalletList)

            rvWalletList.adapter = fastAdapter
            rvWalletList.itemAnimator = null
            rvWalletList.layoutManager = LinearLayoutManager(view.context)
        }

        addWalletAdapter!!.add(AddWalletItem())
        registerForActivityResult(RegistrationActivity.REQUEST_CODE)

        registerForGenericDialogResult(SUPPORT_FORM_DIALOG) { resultKey, _->
            when(resultKey) {
                SUPPORT_FORM_DIALOG_POSITIVE ->
                    eventConsumer.accept(E.OnPositiveDialogClicked)
            }
        }
    }

    override fun onDestroyView(view: View) {
        walletAdapter = null
        addWalletAdapter = null
        fastAdapter = null
        super.onDestroyView(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RegistrationActivity.REQUEST_CODE && resultCode == RegistrationActivity.RESULT_VERIFIED) {
            eventConsumer.accept(E.OnEmailVerified)
        }
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        modelFlow.distinctUntilChangedBy { it.wallets.values }
            .flowOn(Dispatchers.Default)
            .onEach { m -> walletAdapter?.setNewList(m.wallets.values.toList()) }
            .launchIn(uiBindScope)
        return emptyFlow()
    }

    override fun M.render() {
        with(binding) {
            ifChanged(M::aggregatedFiatBalance) {
                totalAssetsUsd.text = aggregatedFiatBalance.formatFiatForUi(BRSharedPrefs.getPreferredFiatIso())
            }

            ifChanged(M::showPrompt) {
                if (promptContainer.childCount > 0) {
                    promptContainer.removeAllViews()
                }
                if (showPrompt) {
                    val promptView = getPromptView(promptId!!)
                    promptContainer.addView(promptView, 0)
                }
            }

            ifChanged(M::hasInternet) {
                notificationBar.apply {
                    isGone = hasInternet
                    if (hasInternet) bringToFront()
                }
            }
        }
    }

    private fun setUpBuildInfoLabel() {
        val network = if (BuildConfig.BITCOIN_TESTNET) NETWORK_TESTNET else NETWORK_MAINNET
        val buildInfo = "$network ${BuildConfig.VERSION_NAME} build ${BuildConfig.BUILD_VERSION}"
        binding.testnetLabel.text = buildInfo
        binding.testnetLabel.isVisible = BuildConfig.BITCOIN_TESTNET || BuildConfig.DEBUG
    }

    private fun getPromptView(promptItem: PromptItem): View {
        val act = checkNotNull(activity)

        val baseLayout = act.layoutInflater.inflate(R.layout.base_prompt, null)
        val title = baseLayout.findViewById<BaseTextView>(R.id.prompt_title)
        val description = baseLayout.findViewById<BaseTextView>(R.id.prompt_description)
        val continueButton = baseLayout.findViewById<Button>(R.id.continue_button)
        val dismissButton = baseLayout.findViewById<ImageButton>(R.id.dismiss_button)
        dismissButton.setOnClickListener {
            eventConsumer.accept(E.OnPromptDismissed(promptItem))
        }
        when (promptItem) {
            PromptItem.FINGER_PRINT -> {
                title.text = act.getString(R.string.Prompts_TouchId_title_android)
                description.text = act.getString(R.string.Prompts_TouchId_body_android)
                continueButton.setOnClickListener {
                    eventConsumer.accept(E.OnFingerprintPromptClicked)
                }
            }
            PromptItem.PAPER_KEY -> {
                title.text = act.getString(R.string.Prompts_PaperKey_title)
                description.text = act.getString(R.string.Prompts_PaperKey_Body_Android)
                continueButton.setOnClickListener {
                    eventConsumer.accept(E.OnPaperKeyPromptClicked)
                }
            }
            PromptItem.UPGRADE_PIN -> {
                title.text = act.getString(R.string.Prompts_UpgradePin_title)
                description.text = act.getString(R.string.Prompts_UpgradePin_body)
                continueButton.setOnClickListener {
                    eventConsumer.accept(E.OnUpgradePinPromptClicked)
                }
            }
            PromptItem.RECOMMEND_RESCAN -> {
                title.text = act.getString(R.string.Prompts_RecommendRescan_title)
                description.text = act.getString(R.string.Prompts_RecommendRescan_body)
                continueButton.setOnClickListener {
                    eventConsumer.accept(E.OnRescanPromptClicked)
                }
            }
            PromptItem.RATE_APP -> return getRateAppPrompt()
            PromptItem.VERIFY_USER -> return getVerifyUserPrompt()
        }
        return baseLayout
    }

    private fun getVerifyUserPrompt(): View {
        val verifyPrompt = VerifyPromptBinding.inflate(LayoutInflater.from(activity))
        verifyPrompt.btnCancel.setOnClickListener {
            userManager.updateVerifyPrompt(false)
            eventConsumer.accept(E.OnPromptDismissed(PromptItem.VERIFY_USER))
        }

        verifyPrompt.btnVerifyAccount.setOnClickListener {
            eventConsumer.accept(E.OnProfileClicked)
            binding.promptContainer.removeAllViews()
        }

        return verifyPrompt.root
    }


    private fun getRateAppPrompt(): View {
        val act = checkNotNull(activity)
        val customLayout = act.layoutInflater.inflate(R.layout.rate_app_prompt, null)
        val negativeButton = customLayout.findViewById<BRButton>(R.id.negative_button)
        val positiveButton = customLayout.findViewById<BRButton>(R.id.submit_button)
        val closeButton = customLayout.findViewById<ImageView>(R.id.close_button)
        val dontShowCheckBox = customLayout.findViewById<CheckBox>(R.id.dont_show_checkbox)
        closeButton.setOnClickListener {
            eventConsumer.accept(E.OnPromptDismissed(PromptItem.RATE_APP))
        }
        negativeButton.setOnClickListener {
            eventConsumer.accept(E.OnPromptDismissed(PromptItem.RATE_APP))
            eventConsumer.accept(E.OnRateAppPromptNoThanksClicked)
        }
        positiveButton.setOnClickListener {
            eventConsumer.accept(E.OnRateAppPromptClicked)
        }
        dontShowCheckBox.setOnClickListener {
            eventConsumer.accept(E.OnRateAppPromptDontShowClicked((it as CheckBox).isChecked))
        }
        return customLayout
    }

    private class DragEventHandler(
        private val fastAdapter: GenericFastAdapter,
        private val output: Consumer<E>
    ) : ItemTouchCallback {

        fun isAddWallet(position: Int) = fastAdapter.getItem(position) is AddWalletItem

        override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
            if (isAddWallet(newPosition)) return false

            val adapter = fastAdapter.getAdapter(newPosition)
            check(adapter is GenericModelAdapter<*>)
            DragDropUtil.onMove(adapter, oldPosition, newPosition)

            output.accept(
                E.OnWalletDisplayOrderUpdated(
                    adapter.models
                        .filterIsInstance<Wallet>()
                        .map(Wallet::currencyId)
                )
            )
            return true
        }

        override fun itemTouchDropped(oldPosition: Int, newPosition: Int) = Unit
    }
}
