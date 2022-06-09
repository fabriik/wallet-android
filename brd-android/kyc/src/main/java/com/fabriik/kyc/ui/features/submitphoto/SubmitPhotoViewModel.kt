package com.fabriik.kyc.ui.features.submitphoto

import android.app.Application
import androidx.core.net.toFile
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.data.KycApi
import com.fabriik.kyc.data.model.DocumentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class SubmitPhotoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle

) : FabriikViewModel<SubmitPhotoContract.State, SubmitPhotoContract.Event, SubmitPhotoContract.Effect>(
    application, savedStateHandle
) {

    private val kycApi = KycApi.create()
    private lateinit var arguments: SubmitPhotoFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = SubmitPhotoFragmentArgs.fromBundle(savedStateHandle.toBundle())
        super.parseArguments(savedStateHandle)
    }

    override fun createInitialState() = SubmitPhotoContract.State(
        currentData = arguments.currentData,
        documentType = arguments.documentType,
        documentData = arguments.documentData
    )

    override fun handleEvent(event: SubmitPhotoContract.Event) {
        when (event) {
            is SubmitPhotoContract.Event.BackClicked,
            is SubmitPhotoContract.Event.RetakeClicked ->
                setEffect { SubmitPhotoContract.Effect.Back }

            is SubmitPhotoContract.Event.DismissClicked ->
                setEffect { SubmitPhotoContract.Effect.Dismiss }

            is SubmitPhotoContract.Event.ConfirmClicked ->
                onConfirmClicked()
        }
    }

    private fun onConfirmClicked() {
        val documentData = currentState.documentData.toMutableList()
        documentData.add(currentState.currentData)

        when (currentState.documentType) {
            DocumentType.SELFIE ->
                uploadSelfie(documentData)

            DocumentType.PASSPORT ->
                uploadPassport(documentData)

            else -> when (documentData.size) {
                ONE_PHOTO -> setEffect {
                    SubmitPhotoContract.Effect.TakePhoto(
                        documentType = currentState.documentType,
                        documentData = documentData.toTypedArray()
                    )
                }
                TWO_PHOTOS -> uploadOtherDocuments(documentData)
            }
        }
    }

    private fun uploadOtherDocuments(documentData: List<DocumentData>) {
        // todo: validation

        uploadFile(
            type = TYPE_ID,
            documentData = documentData
        ) {
            setEffect {
                SubmitPhotoContract.Effect.TakePhoto(
                    documentType = DocumentType.SELFIE,
                    documentData = emptyArray()
                )
            }
        }
    }

    private fun uploadPassport(documentData: List<DocumentData>) {
        // todo: validation

        uploadFile(
            type = TYPE_ID,
            documentData = documentData
        ) {
            setEffect {
                SubmitPhotoContract.Effect.TakePhoto(
                    documentType = DocumentType.SELFIE,
                    documentData = emptyArray()
                )
            }
        }
    }

    private fun uploadSelfie(documentData: List<DocumentData>) {
        // todo: validation

        uploadFile(
            type = TYPE_SELFIE,
            documentData = documentData
        ) {
            setEffect {
                SubmitPhotoContract.Effect.PostValidation
            }
        }
    }

    private fun uploadFile(type: String, documentData: List<DocumentData>, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = kycApi.uploadPhotos(
                type = type,
                documentData = documentData,
                documentType = currentState.documentType
            )

            when (response.status) {
                Status.SUCCESS -> callback()
                Status.ERROR -> {}
            }
        }
    }

    companion object {
        const val ONE_PHOTO = 1
        const val TWO_PHOTOS = 2
        const val TYPE_ID = "ID"
        const val TYPE_SELFIE = "SELFIE"
    }
}