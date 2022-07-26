package com.fabriik.buy.ui.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentAddCardBinding
import com.fabriik.common.ui.base.FabriikView
import kotlinx.coroutines.flow.collect

class AddCardFragment : Fragment(), FabriikView<AddCardContract.State, AddCardContract.Effect> {

    lateinit var binding: FragmentAddCardBinding

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

            etCardNumber.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    formatCardField(text)
                }
            }

            etDate.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    formatDate(text)
                }
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
    }

    override fun handleEffect(effect: AddCardContract.Effect) {
        when (effect) {
            AddCardContract.Effect.Back ->
                findNavController().popBackStack()

            AddCardContract.Effect.Dismiss ->
                activity?.finish()

            AddCardContract.Effect.Confirm -> {}
        }
    }

    private fun formatCardField(text: CharSequence) {
        when {
            text.length == 5 && text[4].isDigit() -> binding.etCardNumber.text?.insert(4, " ")
            text.length == 10 && text[9].isDigit() -> binding.etCardNumber.text?.insert(9, " ")
            text.length == 15 && text[14].isDigit() -> binding.etCardNumber.text?.insert(14, " ")
        }
    }

    private fun formatDate(text: CharSequence) {
        when {
            text.length == 3 && text[2].isDigit() -> binding.etDate.text?.insert(2, "/")
        }
    }
}