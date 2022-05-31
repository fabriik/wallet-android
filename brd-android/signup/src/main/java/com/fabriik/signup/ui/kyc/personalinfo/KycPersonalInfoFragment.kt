package com.fabriik.signup.ui.kyc.personalinfo

import android.app.DatePickerDialog
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
import com.fabriik.signup.databinding.FragmentKycPersonalInfoBinding
import com.fabriik.signup.ui.SignupActivity
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.signup.utils.SnackBarUtils
import com.fabriik.signup.utils.hideKeyboard
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


class KycPersonalInfoFragment : Fragment(),
    FabriikView<KycPersonalInfoContract.State, KycPersonalInfoContract.Effect> {

    private lateinit var binding: FragmentKycPersonalInfoBinding
    private val viewModel: KycPersonalInfoViewModel by lazy {
        ViewModelProvider(this).get(KycPersonalInfoViewModel::class.java)
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_kyc_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKycPersonalInfoBinding.bind(view)

        with(binding) {

            // setup "Date of birth" input field
            etDateOfBirth.setOnClickListener {
                viewModel.setEvent(
                    KycPersonalInfoContract.Event.DateOfBirthClicked
                )
            }

            // setup "Tax ID" input field
            etTaxId.doAfterTextChanged {
                viewModel.setEvent(
                    KycPersonalInfoContract.Event.TaxIdChanged(
                        it.toString()
                    )
                )
            }

            // setup "Next" button
            btnNext.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    KycPersonalInfoContract.Event.NextClicked
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

    override fun render(state: KycPersonalInfoContract.State) {
        binding.etDateOfBirth.text =
            if (state.dateOfBirth == null) null else dateFormatter.format(state.dateOfBirth)
    }

    override fun handleEffect(effect: KycPersonalInfoContract.Effect) {
        when (effect) {
            is KycPersonalInfoContract.Effect.GoToIdUpload -> {
                findNavController().navigate(
                    KycPersonalInfoFragmentDirections.actionIdUpload()
                )
            }

            is KycPersonalInfoContract.Effect.OpenDatePicker ->
                openDatePicker(effect.date)

            is KycPersonalInfoContract.Effect.ShowLoading -> {
                val activity = activity as SignupActivity?
                activity?.showLoading(effect.show)
            }

            is KycPersonalInfoContract.Effect.ShowSnackBar -> {
                SnackBarUtils.showLong(
                    view = binding.root,
                    text = effect.message
                )
            }
        }
    }

    private fun openDatePicker(date: Date?) {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0)
            }

            viewModel.setEvent(
                KycPersonalInfoContract.Event.DateOfBirthChanged(
                    selectedDate.time
                )
            )
        }

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }

        DatePickerDialog(
            requireContext(), listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = Calendar.getInstance().timeInMillis
        }.show()
    }
}