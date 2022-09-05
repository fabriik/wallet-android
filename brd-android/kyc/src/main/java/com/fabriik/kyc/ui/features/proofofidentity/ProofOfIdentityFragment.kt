package com.fabriik.kyc.ui.features.proofofidentity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentProofOfIdentityBinding
import kotlinx.coroutines.flow.collect

class ProofOfIdentityFragment : Fragment(),
    FabriikView<ProofOfIdentityContract.State, ProofOfIdentityContract.Effect> {

    private lateinit var binding: FragmentProofOfIdentityBinding
    private val viewModel: ProofOfIdentityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_proof_of_identity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProofOfIdentityBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(ProofOfIdentityContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(ProofOfIdentityContract.Event.DismissClicked)
            }

            cvIdCard.setOnClickListener {
                viewModel.setEvent(ProofOfIdentityContract.Event.IdCardClicked)
            }

            cvPassport.setOnClickListener {
                viewModel.setEvent(ProofOfIdentityContract.Event.PassportClicked)
            }

            cvDrivingLicence.setOnClickListener {
                viewModel.setEvent(ProofOfIdentityContract.Event.DrivingLicenceClicked)
            }

            cvResidencePermit.setOnClickListener {
                viewModel.setEvent(ProofOfIdentityContract.Event.ResidencePermitClicked)
            }
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }

        viewModel.setEvent(
            ProofOfIdentityContract.Event.LoadDocuments
        )
    }

    override fun render(state: ProofOfIdentityContract.State) {
        with(binding) {
            cvIdCard.isVisible = state.idCardVisible
            cvPassport.isVisible = state.passportVisible
            cvDrivingLicence.isVisible = state.drivingLicenceVisible
            cvResidencePermit.isVisible = state.residencePermitVisible
            loadingIndicator.isVisible = state.initialLoadingVisible
        }
    }

    override fun handleEffect(effect: ProofOfIdentityContract.Effect) {
        when (effect) {
            is ProofOfIdentityContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )

            is ProofOfIdentityContract.Effect.Dismiss ->
                findNavController().navigate(
                    ProofOfIdentityFragmentDirections.actionAccountVerification()
                )

            is ProofOfIdentityContract.Effect.GoBack ->
                findNavController().popBackStack()

            is ProofOfIdentityContract.Effect.GoToDocumentUpload ->
                findNavController().navigate(
                    ProofOfIdentityFragmentDirections.actionTakePhoto(
                        documentType = effect.documentType,
                        documentData = arrayOf()
                    )
                )
        }
    }
}