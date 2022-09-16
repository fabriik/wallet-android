package com.fabriik.kyc.ui.features.personalinformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.common.utils.hideKeyboard
import com.fabriik.common.utils.showKeyboard
import com.fabriik.common.utils.textOrEmpty
import com.fabriik.kyc.R
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.databinding.FragmentPersonalInformationBinding
import com.fabriik.kyc.ui.features.countryselection.CountrySelectionFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collect
import java.util.*

class PersonalInformationFragment : Fragment(),
    FabriikView<PersonalInformationContract.State, PersonalInformationContract.Effect> {

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

            btnConfirm.setOnClickListener {
                viewModel.setEvent(PersonalInformationContract.Event.ConfirmClicked)
            }

            etName.doAfterTextChanged {
                viewModel.setEvent(
                    PersonalInformationContract.Event.NameChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etLastName.doAfterTextChanged {
                viewModel.setEvent(
                    PersonalInformationContract.Event.LastNameChanged(
                        it.textOrEmpty()
                    )
                )
            }

            etDay.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.DateClicked
                )
            }

            etYear.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.DateClicked
                )
            }

            etMonth.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.DateClicked
                )
            }

            etCountry.setOnClickListener {
                viewModel.setEvent(
                    PersonalInformationContract.Event.CountryClicked
                )
            }

            parentFragmentManager.setFragmentResultListener(REQUEST_KEY_COUNTRY_SELECTION, this@PersonalInformationFragment) { _, bundle ->
                val country = bundle.getParcelable(CountrySelectionFragment.EXTRA_SELECTED_COUNTRY) as Country?
                if (country != null) {
                    viewModel.setEvent(
                        PersonalInformationContract.Event.CountryChanged(country)
                    )
                }
            }
        }

        if (viewModel.state.value.name.isEmpty()) {
            binding.etName.showKeyboard()
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

        viewModel.setEvent(PersonalInformationContract.Event.LoadProfile)
    }

    override fun render(state: PersonalInformationContract.State) {
        with(binding) {
            viewLoading.root.isVisible = state.loadingVisible
            viewCompleted.root.isVisible = state.completedViewVisible
            btnConfirm.isEnabled = state.confirmEnabled
            etCountry.setText(state.country?.name)

            etName.setText(state.name)
            etName.setSelection(state.name.length)
            etLastName.setText(state.lastName)
            etLastName.setSelection(state.lastName.length)

            val date = state.dateOfBirth
            etDay.setText(date?.get(Calendar.DAY_OF_MONTH)?.toString())
            etYear.setText(date?.get(Calendar.YEAR)?.toString())
            etMonth.setText(date?.get(Calendar.MONTH)?.plus(1)?.toString())
        }
    }

    override fun handleEffect(effect: PersonalInformationContract.Effect) {
        when (effect) {
            is PersonalInformationContract.Effect.GoBack -> {
                hideKeyboard()
                findNavController().popBackStack()
            }

            is PersonalInformationContract.Effect.Dismiss -> {
                hideKeyboard()
                findNavController().navigate(
                    PersonalInformationFragmentDirections.actionAccountVerification()
                )
            }

            is PersonalInformationContract.Effect.ShowToast -> {
                hideKeyboard()
                FabriikToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )
            }

            is PersonalInformationContract.Effect.DateSelection -> {
                hideKeyboard()
                val constraints = CalendarConstraints.Builder()
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                val picker = MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(constraints)
                    .setSelection(effect.date?.timeInMillis)
                    .build()

                picker.addOnPositiveButtonClickListener {
                    viewModel.setEvent(PersonalInformationContract.Event.DateChanged(it))
                }

                picker.show(childFragmentManager, TAG_DATE_PICKER)
            }

            is PersonalInformationContract.Effect.CountrySelection -> {
                hideKeyboard()
                findNavController().navigate(
                    PersonalInformationFragmentDirections.actionCountrySelection(
                        REQUEST_KEY_COUNTRY_SELECTION
                    )
                )
            }
        }
    }

    companion object {
        const val TAG_DATE_PICKER = "date_of_birth_picker"
        const val REQUEST_KEY_COUNTRY_SELECTION = "request_key_country"
    }

    private fun hideKeyboard() {
        with(binding) {
            etName.hideKeyboard()
            etLastName.hideKeyboard()
        }
    }
}