package com.fabriik.kyc.ui.features.proofofidentity

import android.app.Application
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.kyc.R
import com.fabriik.kyc.data.KycApi
import com.fabriik.kyc.data.enums.DocumentType

class ProofOfIdentityViewModel(
    application: Application
) : FabriikViewModel<ProofOfIdentityContract.State, ProofOfIdentityContract.Event, ProofOfIdentityContract.Effect>(
    application
), ProofOfIdentityEventHandler {

    private val kycApi = KycApi.create(application.applicationContext)

    override fun createInitialState() = ProofOfIdentityContract.State()

    override fun onBackClicked() {
        setEffect { ProofOfIdentityContract.Effect.GoBack }
    }

    override fun onDismissClicked() {
        setEffect { ProofOfIdentityContract.Effect.Dismiss }
    }

    override fun onIdCardClicked() {
        setEffect {
            ProofOfIdentityContract.Effect.GoToDocumentUpload(
                DocumentType.ID_CARD
            )
        }
    }

    override fun onPassportClicked() {
        setEffect {
            ProofOfIdentityContract.Effect.GoToDocumentUpload(
                DocumentType.PASSPORT
            )
        }
    }

    override fun onDrivingLicenceClicked() {
        setEffect {
            ProofOfIdentityContract.Effect.GoToDocumentUpload(
                DocumentType.DRIVING_LICENCE
            )
        }
    }

    override fun onResidencePermitClicked() {
        setEffect {
            ProofOfIdentityContract.Effect.GoToDocumentUpload(
                DocumentType.RESIDENCE_PERMIT
            )
        }
    }

    override fun onLoadDocuments() {
        callApi(
            endState = { copy(initialLoadingVisible = false) },
            startState = { copy(initialLoadingVisible = true) },
            action = { kycApi.getDocuments() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setState {
                            copy(
                                idCardVisible = isDocumentAvailable(it, DocumentType.ID_CARD),
                                passportVisible = isDocumentAvailable(it, DocumentType.PASSPORT),
                                drivingLicenceVisible = isDocumentAvailable(it, DocumentType.DRIVING_LICENCE),
                                residencePermitVisible = isDocumentAvailable(it, DocumentType.RESIDENCE_PERMIT)
                            )
                        }

                    Status.ERROR ->
                        setEffect {
                            ProofOfIdentityContract.Effect.ShowToast(
                                it.message ?: getString(R.string.Api_DefaultError)
                            )
                        }
                }
            }
        )
    }

    private fun isDocumentAvailable(response: Resource<List<DocumentType>?>, type: DocumentType) : Boolean {
        return response.data?.contains(type) ?: false
    }
}