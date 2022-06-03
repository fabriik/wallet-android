package com.fabriik.kyc.ui.features.accountverification

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView

interface AccountVerificationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object BasicClicked : Event()
        object UnlimitedClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object GoToPersonalInfo : Effect()
        object GoToProofOfIdentity : Effect()
    }

    data class State(
        val level1State: Level1State,
        val level2State: Level2State
    ) : FabriikContract.State

    data class Level1State(
        val isEnabled: Boolean = false,
        val statusState: AccountVerificationStatusView.StatusViewState? = null,
    ) : FabriikContract.State

    data class Level2State(
        val isEnabled: Boolean = false,
        val statusState: AccountVerificationStatusView.StatusViewState? = null,
        val verificationError: String? = null,
    ) : FabriikContract.State
}