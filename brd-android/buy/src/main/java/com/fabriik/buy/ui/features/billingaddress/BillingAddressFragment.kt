package com.fabriik.buy.ui.features.billingaddress

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.tools.util.Utils
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentBillingAddressBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.common.utils.textOrEmpty
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.ui.features.countryselection.CountrySelectionFragment
import kotlinx.coroutines.flow.collect

class BillingAddressFragment : Fragment(),
    FabriikView<BillingAddressContract.State, BillingAddressContract.Effect> {

    private lateinit var binding: FragmentBillingAddressBinding
    private val viewModel: BillingAddressViewModel by viewModels()

    private val browserResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.setEvent(BillingAddressContract.Event.PaymentChallengeResult(it.resultCode))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_billing_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBillingAddressBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(BillingAddressContract.Event.BackPressed)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(BillingAddressContract.Event.DismissClicked)
            }

            etCountry.setOnClickListener {
                viewModel.setEvent(BillingAddressContract.Event.CountryClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(BillingAddressContract.Event.ConfirmClicked)
            }

            etName.doAfterTextChanged {
                viewModel.setEvent(
                    BillingAddressContract.Event.FirstNameChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etLastName.doAfterTextChanged {
                viewModel.setEvent(
                    BillingAddressContract.Event.LastNameChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etCity.doAfterTextChanged {
                viewModel.setEvent(
                    BillingAddressContract.Event.CityChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etState.doAfterTextChanged {
                viewModel.setEvent(
                    BillingAddressContract.Event.StateChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etPostalCode.doAfterTextChanged {
                viewModel.setEvent(
                    BillingAddressContract.Event.ZipChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etAddress.doAfterTextChanged {
                viewModel.setEvent(
                    BillingAddressContract.Event.AddressChanged(
                        it.textOrEmpty()
                    )
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

        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_COUNTRY_SELECTION, this@BillingAddressFragment) { _, bundle ->
            val country = bundle.getParcelable(CountrySelectionFragment.EXTRA_SELECTED_COUNTRY) as Country?
            if (country != null) {
                viewModel.setEvent(
                    BillingAddressContract.Event.CountryChanged(country)
                )
            }
        }
    }

    override fun render(state: BillingAddressContract.State) {
        with(binding) {
            btnConfirm.isEnabled = state.confirmEnabled
            etCountry.setText(state.country?.name)
            viewLoading.root.isVisible = state.loadingIndicatorVisible
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

            is BillingAddressContract.Effect.PaymentMethod ->
                findNavController().navigate(
                    BillingAddressFragmentDirections.actionPaymentMethod(
                        effect.flow
                    )
                )

            is BillingAddressContract.Effect.ShowError ->
                FabriikToastUtil.showError(
                    parentView = binding.root,
                    message = effect.message
                )

            is BillingAddressContract.Effect.OpenWebsite -> {
                val uri = Uri.parse(effect.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                browserResultLauncher.launch(intent)
            }
        }
    }

    companion object {
        const val REQUEST_KEY_COUNTRY_SELECTION = "request_key_country"
    }
}