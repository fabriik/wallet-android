package com.fabriik.kyc.ui.features.proofofresidenceupload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentUploadProofOfResidenceBinding
import kotlinx.coroutines.flow.collect

class UploadProofOfResidenceFragment : Fragment(),
    FabriikView<UploadProofOfResidenceContract.State, UploadProofOfResidenceContract.Effect> {

    private lateinit var binding: FragmentUploadProofOfResidenceBinding
    private val viewModel: UploadProofOfResidenceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_upload_proof_of_residence, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadProofOfResidenceBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.DismissClicked)
            }

            tvInfo.setOnClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.InfoClicked)
            }

            btnAddDocument.setOnClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.AddDocumentClicked)
            }

            btnSubmit.setOnClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.SubmitClicked)
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

    override fun render(state: UploadProofOfResidenceContract.State) {
        with(binding) {

        }
    }

    override fun handleEffect(effect: UploadProofOfResidenceContract.Effect) {
        when (effect) {
            is UploadProofOfResidenceContract.Effect.GoBack ->
                findNavController().popBackStack()

            is UploadProofOfResidenceContract.Effect.Dismiss ->
                requireActivity().finish()

            is UploadProofOfResidenceContract.Effect.GoToCompleted ->
                findNavController().navigate(
                    UploadProofOfResidenceFragmentDirections.actionToCompleted()
                )

            is UploadProofOfResidenceContract.Effect.OpenPhotoSourcePicker ->
                findNavController().navigate(
                    UploadProofOfResidenceFragmentDirections.actionOpenPhotoSourcePicker(
                        effect.requestKey
                    )
                )
        }
    }
}