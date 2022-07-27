package com.fabriik.buy.ui.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentAddCardBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import kotlinx.coroutines.flow.collect

class AddCardFragment : Fragment(), FabriikView<AddCardContract.State, AddCardContract.Effect> {

    private lateinit var binding: FragmentAddCardBinding

    private val viewModel: AddCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddCardBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(AddCardContract.Event.OnBackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(AddCardContract.Event.OnDismissClicked)
            }

            etCardNumber.doAfterTextChanged {
                viewModel.setEvent(AddCardContract.Event.OnCardNumberChanged(it.toString()))
            }

            etDate.doAfterTextChanged {
                viewModel.setEvent(AddCardContract.Event.OnDateChanged(it.toString()))
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

    override fun render(state: AddCardContract.State) {
        with(binding) {
            etCardNumber.setText(state.cardNumber)
            etCardNumber.setSelection(state.cardNumber?.length ?: 0)

            etDate.setText(state.date)
            etDate.setSelection(state.date?.length ?: 0)
        }
    }

    override fun handleEffect(effect: AddCardContract.Effect) {
        when (effect) {
            AddCardContract.Effect.Back ->
                findNavController().popBackStack()

            AddCardContract.Effect.Dismiss ->
                activity?.finish()

            AddCardContract.Effect.Confirm -> {}

            is AddCardContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )
        }
    }
}