package com.fabriik.kyc.ui.features.accountverification

import androidx.annotation.StringRes
import com.fabriik.common.ui.base.FabriikContract

interface AccountVerificationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object InfoClicked : Event()
        object BasicClicked : Event()
        object UnlimitedClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object GoToPersonalInfo : Effect()
        object GoToProofOfIdentity : Effect()
        class ShowInfo(@StringRes val title: Int, @StringRes val description: Int) : Effect()
    }

    data class State(
        val basicBoxEnabled: Boolean = false,
        val unlimitedBoxEnabled: Boolean = false
    ) : FabriikContract.State
}