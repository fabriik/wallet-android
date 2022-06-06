package com.fabriik.kyc.ui.features.submitphoto

import android.net.Uri
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType

interface SubmitPhotoContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object RetakeClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object PostValidation : Effect()
        data class TakePhoto(
            val documentSide: DocumentSide,
            val documentType: DocumentType
        ) : Effect()
    }

    data class State(
        val documentType: DocumentType,
        val documentSide: DocumentSide,
        val image: Uri
    ) : FabriikContract.State
}