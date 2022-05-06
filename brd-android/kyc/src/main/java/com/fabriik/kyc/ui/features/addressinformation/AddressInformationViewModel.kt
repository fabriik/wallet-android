package com.fabriik.kyc.ui.features.addressinformation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class AddressInformationViewModel(
    application: Application
) : FabriikViewModel<AddressInformationContract.State, AddressInformationContract.Event, AddressInformationContract.Effect>(application) {

    override fun createInitialState() = AddressInformationContract.State()

    override fun handleEvent(event: AddressInformationContract.Event) {
        when (event) {
            is AddressInformationContract.Event.BackClicked ->
                setEffect {
                    AddressInformationContract.Effect.GoBack
                }

            is AddressInformationContract.Event.DismissClicked ->
                setEffect {
                    AddressInformationContract.Effect.Dismiss
                }

            is AddressInformationContract.Event.ConfirmClicked ->
                setEffect {
                    AddressInformationContract.Effect.GoToProofOfResidence
                }
        }
    }
}