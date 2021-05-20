package com.satrango.ui.auth.user_signup

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.satrango.databinding.ActivityOTPVerificationScreenBinding


class OTPVerificationScreen : AppCompatActivity() {

    private lateinit var binding: ActivityOTPVerificationScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOTPVerificationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        otpAutoFill()

        binding.apply {

            otpTextWatchers()

            nextBtn.setOnClickListener { startActivity(Intent(this@OTPVerificationScreen, SetPasswordScreen::class.java)) }
        }

    }

    private fun otpTextWatchers() {
        binding.firstNo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    binding.firstNo.clearFocus()
                    binding.secondNo.requestFocus()
                }
            }

        })
        binding.secondNo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    binding.secondNo.clearFocus()
                    binding.thirdNo.requestFocus()
                }
            }

        })
        binding.thirdNo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    binding.thirdNo.clearFocus()
                    binding.fourthNo.requestFocus()
                }
            }

        })
        binding.fourthNo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    binding.fourthNo.clearFocus()
                }
            }

        })
    }

    private fun otpAutoFill() {
        val smsRetriverClient = SmsRetriever.getClient(this)
        val task = smsRetriverClient.startSmsRetriever()
        task.addOnSuccessListener {
            val filter = IntentFilter()
            filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            registerReceiver(SmsReceiver(), filter)
        }
    }
}