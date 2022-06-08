package com.fabriik.kyc.ui.features.proofofidentity

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.FabriikApiResponse
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.data.KycApi
import com.fabriik.kyc.data.enums.DocumentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProofOfIdentityViewModel(
    application: Application
) : FabriikViewModel<ProofOfIdentityContract.State, ProofOfIdentityContract.Event, ProofOfIdentityContract.Effect>(
    application
) {

    private val kycApi = KycApi.create()

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
        }
    }

    private fun loadDocuments() {
        viewModelScope.launch(Dispatchers.IO) {
            // show loading indicator
            setState { copy(initialLoadingVisible = true) }

            val response = kycApi.getDocuments()

            // dismiss loading indicator
            setState { copy(initialLoadingVisible = false) }

            when (response.status) {
                Status.SUCCESS ->
                    setState {
                        copy(
                            idCardVisible = isDocumentAvailable(response, DocumentType.ID_CARD),
                            passportVisible = isDocumentAvailable(response, DocumentType.PASSPORT),
                            drivingLicenceVisible = isDocumentAvailable(response, DocumentType.DRIVING_LICENCE),
                            residencePermitVisible = isDocumentAvailable(response, DocumentType.RESIDENCE_PERMIT)
                        )
                    }

                Status.ERROR ->
                    setEffect {
                        ProofOfIdentityContract.Effect.ShowToast(response.message!!)
                    }
            }
        }
    }

    private fun isDocumentAvailable(response: Resource<List<DocumentType>?>, type: DocumentType) : Boolean {
        return response.data?.contains(type) ?: false
    }
}