package com.fabriik.buy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.buy.R
import com.fabriik.buy.data.enums.BuyDetailsFlow
import com.fabriik.buy.databinding.ActivityBuyBinding
import kotlinx.parcelize.Parcelize

class BuyActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityBuyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph_buy)

        val args = intent.extras?.getParcelable(EXTRA_ARGS) as Args? ?: throw IllegalArgumentException("Args object not found")
        navigateToScreen(
            startDestination = args.startDestination,
            bundle = args.bundle
        )
    }

    private fun navigateToScreen(startDestination: Int, bundle: Bundle) {
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph_buy)
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
        private const val EXTRA_FLOW = "flow"
        private const val EXTRA_EXCHANGE_ID = "exchangeId"

        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, BuyActivity::class.java)
            intent.putExtra(
                EXTRA_ARGS, Args(
                    startDestination = R.id.fragmentBuyInput,
                    bundle = bundleOf()
                )
            )
            return intent
        }

        fun getStartIntentForSwapDetails(context: Context, exchangeId: String): Intent {
            val intent = Intent(context, BuyActivity::class.java)
            intent.putExtra(
                EXTRA_ARGS, Args(
                    startDestination =  R.id.fragmentBuyDetails,
                    bundle = bundleOf(
                        EXTRA_EXCHANGE_ID to exchangeId,
                        EXTRA_FLOW to BuyDetailsFlow.TRANSACTIONS
                    )
                )
            )
            return intent
        }

        fun getStartIntentForPaymentMethod(context: Context): Intent {
            val intent = Intent(context, BuyActivity::class.java)
            intent.putExtra(
                EXTRA_ARGS, Args(
                    startDestination =  R.id.fragmentPaymentMethod,
                    bundle = bundleOf()
                )
            )
            return intent
        }
    }
}