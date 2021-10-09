package com.satrango.ui.auth.login_screen

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityLoginScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.ui.auth.forgot_password.ForgotPasswordScreenOne
import com.satrango.ui.auth.user_signup.UserSignUpScreenOne
import com.satrango.ui.auth.user_signup.UserSignUpScreenThree
import com.satrango.ui.auth.user_signup.models.UserLoginModel
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar

class LoginScreen : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private val GOOGLE_SIGN_IN: Int = 100
    private lateinit var binding: ActivityLoginScreenBinding

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    // Facebook SignIn Object
    private lateinit var facebookCallBackManager: CallbackManager
    private lateinit var progressDialog: BeautifulProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSocialLogins()
        PermissionUtils.checkAndRequestPermissions(this)
        initializeProgressDialog()

        val factory = ViewModelFactory(LoginRepository())
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.apply {

            val userCredentials = UserUtils.getLoginCredentials(this@LoginScreen)
            if (userCredentials[resources.getString(R.string.phoneNo)]!!.isNotEmpty()) {
                mobileNo.setText(userCredentials[resources.getString(R.string.phoneNo)])
                password.setText(userCredentials[resources.getString(R.string.password)])
            }

            googleSigInBtn.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

            facebookSignInBtn.setOnClickListener {
                facebookSignBtn.performClick()
            }

            signUpBtn.setOnClickListener {
                UserUtils.FORGOT_PWD = false
                startActivity(Intent(this@LoginScreen, UserSignUpScreenOne::class.java))
            }

            signInBtn.setOnClickListener {
                val phoneNo = mobileNo.text.toString().trim()
                val pwd = password.text.toString().trim()

                if (rememberMe.isChecked) {
                    UserUtils.saveLoginCredentials(this@LoginScreen, phoneNo, pwd)
                } else {
                    UserUtils.deleteUserCredentials(this@LoginScreen)
                }

                when {
                    phoneNo.isEmpty() -> {
                        mobileNo.error = "Enter Phone number"
                        mobileNo.requestFocus()
                    }
                    phoneNo.length != 10 -> {
                        mobileNo.error = "Enter Valid Phone number"
                        mobileNo.requestFocus()
                    }
                    pwd.isEmpty() -> {
                        password.error = "Enter Password"
                        password.requestFocus()
                    }
                    else -> {
                        UserUtils.setGoogleId(this@LoginScreen, "")
                        UserUtils.setFacebookId(this@LoginScreen, "")
                        loginToServer(phoneNo, pwd, "login")
                    }
                }

            }

            forgetPassword.setOnClickListener {
                UserUtils.FORGOT_PWD = true
                startActivity(Intent(this@LoginScreen, ForgotPasswordScreenOne::class.java))
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withImage, resources.getString(R.string.loading))
        progressDialog.setImageLocation(resources.getDrawable(R.drawable.circlelogo))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    private fun loginToServer(phoneNo: String, password: String, type: String) {

        if (!PermissionUtils.isNetworkConnected(this@LoginScreen)) {
            PermissionUtils.connectionAlert(this) { loginToServer(phoneNo, password, type) }
            return
        }

        val requestBody = UserLoginModel(phoneNo, password, type, RetrofitBuilder.USER_KEY)
        viewModel.userLogin(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.mobileNo.clearFocus()
                    binding.password.clearFocus()
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    UserUtils.setUserLoggedInVia(this, type, it.data!!)
                    progressDialog.dismiss()
                    startActivity(Intent(this, UserLoginTypeScreen::class.java))
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    if (UserUtils.getGoogleId(this).isNotEmpty() || UserUtils.getFacebookId(this)
                            .isNotEmpty()
                    ) {
                        startActivity(Intent(this, UserSignUpScreenThree::class.java))
                    } else {
                        snackBar(binding.signUpBtn, it.message!!)
                    }
                }
            }
        })
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
                        val userName = jsonObject.getString("name")
                        UserUtils.setFacebookId(this@LoginScreen, userId)
                        UserUtils.setGoogleId(this@LoginScreen, "")
                        UserUtils.setFirstName(this@LoginScreen, userName.split(" ")[0])
                        try {
                            UserUtils.setLastName(this@LoginScreen, userName.split(" ")[1])
                        } catch (e: IndexOutOfBoundsException) {
                        }
                        loginToServer(userId, "", resources.getString(R.string.userFacebookLogin))
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, name")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    snackBar(binding.signUpBtn, "Login Cancelled")
                }

                override fun onError(error: FacebookException?) {
                    snackBar(binding.signUpBtn, error!!.message!!)
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
                snackBar(binding.signUpBtn, e.message!!)
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
            UserUtils.setGoogleId(this, googleId!!)
            UserUtils.setFacebookId(this, "")
            UserUtils.setFirstName(this, userName.split(" ")[0])
            UserUtils.setMail(this, email)
            try {
                UserUtils.setLastName(this, userName.split(" ")[1])
            } catch (e: IndexOutOfBoundsException) {
            }
            loginToServer(email!!, "", resources.getString(R.string.userGoogleLogin))
        } else {
            snackBar(binding.signUpBtn, "Google SignIn Failed")
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}