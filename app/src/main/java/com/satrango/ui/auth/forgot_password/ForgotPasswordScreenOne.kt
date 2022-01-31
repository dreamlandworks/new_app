package com.satrango.ui.auth.forgot_password

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityForgotPasswordScreenOneBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class ForgotPasswordScreenOne : AppCompatActivity() {

    private lateinit var viewModel: ForgotPwdViewModel
    private lateinit var binding: ActivityForgotPasswordScreenOneBinding
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(ForgotPwdRepository())
        viewModel = ViewModelProvider(this, factory)[ForgotPwdViewModel::class.java]

        initializeProgressDialog()

        binding.apply {

            textWatcher()

            resetPasswordBtn.setOnClickListener {
                val mobile = mobileNo.text.toString().trim()
                if (mobile.isEmpty()) {
                    snackBar(mobileNo, "Please Enter Valid 10 digit Mobile Number")
                } else if (mobile.length != 10) {
                    snackBar(mobileNo, "Please Enter Valid 10 digit Mobile Number")
                } else {
                    UserUtils.setPhoneNo(this@ForgotPasswordScreenOne, mobile)
                    verifyUser()
                }
            }

        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun verifyUser() {
        val forgotPwdVerifyReqModel =
            ForgotPwdVerifyReqModel("", RetrofitBuilder.USER_KEY, UserUtils.getPhoneNo(this))
        viewModel.verifyUser(this, forgotPwdVerifyReqModel).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    startActivity(Intent(this, OTPVerificationScreen::class.java))
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.resetPasswordBtn, "No User found with this Mobile Number. Please try again")
                }
            }
        })
    }

    private fun textWatcher() {
        binding.mobileNo.addTextChangedListener(object : TextWatcher {
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
                    binding.mobileNo.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_passwords,
                        0,
                        R.drawable.ic_greencheck,
                        0
                    )
                }
            }

        })
    }
}