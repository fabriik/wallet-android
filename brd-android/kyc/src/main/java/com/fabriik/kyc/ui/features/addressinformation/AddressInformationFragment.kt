package com.fabriik.kyc.ui.features.addressinformation

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
import com.fabriik.kyc.databinding.FragmentAddressInformationBinding
import com.fabriik.kyc.ui.features.accountverification.AccountVerificationContract
import com.fabriik.kyc.ui.features.accountverification.AccountVerificationFragment
import com.fabriik.kyc.ui.features.accountverification.AccountVerificationFragmentDirections
import kotlinx.coroutines.flow.collect

class AddressInformationFragment : Fragment(), FabriikView<AddressInformationContract.State, AddressInformationContract.Effect> {

    private lateinit var binding: FragmentAddressInformationBinding
    private val viewModel: AddressInformationViewModel by lazy {
        ViewModelProvider(this).get(AddressInformationViewModel::class.java)
    }

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

            tvInfo.setOnClickListener {
                viewModel.setEvent(AddressInformationContract.Event.InfoClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(AddressInformationContract.Event.ConfirmClicked)
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