package com.fabriik.kyc.ui.features.proofofidentityupload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentUploadProofOfIdentityBinding
import com.fabriik.kyc.databinding.FragmentUploadProofOfResidenceBinding
import kotlinx.coroutines.flow.collect

class UploadProofOfIdentityFragment : Fragment(),
    FabriikView<UploadProofOfIdentityContract.State, UploadProofOfIdentityContract.Effect> {

    private lateinit var binding: FragmentUploadProofOfIdentityBinding
    private val viewModel: UploadProofOfIdentityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_upload_proof_of_identity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadProofOfIdentityBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.DismissClicked)
            }

            btnAddIdBack.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.AddIdBackClicked)
            }

            btnAddIdFront.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.AddIdFrontClicked)
            }

            btnAddPassport.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.AddPassportClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.ConfirmClicked)
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
    }

    override fun render(state: UploadProofOfIdentityContract.State) {
        with(binding) {
            //btnConfirm.isEnabled = state.confirmEnabled // todo: revert
            tvDescription.text = state.description
            btnAddPassport.isVisible = state.addPassportPhotoVisible
            layoutIdButtons.isVisible = state.addIdPhotosVisible
        }
    }

    override fun handleEffect(effect: UploadProofOfIdentityContract.Effect) {
        when (effect) {
            is UploadProofOfIdentityContract.Effect.GoBack ->
                findNavController().popBackStack()

            is UploadProofOfIdentityContract.Effect.Dismiss ->
                requireActivity().finish()

            is UploadProofOfIdentityContract.Effect.GoToAddress ->
                findNavController().navigate(
                    UploadProofOfIdentityFragmentDirections.actionToAddress()
                )
        }
    }
}