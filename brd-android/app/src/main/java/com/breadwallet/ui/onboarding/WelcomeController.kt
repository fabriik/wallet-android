package com.breadwallet.ui.onboarding

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
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

            buttonRecoverWallet.paintFlags = buttonRecoverWallet.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
    }
}
