package com.fabriik.kyc.ui.features.accountverification

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.data.enums.AccountVerificationStatus
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView

class AccountVerificationViewModel(
    application: Application
) : FabriikViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(
    application
) {

    override fun createInitialState() = AccountVerificationContract.State(
        level1State = AccountVerificationContract.Level1State(
            isEnabled = true,
            statusState = AccountVerificationStatusView.StatusViewState.Verified
        ),
        level2State = AccountVerificationContract.Level2State(
            isEnabled = true,
            statusState = AccountVerificationStatusView.StatusViewState.Declined,
            verificationError = "Test error message"
        )
    )

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect { AccountVerificationContract.Effect.GoBack }

            is AccountVerificationContract.Event.BasicClicked ->
                setEffect { AccountVerificationContract.Effect.GoToPersonalInfo }

            is AccountVerificationContract.Event.UnlimitedClicked ->
                setEffect { AccountVerificationContract.Effect.GoToProofOfIdentity }
        }
    }
}