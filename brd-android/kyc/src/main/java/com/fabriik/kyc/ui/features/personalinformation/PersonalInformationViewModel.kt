package com.fabriik.kyc.ui.features.personalinformation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PersonalInformationViewModel(
    application: Application
) : FabriikViewModel<PersonalInformationContract.State, PersonalInformationContract.Event, PersonalInformationContract.Effect>(application) {

    override fun createInitialState() = PersonalInformationContract.State()

    override fun handleEvent(event: PersonalInformationContract.Event) {
        when (event) {
            is PersonalInformationContract.Event.BackClicked ->
                setEffect {
                    PersonalInformationContract.Effect.GoBack
                }

            is PersonalInformationContract.Event.DismissClicked ->
                setEffect {
                    PersonalInformationContract.Effect.Dismiss
                }

            is PersonalInformationContract.Event.ConfirmClicked ->
                setEffect {
                    PersonalInformationContract.Effect.GoToExposedPerson
                }
        }
    }
}