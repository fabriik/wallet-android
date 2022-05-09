package com.fabriik.kyc.ui.features.proofofidentityupload

import com.fabriik.common.ui.base.FabriikContract

interface UploadProofOfIdentityContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object AddPassportClicked : Event()
        object AddIdFrontClicked : Event()
        object AddIdBackClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToAddress : Effect()
    }

    data class State(
        val description: CharSequence,
        val confirmEnabled: Boolean = false,
        val addIdPhotosVisible: Boolean,
        val addPassportPhotoVisible: Boolean
    ) : FabriikContract.State
}