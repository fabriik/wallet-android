package com.fabriik.common.ui.features.nointernet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fabriik.common.databinding.ActivityNoInternetBinding

class NoInternetActivity: AppCompatActivity() {

    private lateinit var binding : ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOk.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getStartIntent(context: Context) = Intent(context, NoInternetActivity::class.java)
    }
}