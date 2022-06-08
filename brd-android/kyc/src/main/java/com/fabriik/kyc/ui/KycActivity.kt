package com.fabriik.kyc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.common.data.model.Profile
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.ActivityKycBinding
import com.fabriik.kyc.ui.features.accountverification.AccountVerificationFragmentArgs
import java.lang.IllegalStateException

class KycActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph_kyc, intent.extras)
    }

    companion object {
        private const val EXTRA_PROFILE = "profile"

        fun getStartIntent(context: Context, profile: Profile): Intent {
            val intent = Intent(context, KycActivity::class.java)
            intent.putExtra(EXTRA_PROFILE, profile)
            return intent
        }
    }
}