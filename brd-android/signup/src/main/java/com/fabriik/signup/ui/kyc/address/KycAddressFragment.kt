package com.fabriik.signup.ui.kyc.address

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.signup.R
import com.fabriik.signup.databinding.FragmentKycAddressBinding
import com.fabriik.signup.ui.SignupActivity
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.signup.utils.SnackBarUtils
import com.fabriik.signup.utils.clickableSpan
import com.fabriik.signup.utils.hideKeyboard
import kotlinx.coroutines.flow.collect

class KycAddressFragment : Fragment(),
    FabriikView<KycAddressContract.State, KycAddressContract.Effect> {

    private lateinit var binding: FragmentKycAddressBinding
    private val viewModel: KycAddressViewModel by lazy {
        ViewModelProvider(this).get(KycAddressViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_kyc_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKycAddressBinding.bind(view)

        with(binding) {

            // setup "Country" input field
            etCountry.doAfterTextChanged {
                viewModel.setEvent(
                    KycAddressContract.Event.CountryChanged(
                        it.toString()
                    )
                )
            }

            // setup "Zip code" input field
            etZip.doAfterTextChanged {
                viewModel.setEvent(
                    KycAddressContract.Event.ZipCodeChanged(
                        it.toString()
                    )
                )
            }

            // setup "Street number" input field
            etAddressStreet.doAfterTextChanged {
                viewModel.setEvent(
                    KycAddressContract.Event.AddressStreetChanged(
                        it.toString()
                    )
                )
            }

            // setup "Unit/Apartment" input field
            etAddressUnit.doAfterTextChanged {
                viewModel.setEvent(
                    KycAddressContract.Event.AddressUnitChanged(
                        it.toString()
                    )
                )
            }

            // setup "City" input field
            etCity.doAfterTextChanged {
                viewModel.setEvent(
                    KycAddressContract.Event.CityChanged(
                        it.toString()
                    )
                )
            }

            // setup "State" input field
            etState.doAfterTextChanged {
                viewModel.setEvent(
                    KycAddressContract.Event.StateChanged(
                        it.toString()
                    )
                )
            }

            // setup "Next" button
            btnNext.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    KycAddressContract.Event.NextClicked
                )
            }

            // setup "T&C / Privacy Policy" view
            tvTerms.clickableSpan(
                fullTextRes = R.string.KycAddress_Terms,
                clickableParts = mapOf(
                    R.string.KycAddress_Terms_Link1 to {
                        viewModel.setEvent(
                            KycAddressContract.Event.TermsAndConditionsClicked
                        )
                    },
                    R.string.KycAddress_Terms_Link2 to {
                        viewModel.setEvent(
                            KycAddressContract.Event.PrivacyPolicyClicked
                        )
                    }
                )
            )
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

    override fun render(state: KycAddressContract.State) {
        //empty
    }

    override fun handleEffect(effect: KycAddressContract.Effect) {
        when (effect) {
            is KycAddressContract.Effect.GoToPersonalInfo -> {
                findNavController().navigate(
                    KycAddressFragmentDirections.actionKycPersonalInfo(
                        zip = effect.zip,
                        city = effect.city,
                        state = effect.state,
                        street = effect.street,
                        country = effect.country
                    )
                )
            }

            is KycAddressContract.Effect.ShowLoading -> {
                val activity = activity as SignupActivity?
                activity?.showLoading(effect.show)
            }

            is KycAddressContract.Effect.ShowSnackBar -> {
                SnackBarUtils.showLong(
                    view = binding.root,
                    text = effect.message
                )
            }

            is KycAddressContract.Effect.OpenWebsite -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    SnackBarUtils.showLong(
                        view = binding.root,
                        text = R.string.SignUp_BrowserNotInstalled
                    )
                }
            }
        }
    }
}