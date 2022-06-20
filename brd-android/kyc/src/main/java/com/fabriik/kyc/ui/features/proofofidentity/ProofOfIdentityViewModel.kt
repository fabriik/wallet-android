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
) {

    private val kycApi = KycApi.create(application.applicationContext)

    override fun createInitialState() = ProofOfIdentityContract.State()

    override fun handleEvent(event: ProofOfIdentityContract.Event) {
        when (event) {
            is ProofOfIdentityContract.Event.LoadDocuments ->
                loadDocuments()

            is ProofOfIdentityContract.Event.BackClicked ->
                setEffect { ProofOfIdentityContract.Effect.GoBack }

            is ProofOfIdentityContract.Event.IdCardClicked ->
                setEffect {
                    ProofOfIdentityContract.Effect.GoToDocumentUpload(
                        DocumentType.ID_CARD
                    )
                }

            is ProofOfIdentityContract.Event.PassportClicked ->
                setEffect {
                    ProofOfIdentityContract.Effect.GoToDocumentUpload(
                        DocumentType.PASSPORT
                    )
                }

            is ProofOfIdentityContract.Event.DrivingLicenceClicked ->
                setEffect {
                    ProofOfIdentityContract.Effect.GoToDocumentUpload(
                        DocumentType.DRIVING_LICENCE
                    )
                }

            is ProofOfIdentityContract.Event.ResidencePermitClicked ->
                setEffect {
                    ProofOfIdentityContract.Effect.GoToDocumentUpload(
                        DocumentType.RESIDENCE_PERMIT
                    )
                }

            is ProofOfIdentityContract.Event.Dismiss ->
                setEffect { ProofOfIdentityContract.Effect.Dismiss }
        }
    }

    private fun loadDocuments() {
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
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
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