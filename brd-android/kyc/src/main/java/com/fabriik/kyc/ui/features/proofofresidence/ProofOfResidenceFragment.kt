package com.fabriik.kyc.ui.features.proofofresidence

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
import com.fabriik.kyc.databinding.FragmentProofOfResidenceBinding
import kotlinx.coroutines.flow.collect

class ProofOfResidenceFragment : Fragment(),
    FabriikView<ProofOfResidenceContract.State, ProofOfResidenceContract.Effect> {

    private lateinit var binding: FragmentProofOfResidenceBinding
    private val viewModel: ProofOfResidenceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_proof_of_residence, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProofOfResidenceBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(ProofOfResidenceContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(ProofOfResidenceContract.Event.DismissClicked)
            }

            cvYes.setOnClickListener {
                viewModel.setEvent(ProofOfResidenceContract.Event.YesClicked)
            }

            cvNo.setOnClickListener {
                viewModel.setEvent(ProofOfResidenceContract.Event.NoClicked)
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

    override fun render(state: ProofOfResidenceContract.State) {
        with(binding) {

        }
    }

    override fun handleEffect(effect: ProofOfResidenceContract.Effect) {
        when (effect) {
            is ProofOfResidenceContract.Effect.GoBack ->
                findNavController().popBackStack()

            is ProofOfResidenceContract.Effect.Dismiss ->
                requireActivity().finish()

            is ProofOfResidenceContract.Effect.GoToCompleted ->
                findNavController().navigate(
                    ProofOfResidenceFragmentDirections.actionToCompleted()
                )

            is ProofOfResidenceContract.Effect.GoToProofUpload ->
                findNavController().navigate(
                    ProofOfResidenceFragmentDirections.actionToUploadProof()
                )
        }
    }
}