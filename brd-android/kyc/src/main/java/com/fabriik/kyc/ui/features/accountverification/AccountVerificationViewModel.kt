package com.fabriik.kyc.ui.features.accountverification

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.ProfileManager
import com.fabriik.common.data.enums.KycStatus
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.kyc.R
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class AccountVerificationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) :
    FabriikViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(
        application, savedStateHandle
    ), KodeinAware {

    override val kodein by closestKodein { application }
    private val profileManager by kodein.instance<ProfileManager>()

    override fun createInitialState() = AccountVerificationContract.State.Loading

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect { AccountVerificationContract.Effect.Back }

            is AccountVerificationContract.Event.DismissClicked ->
                setEffect { AccountVerificationContract.Effect.Dismiss }

            is AccountVerificationContract.Event.InfoClicked ->
                setEffect { AccountVerificationContract.Effect.Info }

            is AccountVerificationContract.Event.Level1Clicked -> {
                when (currentState) {
                    is AccountVerificationContract.State.Content ->
                        navigateOnLevel1Clicked()
                }
            }


            is AccountVerificationContract.Event.Level2Clicked ->
                when (currentState) {
                    is AccountVerificationContract.State.Content ->
                        navigateOnLevel2Clicked()
                }
        }
    }

    private fun navigateOnLevel1Clicked() {
        val state = currentState as AccountVerificationContract.State.Content
        setEffect {
            when (state.profile.kycStatus) {
                KycStatus.DEFAULT,
                KycStatus.EMAIL_VERIFIED,
                KycStatus.EMAIL_VERIFICATION_PENDING,
                KycStatus.KYC1,
                KycStatus.KYC2_EXPIRED,
                KycStatus.KYC2_DECLINED,
                KycStatus.KYC2_RESUBMISSION_REQUESTED ->
                    AccountVerificationContract.Effect.GoToKycLevel1

                KycStatus.KYC2_SUBMITTED ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountVerification_Level2_PendingVerification)
                    )

                KycStatus.KYC2 ->
                    AccountVerificationContract.Effect.ShowLevel1ChangeConfirmationDialog
            }
        }
    }

    private fun navigateOnLevel2Clicked() {
        val state = currentState as AccountVerificationContract.State.Content
        setEffect {
            when (state.profile.kycStatus) {
                KycStatus.DEFAULT,
                KycStatus.EMAIL_VERIFIED,
                KycStatus.EMAIL_VERIFICATION_PENDING ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountVerification_Level2_CompleteLevel1First)
                    )


                KycStatus.KYC1,
                KycStatus.KYC2_EXPIRED,
                KycStatus.KYC2_DECLINED,
                KycStatus.KYC2_RESUBMISSION_REQUESTED ->
                    AccountVerificationContract.Effect.GoToKycLevel2

                KycStatus.KYC2_SUBMITTED ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountVerification_Level2_PendingVerification)
                    )

                KycStatus.KYC2 ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountVerification_Level2_UpdateLevel1IfDataChanged)
                    )
            }
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
            KycStatus.EMAIL_VERIFICATION_PENDING ->
                AccountVerificationContract.Level2State(
                isEnabled = false,
                statusState = null
            )

            KycStatus.KYC1,
            KycStatus.KYC2_EXPIRED -> AccountVerificationContract.Level2State(
                isEnabled = true
            )

            KycStatus.KYC2_DECLINED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Declined,
                verificationError = getString(R.string.FabriikApi_DefaultError)  // todo: read from API
            )

            KycStatus.KYC2_RESUBMISSION_REQUESTED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Resubmit,
                verificationError = getString(R.string.FabriikApi_DefaultError) // todo: read from API
            )

            KycStatus.KYC2_SUBMITTED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Pending
            )
            KycStatus.KYC2 -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Verified
            )
        }
    }

    fun updateProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profileManager.updateProfile().collect { profile ->
                if (profile != null) {
                    setState {
                        AccountVerificationContract.State.Content(
                            profile = profile,
                            level1State = mapStatusToLevel1State(profile.kycStatus),
                            level2State = mapStatusToLevel2State(profile.kycStatus),
                        )
                    }
                }
            }
        }
    }
}