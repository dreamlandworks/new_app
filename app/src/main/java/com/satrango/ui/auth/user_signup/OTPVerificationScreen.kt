package com.satrango.ui.auth.user_signup

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.databinding.ActivityOTPVerificationScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.user_signup.models.ForgetPwdOtpReqModel
import com.satrango.ui.auth.user_signup.models.OTPVeriticationModel
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException


class OTPVerificationScreen : AppCompatActivity() {

    private var otp: Int = 0
    private lateinit var binding: ActivityOTPVerificationScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOTPVerificationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProgressDialog()
        requestOTP()

//        otpAutoFill()
        binding.apply {

            otpTextWatchers()

            nextBtn.setOnClickListener {
                val numOne = firstNo.text.toString().trim()
                val numTwo = secondNo.text.toString().trim()
                val numThree = thirdNo.text.toString().trim()
                val numFourth = fourthNo.text.toString().trim()
                if (numOne.isEmpty() || numTwo.isEmpty() || numThree.isEmpty() || numFourth.isEmpty()) {
                    Snackbar.make(firstNo, "Enter OTP to verify", Snackbar.LENGTH_SHORT).show()
                } else {
                    val userOTP = numOne + numTwo + numThree + numFourth
                    if (otp.toString() == userOTP) {
                        startActivity(Intent(this@OTPVerificationScreen, SetPasswordScreen::class.java))
                        finish()
                    } else {
                        Snackbar.make(nextBtn, "Invalid OTP", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            binding.reSendOTP.setOnClickListener {
                requestOTP()
                Snackbar.make(reSendOTP, "OTP Sent", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun requestOTP() {

        CoroutineScope(Main).launch {
            try {
                var response = Any()
                if (UserUtils.FORGOT_PWD) {
                    val requestBody = ForgetPwdOtpReqModel(UserUtils.phoneNo)
                    response = RetrofitBuilder.getRetrofitInstance().userForgetPwdOtpRequest(requestBody)
                    val responseBody = JSONObject(response.string())
                    if (responseBody.getInt("status") == 200) {
                        progressDialog.dismiss()
                        otp = responseBody.getInt("otp")
                        UserUtils.USER_ID = responseBody.getString("id")
                    } else {
                        Snackbar.make(binding.fourthNo, "Something went wrong", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    val requestBody = OTPVeriticationModel(
                        UserUtils.firstName,
                        UserUtils.lastName,
                        UserUtils.phoneNo
                    )
                    response = RetrofitBuilder.getRetrofitInstance().userRequestOTP(requestBody)
                    val responseBody = JSONObject(response.string())
                    if (responseBody.getInt("status") == 200) {
                        progressDialog.dismiss()
                        otp = responseBody.getInt("otp")
                    } else {
                        Snackbar.make(binding.fourthNo, "Something went wrong", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                progressDialog.dismiss()
                Snackbar.make(binding.fourthNo, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                progressDialog.dismiss()
                Snackbar.make(binding.fourthNo, "Something went wrong", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: ConnectException) {
                progressDialog.dismiss()
                Snackbar.make(binding.fourthNo, "Please Check Internet", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                progressDialog.dismiss()
                Snackbar.make(binding.fourthNo, "Something went wrong", Snackbar.LENGTH_SHORT)
                    .show()
            }

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