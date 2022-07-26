package com.fabriik.common.ui.features.nointernet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fabriik.common.databinding.ActivityNoInternetBinding

class NoInternetActivity: AppCompatActivity() {

    private val binding = ActivityNoInternetBinding.inflate(
        layoutInflater
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnOk.setOnClickListener {
            finish()
        }
    }
}