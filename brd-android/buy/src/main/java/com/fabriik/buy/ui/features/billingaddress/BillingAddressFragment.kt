package com.fabriik.buy.ui.features.billingaddress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.tools.util.Utils
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentBillingAddressBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.ui.features.countryselection.CountrySelectionFragment
import kotlinx.coroutines.flow.collect

class BillingAddressFragment : Fragment(),
    FabriikView<BillingAddressContract.State, BillingAddressContract.Effect> {

    private lateinit var binding: FragmentBillingAddressBinding
    private val viewModel: BillingAddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_billing_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBillingAddressBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(BillingAddressContract.Event.OnBackPressed)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(BillingAddressContract.Event.OnDismissClicked)
            }

            etCountry.setOnClickListener {
                viewModel.setEvent(BillingAddressContract.Event.OnCountryClicked)
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

        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_COUNTRY_SELECTION, this@BillingAddressFragment) { _, bundle ->
            val country = bundle.getParcelable(CountrySelectionFragment.EXTRA_SELECTED_COUNTRY) as Country?
            if (country != null) {
                viewModel.setEvent(
                    BillingAddressContract.Event.OnCountryChanged(country)
                )
            }
        }
    }

    override fun render(state: BillingAddressContract.State) {
        with(binding) {
            btnConfirm.isEnabled = state.confirmEnabled
            etCountry.setText(state.country?.name)
        }
    }

    override fun handleEffect(effect: BillingAddressContract.Effect) {
        when (effect) {
            BillingAddressContract.Effect.Back ->
                findNavController().popBackStack()

            BillingAddressContract.Effect.Dismiss ->
                activity?.finish()

            BillingAddressContract.Effect.CountrySelection -> {
                Utils.hideKeyboard(binding.root.context)
                findNavController().navigate(
                    BillingAddressFragmentDirections.actionCountrySelection(
                        REQUEST_KEY_COUNTRY_SELECTION
                    )
                )
            }
        }
    }

    companion object {
        const val REQUEST_KEY_COUNTRY_SELECTION = "request_key_country"
    }
}