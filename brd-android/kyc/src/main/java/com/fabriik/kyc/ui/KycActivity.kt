package com.fabriik.kyc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.ActivityKycBinding

class KycActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, KycActivity::class.java)
    }
}