package com.fabriik.kyc.ui.features.submitphoto

import android.net.Uri
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentType

interface SubmitPhotoContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object RetakeClicked : Event()
        object ConfirmClicked : Event()

        data class OnCreate(
            val documentType: DocumentType,
            val image: Uri,
        ) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object GoToCamera : Effect()
        object GoForward : Effect()
    }

    data class State(
        val documentType: DocumentType? = null,
        val image: Uri? = null,
    ) : FabriikContract.State
}