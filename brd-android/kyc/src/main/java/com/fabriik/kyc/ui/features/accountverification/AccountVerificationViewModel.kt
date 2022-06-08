package com.fabriik.kyc.ui.features.accountverification

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.data.enums.KycStatus
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView

class AccountVerificationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: AccountVerificationFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = AccountVerificationFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = AccountVerificationContract.State(
        level1State = mapStatusToLevel1State(arguments.profile.kycStatus),
        level2State = mapStatusToLevel2State(arguments.profile.kycStatus)
    )

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect { AccountVerificationContract.Effect.GoBack }

            is AccountVerificationContract.Event.Level1Clicked ->
                setEffect { AccountVerificationContract.Effect.GoToKycLevel1 }

            is AccountVerificationContract.Event.Level2Clicked ->
                setEffect { AccountVerificationContract.Effect.GoToKycLevel2 }
        }
    }

    private fun mapStatusToLevel1State(status: KycStatus): AccountVerificationContract.Level1State {
        val state = when (status) {
            KycStatus.DEFAULT,
            KycStatus.EMAIL_VERIFIED,
            KycStatus.EMAIL_VERIFICATION_PENDING -> null
            else -> AccountVerificationStatusView.StatusViewState.Verified
        }

        return AccountVerificationContract.Level1State(
            isEnabled = true,
            statusState = state
        )
    }

    private fun mapStatusToLevel2State(status: KycStatus): AccountVerificationContract.Level2State {
        return when (status) {
            KycStatus.DEFAULT,
            KycStatus.EMAIL_VERIFIED,
            KycStatus.EMAIL_VERIFICATION_PENDING -> AccountVerificationContract.Level2State(
                isEnabled = false,
                statusState = null
            )

            KycStatus.KYC_BASIC,
            KycStatus.KYC_UNLIMITED_EXPIRED -> AccountVerificationContract.Level2State(
                isEnabled = true
            )

            KycStatus.KYC_UNLIMITED_DECLINED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Declined,
                verificationError = "Test error message"
            )

            KycStatus.KYC_UNLIMITED_RESUBMISSION_REQUESTED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Resubmit,
                verificationError = "Test error message"
            )

            KycStatus.KYC_UNLIMITED_SUBMITTED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Pending
            )
            KycStatus.KYC_UNLIMITED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Verified
            )
        }
    }
}