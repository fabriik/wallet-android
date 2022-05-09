package com.fabriik.kyc.ui.features.exposedperson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentCompletedBinding
import com.fabriik.kyc.databinding.FragmentExposedPersonBinding
import kotlinx.coroutines.flow.collect

class ExposedPersonFragment : Fragment(),
    FabriikView<ExposedPersonContract.State, ExposedPersonContract.Effect> {

    private lateinit var binding: FragmentExposedPersonBinding
    private val viewModel: ExposedPersonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_exposed_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExposedPersonBinding.bind(view)

        with(binding) {
            btnConfirm.setOnClickListener {
                viewModel.setEvent(ExposedPersonContract.Event.ConfirmClicked)
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

    override fun render(state: ExposedPersonContract.State) {
        with(binding) {

        }
    }

    override fun handleEffect(effect: ExposedPersonContract.Effect) {
        when (effect) {
            is ExposedPersonContract.Effect.Dismiss ->
                requireActivity().finish()
        }
    }
}