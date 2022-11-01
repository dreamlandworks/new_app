package com.satrango.ui.auth.user_signup.set_password

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivitySetPasswordScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.user_signup.models.UserSignUpModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isForgetPassword
import com.satrango.utils.snackBar

class SetPasswordScreen : AppCompatActivity() {

    private lateinit var viewModel: SetPasswordViewModel
    private lateinit var binding: ActivitySetPasswordScreenBinding
    private lateinit var progressDialog: BeautifulProgressDialog

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
                    password.error = "Please Enter Password"
                    password.requestFocus()
                } else if (cPwd.isEmpty()) {
                    reEnterPassword.error = "Please Enter Confirm Password"
                    reEnterPassword.requestFocus()
                } else if (pwd.isEmpty() && cPwd.isEmpty()) {
                    password.error = "Please Enter Password & Please confirm your password"
                    password.requestFocus()
                } else if (pwd != cPwd) {
                    snackBar(binding.nextBtn, "Passwords don't match. Please enter again")
                } else {
                    UserUtils.setPassword(this@SetPasswordScreen, pwd)
                    if (isForgetPassword(this@SetPasswordScreen)) {
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

        viewModel.resetPassword(this).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                    binding.password.clearFocus()
                    binding.reEnterPassword.clearFocus()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    congratulationsDialog(
                        this,
                        "Your password is successfully set. \nPlease login to continue"
                    )
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    requestFailedDialog(this, it.message!!)
                }
            }

        }
    }

    private fun requestFailedDialog(context: Context, message: String) {
        val dialog = BottomSheetDialog(context)
        val dialogView = layoutInflater.inflate(R.layout.password_failure_dialog, null)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val messageText = dialogView.findViewById<TextView>(R.id.message)
        messageText.text = message
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun congratulationsDialog(context: Context, message: String) {
        val dialog = BottomSheetDialog(context)
        val dialogView = layoutInflater.inflate(R.layout.password_success_dialog, null)
        val loginBtn = dialogView.findViewById<TextView>(R.id.loginBtn)
        val messageText = dialogView.findViewById<TextView>(R.id.message)
        messageText.text = message
        loginBtn.setOnClickListener {
            startActivity(Intent(context, LoginScreen::class.java))
            finish()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun signUpToServer() {
        if (!PermissionUtils.isNetworkConnected(this)) {
            PermissionUtils.connectionAlert(this) { signUpToServer() }
            return
        }
        val requestBody = UserSignUpModel(
            UserUtils.getAddress(this),
            UserUtils.getCity(this),
            UserUtils.getCountry(this),
            UserUtils.getDateOfBirth(this),
            UserUtils.getMail(this),
            UserUtils.getFacebookId(this),
            UserUtils.getFirstName(this),
            UserUtils.getGoogleId(this),
            UserUtils.getLastName(this),
            UserUtils.getPhoneNo(this),
            UserUtils.getPassword(this),
            UserUtils.getPostalCode(this),
            UserUtils.getState(this),
            UserUtils.getTwitterId(this),
            UserUtils.getLatitude(this),
            UserUtils.getLongitude(this),
            UserUtils.getReferralId(this@SetPasswordScreen),
            UserUtils.getGender(this),
            RetrofitBuilder.USER_KEY
        )
//        Log.e("JSON", Gson().toJson(requestBody))
        viewModel.createNewUser(this, requestBody).observe(this@SetPasswordScreen) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.reEnterPassword.clearFocus()
                    binding.password.clearFocus()
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    congratulationsDialog(
                        this,
                        "Your password is successfully set. \nPlease login to continue"
                    )
//                    showCustomDialog()
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
                binding.password.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_passwords,
                    0,
                    R.drawable.ic_greencheck,
                    0
                )
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
                    binding.reEnterPassword.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_passwords,
                        0,
                        R.drawable.ic_greencheck,
                        0
                    )
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