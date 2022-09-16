package com.fabriik.trade.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.trade.R
import com.fabriik.trade.databinding.ActivitySwapBinding
import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

class SwapActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivitySwapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph_swap, intent.extras)

        val args = intent.extras?.getParcelable(EXTRA_ARGS) as Args? ?: throw IllegalArgumentException("Args object not found")
        navigateToScreen(
            startDestination = args.startDestination,
            bundle = args.bundle
        )
    }

    private fun navigateToScreen(startDestination: Int, bundle: Bundle) {
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph_swap)
        graph.startDestination = startDestination
        navHostFragment.navController.setGraph(graph, bundle)
    }

    @Parcelize
    class Args(
        val startDestination: Int,
        val bundle: Bundle
    ) : Parcelable

    companion object {
        private const val EXTRA_ARGS = "extra"
        private const val EXTRA_EXCHANGE_ID = "exchangeId"

        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, SwapActivity::class.java)
            intent.putExtra(
                EXTRA_ARGS, Args(
                    startDestination = R.id.fragmentSwapInput,
                    bundle = bundleOf()
                )
            )
            return intent
        }

        fun getStartIntentForSwapDetails(context: Context, exchangeId: String): Intent {
            val intent = Intent(context, SwapActivity::class.java)
            intent.putExtra(
                EXTRA_ARGS, Args(
                    startDestination = R.id.fragmentSwapDetails,
                    bundle = bundleOf(EXTRA_EXCHANGE_ID to exchangeId)
                )
            )
            return intent
        }
    }
}