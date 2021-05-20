package com.satrango.ui.auth.user_signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityTermsAndConditionScreenBinding

class TermsAndConditionScreen : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditionScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            backIcon.setOnClickListener { onBackPressed() }
            backIconText.setOnClickListener { onBackPressed() }

        }
    }
}