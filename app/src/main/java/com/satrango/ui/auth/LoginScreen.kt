package com.satrango.ui.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.satrango.ui.auth.forgot_password.ForgotPasswordScreenOne
import com.satrango.ui.auth.user_signup.UserSignUpScreenOne
import com.satrango.databinding.ActivityLoginScreenBinding
import com.satrango.utils.PermissionUtils

class LoginScreen : AppCompatActivity() {

    private val GOOGLE_SIGN_IN: Int = 100
    private lateinit var binding: ActivityLoginScreenBinding

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    // Facebook SignIn Object
    private lateinit var facebookCallBackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSocialLogins()
        PermissionUtils.checkAndRequestPermissions(this)

        binding.apply {

            googleSigInBtn.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

            facebookSignInBtn.setOnClickListener {
                facebookSignBtn.performClick()
            }

            signUpBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginScreen,
                        UserSignUpScreenOne::class.java
                    )
                )
            }

            forgetPassword.setOnClickListener { startActivity(Intent(this@LoginScreen, ForgotPasswordScreenOne::class.java)) }

            signInBtn.setOnClickListener { startActivity(Intent(this@LoginScreen, UserLoginTypeScreen::class.java)) }

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
                        val userName = jsonObject!!.getString("name")
                        val userId = jsonObject.getString("id")
                        val profileImage = jsonObject.getString("public_profile")
                        Toast.makeText(
                            this@LoginScreen,
                            "$userId $userName $profileImage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, name")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginScreen, error!!.message, Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallBackManager.onActivityResult(requestCode, resultCode, data);
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
            Toast.makeText(this, "$token $userName $email $googleId", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Google SignIn Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}