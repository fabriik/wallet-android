package com.breadwallet.ui.settings.delete

import com.breadwallet.databinding.ControllerDeleteAccountBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.checked
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.M
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.E
import com.breadwallet.ui.settings.delete.DeleteAccountInfo.F
import com.spotify.mobius.Connectable
import com.spotify.mobius.First.first
import com.spotify.mobius.Init
import kotlinx.coroutines.flow.*

class DeleteAccountInfoController: BaseMobiusController<M, E, F>() {

    private val binding by viewBinding(ControllerDeleteAccountBinding::inflate)

    override val defaultModel = M.createDefault()
    override val update = DeleteAccountInfoUpdate
    override val init: Init<M, F> = Init<M, F> { model -> first(model) }
    override val effectHandler = Connectable<F, E> { DeleteAccountInfoHandler() }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
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
