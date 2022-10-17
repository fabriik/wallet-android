package com.fabriik.kyc.ui.features.submitphoto

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.R
import com.fabriik.kyc.data.KycApi
import com.fabriik.kyc.data.model.DocumentData

class SubmitPhotoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle

) : FabriikViewModel<SubmitPhotoContract.State, SubmitPhotoContract.Event, SubmitPhotoContract.Effect>(
    application, savedStateHandle
), SubmitPhotoEventHandler {

    private val kycApi = KycApi.create(application.applicationContext)
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

    override fun onBackClicked() {
        setEffect { SubmitPhotoContract.Effect.Back }
    }

    override fun onRetakeClicked() {
        setEffect { SubmitPhotoContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { SubmitPhotoContract.Effect.Dismiss }
    }

    override fun onConfirmClicked() {
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
        if (documentData.size != TWO_PHOTOS) {
            setEffect {
                SubmitPhotoContract.Effect.ShowToast(
                    getString(R.string.Api_DefaultError)
                )
            }
            return
        }

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
        if (documentData.size != ONE_PHOTO) {
            setEffect {
                SubmitPhotoContract.Effect.ShowToast(
                    getString(R.string.Api_DefaultError)
                )
            }
            return
        }

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
        if (documentData.size != ONE_PHOTO) {
            setEffect {
                SubmitPhotoContract.Effect.ShowToast(
                    getString(R.string.Api_DefaultError)
                )
            }
            return
        }

        uploadFile(
            type = TYPE_SELFIE,
            documentData = documentData
        ) {
            submitPhotosForVerification()
        }
    }

    private fun uploadFile(type: String, documentData: List<DocumentData>, callback: () -> Unit) {
        callApi(
            endState = { copy(loadingVisible = false) },
            startState = { copy(loadingVisible = true) },
            action = {
                kycApi.uploadPhotos(
                    type = type,
                    documentData = documentData,
                    documentType = currentState.documentType
                )
            },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        callback()

                    Status.ERROR ->
                        setEffect {
                            SubmitPhotoContract.Effect.ShowToast(
                                it.message ?: getString(R.string.Api_DefaultError)
                            )
                        }
                }
            }
        )
    }

    private fun submitPhotosForVerification() {
        callApi(
            endState = { copy(loadingVisible = false) },
            startState = { copy(loadingVisible = true) },
            action = { kycApi.submitPhotosForVerification() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setEffect { SubmitPhotoContract.Effect.PostValidation }

                    Status.ERROR -> setEffect {
                        SubmitPhotoContract.Effect.ShowToast(
                            it.message ?: getString(R.string.Api_DefaultError)
                        )
                    }
                }
            }
        )
    }

    companion object {
        const val ONE_PHOTO = 1
        const val TWO_PHOTOS = 2
        const val TYPE_ID = "ID"
        const val TYPE_SELFIE = "SELFIE"
    }
}