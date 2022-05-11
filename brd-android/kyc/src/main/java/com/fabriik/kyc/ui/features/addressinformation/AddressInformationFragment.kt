package com.fabriik.kyc.ui.features.addressinformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.textOrEmpty
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentAddressInformationBinding
import kotlinx.coroutines.flow.collect

class AddressInformationFragment : Fragment(),
    FabriikView<AddressInformationContract.State, AddressInformationContract.Effect> {

    private lateinit var binding: FragmentAddressInformationBinding
    private val viewModel: AddressInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_address_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddressInformationBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(AddressInformationContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(AddressInformationContract.Event.DismissClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(AddressInformationContract.Event.ConfirmClicked)
            }

            etCountry.doAfterTextChanged {
                viewModel.setEvent(
                    AddressInformationContract.Event.CountryChanged(it.textOrEmpty())
                )
            }

            etState.doAfterTextChanged {
                viewModel.setEvent(
                    AddressInformationContract.Event.StateChanged(it.textOrEmpty())
                )
            }

            etCity.doAfterTextChanged {
                viewModel.setEvent(
                    AddressInformationContract.Event.CityChanged(it.textOrEmpty())
                )
            }

            etZip.doAfterTextChanged {
                viewModel.setEvent(
                    AddressInformationContract.Event.ZipChanged(it.textOrEmpty())
                )
            }

            etAddress1.doAfterTextChanged {
                viewModel.setEvent(
                    AddressInformationContract.Event.AddressLine1Changed(it.textOrEmpty())
                )
            }

            etAddress2.doAfterTextChanged {
                viewModel.setEvent(
                    AddressInformationContract.Event.AddressLine2Changed(it.textOrEmpty())
                )
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

    override fun render(state: AddressInformationContract.State) {
        with(binding) {
            btnConfirm.isEnabled = state.confirmEnabled
        }
    }

    override fun handleEffect(effect: AddressInformationContract.Effect) {
        when (effect) {
            is AddressInformationContract.Effect.GoBack ->
                findNavController().popBackStack()

            is AddressInformationContract.Effect.Dismiss ->
                requireActivity().finish()

            is AddressInformationContract.Effect.GoToProofOfResidence ->
                findNavController().navigate(
                    AddressInformationFragmentDirections.actionToProofOfResidence()
                )
        }
    }
}