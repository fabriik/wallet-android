package com.fabriik.kyc.ui.features.accountverification

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class AccountVerificationViewModel(
    application: Application
) : FabriikViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(application) {

    override fun createInitialState() = AccountVerificationContract.State()

    override fun handleEvent(event: AccountVerificationContract.Event) {
        when (event) {
            is AccountVerificationContract.Event.BackClicked ->
                setEffect {
                    AccountVerificationContract.Effect.GoBack
                }

            is AccountVerificationContract.Event.BasicClicked ->
                setEffect {
                    AccountVerificationContract.Effect.GoToPersonalInfo
                }

            is AccountVerificationContract.Event.UnlimitedClicked -> {
                //todo: define flow
            }

            is AccountVerificationContract.Event.InfoClicked -> {
                //todo: show dialog?
            }
        }
    }
}