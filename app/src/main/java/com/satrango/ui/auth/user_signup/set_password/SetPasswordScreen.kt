package com.satrango.ui.auth.user_signup.set_password

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySetPasswordScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.user_signup.models.UserSignUpModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class SetPasswordScreen : AppCompatActivity() {

    private lateinit var viewModel: SetPasswordViewModel
    private lateinit var binding: ActivitySetPasswordScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProgressDialog()

        val factory = ViewModelFactory(SetPasswordRepository())
        viewModel = ViewModelProvider(this, factory)[SetPasswordViewModel::class.java]

        binding.apply {

            textWatchers()

            nextBtn.setOnClickListener {
                val pwd = password.text.toString().trim()
                val cPwd = reEnterPassword.text.toString().trim()

                if (pwd.isEmpty()) {
                    password.error = "Enter Password"
                    password.requestFocus()
                } else if (cPwd.isEmpty()) {
                    reEnterPassword.error = "Enter Confirm Password"
                    reEnterPassword.requestFocus()
                } else if (pwd != cPwd) {
                  snackBar(binding.nextBtn, "Confirm Password not valid")
                } else {
                    UserUtils.password = pwd
                    if (UserUtils.FORGOT_PWD) {
                        resetPwdOnServer()
                    } else {
                        signUpToServer()
                    }
                }
            }
        }
    }

    private fun resetPwdOnServer() {
        if (!PermissionUtils.isNetworkConnected(this)) {
            PermissionUtils.connectionAlert(this) { resetPwdOnServer() }
            return
        }

        viewModel.resetPassword(this).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                    binding.password.clearFocus()
                    binding.reEnterPassword.clearFocus()
                }
                is NetworkResponse.Success -> {
                    UserUtils.FORGOT_PWD = false
                    startActivity(Intent(this, LoginScreen::class.java))
                    snackBar(binding.nextBtn, it.data!!)
                    progressDialog.dismiss()
                    finish()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }

        })
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    private fun signUpToServer() {
        if (!PermissionUtils.isNetworkConnected(this)) {
            PermissionUtils.connectionAlert(this) { signUpToServer() }
            return
        }
            val requestBody = UserSignUpModel(
                UserUtils.address,
                UserUtils.city,
                UserUtils.country,
                UserUtils.dateOfBirth,
                UserUtils.mailId,
                UserUtils.facebookId,
                UserUtils.firstName,
                UserUtils.googleId,
                UserUtils.lastName,
                UserUtils.phoneNo,
                UserUtils.password,
                UserUtils.postalCode,
                UserUtils.state,
                UserUtils.twitterId,
                UserUtils.latitude,
                UserUtils.longitute,
                UserUtils.getReferralId(this@SetPasswordScreen),
                UserUtils.gender,
                RetrofitBuilder.USER_KEY
            )
            viewModel.createNewUser(this, requestBody).observe(this@SetPasswordScreen) {
                when(it) {
                    is NetworkResponse.Loading -> {
                        binding.reEnterPassword.clearFocus()
                        binding.password.clearFocus()
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        UserUtils.setReferralId(this@SetPasswordScreen, it.data!!)
                        showCustomDialog()
                    }
                    is NetworkResponse.Failure -> {
                        snackBar(binding.nextBtn, it.message!!)
                        progressDialog.dismiss()
                    }
                }

            }
    }

    private fun textWatchers() {
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_passwords, 0, R.drawable.ic_greencheck, 0)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.reEnterPassword.addTextChangedListener(object : TextWatcher {

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
                if (s.toString() == binding.password.text.toString().trim()) {
                    binding.reEnterPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_passwords, 0, R.drawable.ic_greencheck, 0)
                }
            }

        })
    }

    private fun showCustomDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.user_signup_success_dialog, viewGroup, false)
        val loginBtn = dialogView.findViewById<TextView>(R.id.loginBtn)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        closeBtn.visibility = View.GONE
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        loginBtn.setOnClickListener {
            val intent = Intent(this@SetPasswordScreen, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }
        closeBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}