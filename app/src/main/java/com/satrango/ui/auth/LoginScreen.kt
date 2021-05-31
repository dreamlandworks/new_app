package com.satrango.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivityLoginScreenBinding
import com.satrango.databinding.UserDashboardHeaderBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.forgot_password.ForgotPasswordScreenOne
import com.satrango.ui.auth.models.user_signup.UserLoginModel
import com.satrango.ui.auth.user_signup.UserSignUpScreenOne
import com.satrango.ui.user_dashboard.UserDashboardScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginScreen : AppCompatActivity() {

    private val GOOGLE_SIGN_IN: Int = 100
    private lateinit var binding: ActivityLoginScreenBinding

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    // Facebook SignIn Object
    private lateinit var facebookCallBackManager: CallbackManager

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSocialLogins()
        PermissionUtils.checkAndRequestPermissions(this)
        initializeProgressDialog()

        binding.apply {

            googleSigInBtn.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

            facebookSignInBtn.setOnClickListener {
                facebookSignBtn.performClick()
            }

            signUpBtn.setOnClickListener {
                UserUtils.FORGOT_PWD = false
                startActivity(
                    Intent(
                        this@LoginScreen,
                        UserSignUpScreenOne::class.java
                    )
                )
            }

            signInBtn.setOnClickListener {
                val phoneNo = mobileNo.text.toString().trim()
                val pwd = password.text.toString().trim()

                if (phoneNo.isEmpty()) {
                    mobileNo.error = "Enter Phone number"
                    mobileNo.requestFocus()
                } else if (pwd.isEmpty()) {
                    password.error = "Enter Password"
                    password.requestFocus()
                } else {
                    loginToServer(phoneNo, pwd, "login")
                }

            }

            forgetPassword.setOnClickListener {
                UserUtils.FORGOT_PWD = true
                startActivity(Intent(this@LoginScreen, ForgotPasswordScreenOne::class.java))
            }

        }

    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }

    private fun loginToServer(phoneNo: String, password: String, type: String) {
        binding.mobileNo.clearFocus()
        binding.password.clearFocus()
        progressDialog.show()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = UserLoginModel(phoneNo, password, type)
                val response = RetrofitBuilder.getRetrofitInstance().login(requestBody)
                val jsonResponse = JSONObject(response.string())
                progressDialog.dismiss()
                if (jsonResponse.getInt("status") == 200) {
                    UserUtils.setUserLoggedInVia(this@LoginScreen, type, jsonResponse.getString("User id"))
                    startActivity(Intent(this@LoginScreen, UserDashboardScreen::class.java))
                } else {
                    Snackbar.make(binding.password, "Invalid Credentials", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                progressDialog.dismiss()
                Snackbar.make(binding.password, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                progressDialog.dismiss()
                Snackbar.make(binding.password, "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
            } catch (e: SocketTimeoutException) {
                progressDialog.dismiss()
                Snackbar.make(binding.password, "Please check internet Connection", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun initializeSocialLogins() {

        // Google SignIn Object Initialization
        googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Facebook SignIn Object Initialization
        facebookCallBackManager = CallbackManager.Factory.create()

        binding.facebookSignBtn.registerCallback(
            facebookCallBackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    val token = result!!.accessToken
                    val request = GraphRequest.newMeRequest(
                        token
                    ) { jsonObject, _ ->
                        val userId = jsonObject.getString("id")
                        loginToServer(userId, "", resources.getString(R.string.userFacebookLogin))
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, name")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginScreen, "Login Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginScreen, error!!.message, Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            try {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                validateGoogleSignInResult(result)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.PERMISSIONS_CODE == requestCode && grantResults.isNotEmpty()) {
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    PermissionUtils.checkAndRequestPermissions(this)
                }
            }
        }
    }

    private fun validateGoogleSignInResult(result: GoogleSignInResult?) {
        if (result!!.isSuccess) {
            val account = result.signInAccount
            val token = account!!.idToken
            val userName = account.displayName
            val email = account.email
            val googleId = account.id
            val image = account.photoUrl
            loginToServer(email!!, "", resources.getString(R.string.userGoogleLogin))
        } else {
            Toast.makeText(this, "Google SignIn Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


}