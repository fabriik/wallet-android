package com.fabriik.kyc.ui.features.postvalidation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentPostValidationBinding

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

            btnConfirm.setOnClickListener {
                viewModel.setEvent(PostValidationContract.Event.ConfirmClicked)
            }
        }
    }

    override fun render(state: PostValidationContract.State) {
        TODO("Not yet implemented")
    }

    override fun handleEffect(effect: PostValidationContract.Effect) {
        when (effect) {
            is PostValidationContract.Effect.GoBack -> {
                findNavController().popBackStack()
            }
            is PostValidationContract.Effect.GoForward -> {
                //TODO - Navigate to next screen
            }
        }
    }
}