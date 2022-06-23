package com.fabriik.kyc.ui.features.accountverification

import com.fabriik.common.data.model.Profile
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView

interface AccountVerificationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object InfoClicked : Event()
        object Level1Clicked : Event()
        object Level2Clicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Info : Effect()
        object Dismiss : Effect()
        object GoToKycLevel1 : Effect()
        object GoToKycLevel2 : Effect()
        object ShowLevel1ChangeConfirmationDialog : Effect()
        data class ShowToast(val message: String) : Effect()
    }

    sealed class State : FabriikContract.State {

        data class Empty(
            val isLoading: Boolean = true
        ) : State()

        data class Content(
            val profile: Profile,
            val level1State: Level1State,
            val level2State: Level2State,
        ) : State()
    }

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