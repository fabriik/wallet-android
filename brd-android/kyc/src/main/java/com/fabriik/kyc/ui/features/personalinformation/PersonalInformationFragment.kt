package com.fabriik.kyc.ui.features.personalinformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentPersonalInformationBinding
import kotlinx.coroutines.flow.collect

class PersonalInformationFragment : Fragment(), FabriikView<PersonalInformationContract.State, PersonalInformationContract.Effect> {

    private lateinit var binding: FragmentPersonalInformationBinding
    private val viewModel: PersonalInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_personal_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPersonalInformationBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.DismissClicked)
            }

            tvInfo.setOnClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.ScreenInfoClicked)
            }

            tvSection3Info.setOnClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.ExposedPersonInfoClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.ConfirmClicked)
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

    override fun render(state: PersonalInformationContract.State) {
        with(binding) {

        }
    }

    override fun handleEffect(effect: PersonalInformationContract.Effect) {
        when (effect) {
            is PersonalInformationContract.Effect.GoBack ->
                findNavController().popBackStack()

            is PersonalInformationContract.Effect.Dismiss ->
                requireActivity().finish()
        }
    }
}