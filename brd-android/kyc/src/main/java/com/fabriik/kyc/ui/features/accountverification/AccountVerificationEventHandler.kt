package com.fabriik.kyc.ui.features.accountverification

import com.fabriik.common.ui.base.FabriikEventHandler

interface AccountVerificationEventHandler: FabriikEventHandler<AccountVerificationContract.Event> {

    override fun handleEvent(event: AccountVerificationContract.Event) {
        return when (event) {
            is AccountVerificationContract.Event.BackClicked -> onBackClicked()
            is AccountVerificationContract.Event.DismissClicked -> onDismissClicked()
            is AccountVerificationContract.Event.InfoClicked -> onInfoClicked()
            is AccountVerificationContract.Event.Level1Clicked -> onLevel1Clicked()
            is AccountVerificationContract.Event.Level2Clicked -> onLevel2Clicked()
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onInfoClicked()

    fun onLevel1Clicked()

    fun onLevel2Clicked()
}