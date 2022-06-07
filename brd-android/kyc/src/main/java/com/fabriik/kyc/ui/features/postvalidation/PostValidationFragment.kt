package com.fabriik.kyc.ui.features.postvalidation

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
import com.fabriik.kyc.databinding.FragmentPostValidationBinding
import kotlinx.coroutines.flow.collect

class PostValidationFragment : Fragment(),
    FabriikView<PostValidationContract.State, PostValidationContract.Effect> {

    private lateinit var binding: FragmentPostValidationBinding
    private val viewModel: PostValidationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_validation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostValidationBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(PostValidationContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PostValidationContract.Event.DismissClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(PostValidationContract.Event.ConfirmClicked)
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

    override fun render(state: PostValidationContract.State) {
    }

    override fun handleEffect(effect: PostValidationContract.Effect) {
        when (effect) {
            is PostValidationContract.Effect.Back -> {
                findNavController().popBackStack()
            }

            is PostValidationContract.Effect.Dismiss -> {
                requireActivity().finish()
            }

            is PostValidationContract.Effect.Profile -> {
                //TODO - Navigate to next screen
            }
        }
    }
}