package com.fabriik.signup.ui.kyc.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.signup.R
import com.fabriik.signup.databinding.FragmentKycCompleteBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.signup.utils.hideKeyboard
import kotlinx.coroutines.flow.collect

class KycCompletedFragment : Fragment(),
    FabriikView<KycCompletedContract.State, KycCompletedContract.Effect> {

    private lateinit var binding: FragmentKycCompleteBinding
    private val viewModel: KycCompletedViewModel by lazy {
        ViewModelProvider(this).get(KycCompletedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_kyc_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKycCompleteBinding.bind(view)

        with(binding) {
            // setup "Done" button
            btnDone.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    KycCompletedContract.Event.DoneClicked
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

    override fun render(state: KycCompletedContract.State) {
        //empty
    }

    override fun handleEffect(effect: KycCompletedContract.Effect) {
        when (effect) {
            is KycCompletedContract.Effect.GoToLogin -> {
                findNavController().navigate(
                    KycCompletedFragmentDirections.actionLogIn()
                )
            }
        }
    }
}