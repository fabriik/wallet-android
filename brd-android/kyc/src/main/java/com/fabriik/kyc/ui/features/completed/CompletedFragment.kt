package com.fabriik.kyc.ui.features.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentCompletedBinding
import com.fabriik.kyc.databinding.FragmentProofOfResidenceBinding
import kotlinx.coroutines.flow.collect

class CompletedFragment : Fragment(),
    FabriikView<CompletedContract.State, CompletedContract.Effect> {

    private lateinit var binding: FragmentCompletedBinding
    private val viewModel: CompletedViewModel by lazy {
        ViewModelProvider(this).get(CompletedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCompletedBinding.bind(view)

        with(binding) {
            btnGotIt.setOnClickListener {
                viewModel.setEvent(CompletedContract.Event.GotItClicked)
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

    override fun render(state: CompletedContract.State) {
        with(binding) {

        }
    }

    override fun handleEffect(effect: CompletedContract.Effect) {
        when (effect) {
            is CompletedContract.Effect.Dismiss ->
                requireActivity().finish()
        }
    }
}