package com.fabriik.buy.ui.billingaddress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentBillingAddressBinding
import com.fabriik.common.ui.base.FabriikView
import kotlinx.coroutines.flow.collect

class BillingAddressFragment : Fragment(),
    FabriikView<BillingAddressContract.State, BillingAddressContract.Effect> {

    lateinit var binding: FragmentBillingAddressBinding
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

            tilCountry.setOnClickListener {
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
    }

    override fun render(state: BillingAddressContract.State) {
    }

    override fun handleEffect(effect: BillingAddressContract.Effect) {
        when (effect) {
            BillingAddressContract.Effect.Back -> TODO()

            BillingAddressContract.Effect.Dismiss ->
                activity?.finish()

            BillingAddressContract.Effect.CountryList -> TODO()
        }
    }
}