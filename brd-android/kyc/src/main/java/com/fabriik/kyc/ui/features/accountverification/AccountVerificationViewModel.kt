package com.fabriik.kyc.ui.features.accountverification

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.Status
import com.fabriik.common.data.enums.KycStatus
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.R
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView
import com.fabriik.registration.data.RegistrationApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountVerificationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: AccountVerificationFragmentArgs
    private val registrationApi = RegistrationApi.create(application.applicationContext)

    init {
        if (arguments.profile == null) {
            loadProfile()
        }
    }

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = AccountVerificationFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = AccountVerificationContract.State(
        level1State = mapStatusToLevel1State(arguments.profile?.kycStatus),
        level2State = mapStatusToLevel2State(arguments.profile?.kycStatus),
        isLoading = arguments.profile == null
    )

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect { AccountVerificationContract.Effect.Back }

            is AccountVerificationContract.Event.DismissClicked ->
                setEffect { AccountVerificationContract.Effect.Dismiss }

            is AccountVerificationContract.Event.InfoClicked ->
                setEffect { AccountVerificationContract.Effect.Info }

            is AccountVerificationContract.Event.Level1Clicked ->
                setEffect { AccountVerificationContract.Effect.GoToKycLevel1 }

            is AccountVerificationContract.Event.Level2Clicked ->
                setEffect { AccountVerificationContract.Effect.GoToKycLevel2 }

            is AccountVerificationContract.Event.ProfileLoaded ->
                setState {
                    AccountVerificationContract.State(
                        level1State = mapStatusToLevel1State(event.profile.kycStatus),
                        level2State = mapStatusToLevel2State(event.profile.kycStatus),
                        isLoading = false
                    )
                }

            is AccountVerificationContract.Event.ProfileLoadFailed ->
                setEffect { AccountVerificationContract.Effect.ShowToast(event.message) }
        }
    }

    private fun mapStatusToLevel1State(status: KycStatus?): AccountVerificationContract.Level1State {
        val state = when (status) {
            KycStatus.DEFAULT,
            KycStatus.EMAIL_VERIFIED,
            KycStatus.EMAIL_VERIFICATION_PENDING,
            null -> null
            else -> AccountVerificationStatusView.StatusViewState.Verified
        }

        return AccountVerificationContract.Level1State(
            isEnabled = true,
            statusState = state
        )
    }

    private fun mapStatusToLevel2State(status: KycStatus?): AccountVerificationContract.Level2State {
        return when (status) {
            KycStatus.DEFAULT,
            KycStatus.EMAIL_VERIFIED,
            KycStatus.EMAIL_VERIFICATION_PENDING,
            null -> AccountVerificationContract.Level2State(
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

    private fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = registrationApi.getProfile()
            val event = when (response.status) {
                Status.SUCCESS -> AccountVerificationContract.Event.ProfileLoaded(response.data!!)
                Status.ERROR -> AccountVerificationContract.Event.ProfileLoadFailed(response.message!!)
            }
            setEvent(event)
        }
    }
}