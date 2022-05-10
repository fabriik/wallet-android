package com.fabriik.kyc.ui.features.accountverification

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.R

class AccountVerificationViewModel(
    application: Application
) : FabriikViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(
    application
) {

    override fun createInitialState() = AccountVerificationContract.State(
        unlimitedBoxEnabled = true // todo: set from API
    )

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect { AccountVerificationContract.Effect.GoBack }

            is AccountVerificationContract.Event.BasicClicked ->
                setEffect { AccountVerificationContract.Effect.GoToPersonalInfo }

            is AccountVerificationContract.Event.UnlimitedClicked ->
                setEffect { AccountVerificationContract.Effect.GoToProofOfIdentity }

            is AccountVerificationContract.Event.InfoClicked ->
                setEffect {
                    AccountVerificationContract.Effect.ShowInfo(
                        title = R.string.AccountVerification_InfoTitle,
                        description = R.string.AccountVerification_InfoDescription
                    )
                }
        }
    }
}