package com.satrango.ui.auth.forgot_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.snackbar.Snackbar
import com.satrango.R
import com.satrango.ui.auth.user_signup.OTPVerificationScreen
import com.satrango.databinding.ActivityForgotPasswordScreenOneBinding
import com.satrango.utils.UserUtils

class ForgotPasswordScreenOne : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordScreenOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            textWatcher()

            resetPasswordBtn.setOnClickListener {
                val mobile = mobileNo.text.toString().trim()
                if (mobile.isEmpty()) {
                    Snackbar.make(mobileNo, "Enter Mobile Number", Snackbar.LENGTH_LONG).show()
                } else if (mobile.length != 10) {
                    Snackbar.make(mobileNo, "Enter Valid Mobile Number", Snackbar.LENGTH_LONG).show()
                } else {
                    UserUtils.phoneNo = mobile
                    startActivity(Intent(this@ForgotPasswordScreenOne, OTPVerificationScreen::class.java))
                }
            }

        }

    }

    private fun textWatcher() {
        binding.mobileNo.addTextChangedListener(object: TextWatcher {
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
                if (s!!.length == 10) {
                    binding.mobileNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_greencheck, 0)
                }
            }

        })
    }
}