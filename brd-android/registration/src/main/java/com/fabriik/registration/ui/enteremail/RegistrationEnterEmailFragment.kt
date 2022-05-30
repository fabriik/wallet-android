package com.fabriik.registration.ui.enteremail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.registration.R
import com.fabriik.registration.databinding.FragmentRegistrationEnterEmailBinding
import kotlinx.coroutines.flow.collect

class RegistrationEnterEmailFragment : Fragment(),
    FabriikView<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Effect> {

    private lateinit var binding: FragmentRegistrationEnterEmailBinding
    private val viewModel: RegistrationEnterEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_registration_enter_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegistrationEnterEmailBinding.bind(view)

        with(binding) {

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

    override fun render(state: RegistrationEnterEmailContract.State) {
        //empty
    }

    override fun handleEffect(effect: RegistrationEnterEmailContract.Effect) {
        when (effect) {

        }
    }
}