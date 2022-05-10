package com.fabriik.kyc.ui.features.proofofidentityupload

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.bold
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.common.utils.toSpannableString
import com.fabriik.kyc.R
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.ui.dialogs.PhotoSourcePickerBottomSheet

class UploadProofOfIdentityViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<UploadProofOfIdentityContract.State, UploadProofOfIdentityContract.Event, UploadProofOfIdentityContract.Effect>(
    application, savedStateHandle
) {
    private lateinit var arguments: UploadProofOfIdentityFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = UploadProofOfIdentityFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = UploadProofOfIdentityContract.State(
        description = createDescriptionForDocument(arguments.documentType),
        addIdPhotosVisible = arguments.documentType != DocumentType.PASSPORT,
        addPassportPhotoVisible = arguments.documentType == DocumentType.PASSPORT
    )

    override fun handleEvent(event: UploadProofOfIdentityContract.Event) {
        when (event) {
            is UploadProofOfIdentityContract.Event.BackClicked ->
                setEffect { UploadProofOfIdentityContract.Effect.GoBack }

            is UploadProofOfIdentityContract.Event.DismissClicked ->
                setEffect { UploadProofOfIdentityContract.Effect.Dismiss }

            is UploadProofOfIdentityContract.Event.ConfirmClicked ->
                setEffect { UploadProofOfIdentityContract.Effect.GoToAddress }

            is UploadProofOfIdentityContract.Event.AddIdBackClicked ->
                setEffect {
                    UploadProofOfIdentityContract.Effect.OpenPhotoSourcePicker(
                        REQUEST_KEY_BACK_SIDE
                    )
                }

            is UploadProofOfIdentityContract.Event.AddIdFrontClicked ->
                setEffect {
                    UploadProofOfIdentityContract.Effect.OpenPhotoSourcePicker(
                        REQUEST_KEY_FRONT_SIDE
                    )
                }

            is UploadProofOfIdentityContract.Event.AddPassportClicked ->
                setEffect {
                    UploadProofOfIdentityContract.Effect.OpenPhotoSourcePicker(
                        REQUEST_KEY_PASSPORT
                    )
                }

            is UploadProofOfIdentityContract.Event.CameraPermissionGranted ->
                setEffect {
                    UploadProofOfIdentityContract.Effect.OpenCamera(
                        requestKey = event.requestKey,
                        fileName = event.requestKey
                    )
                }

            is UploadProofOfIdentityContract.Event.PhotoSourceSelected -> {
                when (event.resultKey) {
                    PhotoSourcePickerBottomSheet.RESULT_CAMERA ->
                        setEffect {
                            UploadProofOfIdentityContract.Effect.RequestCameraPermission(
                                requestKey = event.requestKey
                            )
                        }

                    PhotoSourcePickerBottomSheet.RESULT_GALLERY ->
                        setEffect { UploadProofOfIdentityContract.Effect.OpenGallery(event.requestKey) }
                }
            }

            is UploadProofOfIdentityContract.Event.PhotoReady ->
                setState {
                    when (event.requestCode) {
                        REQUEST_KEY_PASSPORT -> copy(passportImage = event.imageUri).validate()
                        REQUEST_KEY_BACK_SIDE -> copy(idBackImage = event.imageUri).validate()
                        REQUEST_KEY_FRONT_SIDE -> copy(idFrontImage = event.imageUri).validate()
                        else -> this
                    }
                }
        }
    }

    private fun createDescriptionForDocument(documentType: DocumentType): CharSequence {
        val documentName = getString(
            when (documentType) {
                DocumentType.ID_CARD -> R.string.UploadProofOfIdentity_Description_IdCard
                DocumentType.PASSPORT -> R.string.UploadProofOfIdentity_Description_PassportPage
                DocumentType.DRIVING_LICENCE -> R.string.UploadProofOfIdentity_Description_DrivingLicence
            }
        )

        val description = getString(
            R.string.UploadProofOfIdentity_Description, documentName
        )

        val spannableString = description.toSpannableString()
        val startIndex = description.indexOf(documentName)
        val endIndex = startIndex + documentName.length
        return spannableString.bold(startIndex, endIndex)
    }

    private fun UploadProofOfIdentityContract.State.validate() = copy(
        confirmEnabled = (passportImage != null) || (idBackImage != null && idFrontImage != null)
    )

    companion object {
        const val REQUEST_KEY_PASSPORT = "passport"
        const val REQUEST_KEY_BACK_SIDE = "idBackSide"
        const val REQUEST_KEY_FRONT_SIDE = "idFrontSide"
    }
}