package com.satrango.ui.auth.user_signup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivitySetPasswordScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.LoginScreen
import com.satrango.ui.auth.models.user_signup.UserSignUpModel
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class SetPasswordScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySetPasswordScreenBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProgressDialog()
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
                } else {
                    UserUtils.password = pwd
                    signUpToServer()
                }
            }

        }

    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    private fun signUpToServer() {
        progressDialog.show()
        binding.password.clearFocus()
        binding.reEnterPassword.clearFocus()
        CoroutineScope(Dispatchers.Main).launch {
            try {
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
                    UserUtils.longitute
                )
                val response = RetrofitBuilder.getRetrofitInstance().userSignUp(requestBody)
                val responseObject = JSONObject(response.string())
                if (responseObject.getInt("status") == 200) {
                    progressDialog.dismiss()
                    showCustomDialog()
                } else {
                    Snackbar.make(
                        binding.nextBtn,
                        responseObject.getString("message"),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                progressDialog.dismiss()
                Snackbar.make(binding.password, "User Already Exist", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                progressDialog.dismiss()
                Snackbar.make(binding.password, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: SocketTimeoutException) {
                progressDialog.dismiss()
                Snackbar.make(
                    binding.password,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
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
                    0,
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
                        0,
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