package com.satrango.ui.auth.provider_signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.satrango.databinding.ActivityProviderSignUpTwoBinding
import com.satrango.ui.auth.provider_signup.provider_sign_up_three.ProviderSignUpThree

class ProviderSignUpTwo : AppCompatActivity() {

    private lateinit var binding: ActivityProviderSignUpTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderSignUpTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            keywordSkills.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
            keywordSkills.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)

            nextBtn.setOnClickListener {
                startActivity(Intent(this@ProviderSignUpTwo, ProviderSignUpThree::class.java))
            }

            backBtn.setOnClickListener { onBackPressed() }

        }

    }
}