package com.breadwallet.ui.verifyaccount

import android.os.Bundle
import com.breadwallet.databinding.ControllerVerifyAccountBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.verifyaccount.VerifyScreen.E
import com.breadwallet.ui.verifyaccount.VerifyScreen.F
import com.breadwallet.ui.verifyaccount.VerifyScreen.M
import com.spotify.mobius.Connectable
import com.spotify.mobius.Init
import drewcarlson.mobius.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class VerifyController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    override val defaultModel = M
    override val update = VerifyUpdate
    override val init: Init<M, F> = Init<M, F> { model ->
        first(model)
    }
    override val effectHandler = Connectable<F, E> {
        VerifyScreenHandler()
    }

    private val binding by viewBinding(ControllerVerifyAccountBinding::inflate)

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            merge(
                btnVerifyAccount.clicks().map { E.OnVerifyClicked },
                btnDismiss.clicks().map { E.OnDismissClicked }
            )
        }
    }
}