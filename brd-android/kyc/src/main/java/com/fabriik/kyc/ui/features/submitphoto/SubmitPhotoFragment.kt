package com.fabriik.kyc.ui.features.submitphoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.databinding.FragmentSubmitPhotoBinding
import kotlinx.coroutines.flow.collect

class SubmitPhotoFragment : Fragment(),
    FabriikView<SubmitPhotoContract.State, SubmitPhotoContract.Effect> {

    private lateinit var binding: FragmentSubmitPhotoBinding
    private val viewModel: SubmitPhotoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_submit_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubmitPhotoBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.DismissClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.ConfirmClicked)
            }

            btnRetakePhoto.setOnClickListener {
                viewModel.setEvent(SubmitPhotoContract.Event.RetakeClicked)
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

    override fun render(state: SubmitPhotoContract.State) {
        with(binding) {
            val isSelfie = state.documentType == DocumentType.SELFIE
            ivSelfie.isVisible = isSelfie
            ivDocument.isVisible = !isSelfie

            Glide.with(requireContext())
                .load(state.image)
                .into(if (isSelfie) ivSelfie else ivDocument)
        }
    }

    override fun handleEffect(effect: SubmitPhotoContract.Effect) {
        when (effect) {
            is SubmitPhotoContract.Effect.Back ->
                findNavController().popBackStack()

            is SubmitPhotoContract.Effect.Dismiss ->
                requireActivity().finish()

            is SubmitPhotoContract.Effect.TakePhoto ->
                findNavController().navigate(
                    SubmitPhotoFragmentDirections.actionTakePhoto(
                        documentSide = effect.documentSide,
                        documentType = effect.documentType
                    )
                )

            is SubmitPhotoContract.Effect.PostValidation -> {
                findNavController().navigate(
                    SubmitPhotoFragmentDirections.actionPostValidation()
                )
            }
        }
    }
}