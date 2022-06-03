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

    override fun createInitialState() : AccountVerificationContract.State {
        val status = AccountVerificationStatus.DEFAULT

        return AccountVerificationContract.State(
            level1State = mapStatusToLevel1State(status),
            level2State = mapStatusToLevel2State(status)
        )
    }

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect { AccountVerificationContract.Effect.GoBack }

            is AccountVerificationContract.Event.Level1Clicked ->
                setEffect { AccountVerificationContract.Effect.GoToPersonalInfo }

            is AccountVerificationContract.Event.Level2Clicked -> {} //todo
        }
    }

    private fun mapStatusToLevel1State(status: AccountVerificationStatus): AccountVerificationContract.Level1State {
        val state = when (status) {
            AccountVerificationStatus.DEFAULT -> null
            else -> AccountVerificationStatusView.StatusViewState.Verified
        }

        return AccountVerificationContract.Level1State(
            isEnabled = true,
            statusState = state
        )
    }

    private fun mapStatusToLevel2State(status: AccountVerificationStatus): AccountVerificationContract.Level2State {
        return when (status) {
            AccountVerificationStatus.DEFAULT -> AccountVerificationContract.Level2State(
                isEnabled = false,
                statusState = null
            )
            AccountVerificationStatus.LEVEL1_VERIFIED -> AccountVerificationContract.Level2State(
                isEnabled = true
            )
            AccountVerificationStatus.LEVEL2_DECLINED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Declined,
                verificationError = "Test error message"
            )
            AccountVerificationStatus.LEVEL2_RESUBMIT -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Resubmit,
                verificationError = "Test error message"
            )
            AccountVerificationStatus.LEVEL2_PENDING -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Pending
            )
            AccountVerificationStatus.LEVEL2_VERIFIED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Verified
            )
        }
    }
}