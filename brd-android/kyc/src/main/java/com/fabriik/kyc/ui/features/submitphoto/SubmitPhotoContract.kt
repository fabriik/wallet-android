package com.fabriik.kyc.ui.features.submitphoto

import android.net.Uri
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.data.model.DocumentData

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
        data class ShowToast(
            val message: String
        ) : Effect()
        data class TakePhoto(
            val documentData: Array<DocumentData>,
            val documentType: DocumentType
        ) : Effect()
    }

    data class State(
        val loadingVisible: Boolean = false,
        val currentData: DocumentData,
        val documentType: DocumentType,
        val documentData: Array<DocumentData>
    ) : FabriikContract.State
}