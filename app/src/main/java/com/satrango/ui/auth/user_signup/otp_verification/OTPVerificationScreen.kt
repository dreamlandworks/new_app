package com.satrango.ui.auth.user_signup.otp_verification

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityOTPVerificationScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.ui.auth.user_signup.SmsReceiver
import com.satrango.ui.auth.user_signup.set_password.SetPasswordScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class OTPVerificationScreen : AppCompatActivity() {

    private var otp: Int = 0
    private lateinit var viewModel: OTPVerificationViewModel
    private lateinit var binding: ActivityOTPVerificationScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOTPVerificationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(OTPVerificationRepository())
        viewModel = ViewModelProvider(this, factory)[OTPVerificationViewModel::class.java]

        initializeProgressDialog()
        requestOTP()

        binding.apply {

            otpTextWatchers()

            nextBtn.setOnClickListener {
                val numOne = firstNo.text.toString().trim()
                val numTwo = secondNo.text.toString().trim()
                val numThree = thirdNo.text.toString().trim()
                val numFourth = fourthNo.text.toString().trim()
                if (numOne.isEmpty() || numTwo.isEmpty() || numThree.isEmpty() || numFourth.isEmpty()) {
                    snackBar(firstNo, "Enter OTP to verify")
                } else {
                    val userOTP = numOne + numTwo + numThree + numFourth
                    if (otp.toString() == userOTP) {
                        startActivity(Intent(this@OTPVerificationScreen, SetPasswordScreen::class.java))
                        finish()
                    } else {
                        firstNo.setBackgroundResource(R.drawable.wrongpricebackground)
                        secondNo.setBackgroundResource(R.drawable.wrongpricebackground)
                        thirdNo.setBackgroundResource(R.drawable.wrongpricebackground)
                        fourthNo.setBackgroundResource(R.drawable.wrongpricebackground)
                        snackBar(nextBtn, "Invalid OTP")
                    }
                }
            }

            binding.reSendOTP.setOnClickListener {
                requestOTP()
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
        progressDialog.show()
    }
    private fun requestOTP() {
        if (UserUtils.FORGOT_PWD) {
            viewModel.forgotPwdRequestOTP(this).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        val data = it.data!!
                        otp = data.split("|")[1].toInt()
                        UserUtils.USER_ID = data.split("|")[0]
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.nextBtn, it.message!!)
                    }
                }
            })
        } else {
            viewModel.requestOTP(this).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        otp = it.data!!.toInt()
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        snackBar(binding.nextBtn, it.message!!)
                    }
                }
            })
        }
    }

    private fun otpTextWatchers() {
        binding.firstNo.addTextChangedListener(object : TextWatcher {
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
        binding.secondNo.addTextChangedListener(object : TextWatcher {
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
        binding.thirdNo.addTextChangedListener(object : TextWatcher {
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
        binding.fourthNo.addTextChangedListener(object : TextWatcher {
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

//    private fun otpAutoFill() {
//        val smsRetriverClient = SmsRetriever.getClient(this)
//        val task = smsRetriverClient.startSmsRetriever()
//        task.addOnSuccessListener {
//            val filter = IntentFilter()
//            filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//            registerReceiver(SmsReceiver(), filter)
//        }
//    }
}