package com.fabriik.kyc.ui.features.personalinformation

import com.fabriik.common.ui.base.FabriikContract

interface PersonalInformationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
        object ScreenInfoClicked : Event()
        object ExposedPersonInfoClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}