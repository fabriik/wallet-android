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
                setEffect {
                    UploadProofOfIdentityContract.Effect.GoBack
                }

            is UploadProofOfIdentityContract.Event.DismissClicked ->
                setEffect {
                    UploadProofOfIdentityContract.Effect.Dismiss
                }

            is UploadProofOfIdentityContract.Event.ConfirmClicked ->
                setEffect {
                    UploadProofOfIdentityContract.Effect.GoToAddress
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
}