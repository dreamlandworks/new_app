package com.satrango.ui.auth.login_screen

//import com.facebook.*
//import com.facebook.login.LoginResult

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
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
import com.satrango.ui.auth.user_signup.otp_verification.OTPVerificationScreen
import com.satrango.utils.*
import com.satrango.utils.Constants.login
import com.satrango.utils.Constants.password_alert
import com.satrango.utils.Constants.phone_no_alert
import com.satrango.utils.UserUtils.isForgetPassword
import com.truecaller.android.sdk.*
import com.truecaller.android.sdk.clients.VerificationCallback
import com.truecaller.android.sdk.clients.VerificationDataBundle
import com.zoho.livechat.android.constants.SalesIQConstants.LOG_TAG
import java.util.*


class LoginScreen : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private val RC_SIGN_IN: Int = 111
    private lateinit var viewModel: LoginViewModel
    private val GOOGLE_SIGN_IN: Int = 110
    private lateinit var binding: ActivityLoginScreenBinding

    private lateinit var mGetContent: ActivityResultLauncher<String>
    private lateinit var mGetPermission: ActivityResultLauncher<Intent>

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    private var mGoogleSignInClient: GoogleSignInClient? = null

    private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth

    // Facebook SignIn Object
    // private lateinit var facebookCallBackManager: CallbackManager
    private lateinit var progressDialog: BeautifulProgressDialog

    private lateinit var firebaseDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        firebaseDatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))

        PermissionUtils.checkAndRequestPermissions(this)
        initializeProgressDialog()

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
//            Log.e("Error" + Thread.currentThread().stackTrace[2], paramThrowable.localizedMessage)
        }

        val factory = ViewModelFactory(LoginRepository())
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        auth = Firebase.auth

        binding.apply {

            val userCredentials = UserUtils.getLoginCredentials(this@LoginScreen)
            if (userCredentials[StorageConstants.phone_no]!!.isNotEmpty()) {
                mobileNo.setText(userCredentials[StorageConstants.phone_no])
                password.setText(userCredentials[StorageConstants.password])
            }

            googleSigInBtn.setOnClickListener {
                mAuth = FirebaseAuth.getInstance()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("175510734309-3h29lbrph7vt491g2527van9rua1nrs0.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
                mGoogleSignInClient = GoogleSignIn.getClient(this@LoginScreen, gso)
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

//            facebookSignInBtn.setOnClickListener {
//                facebookSignBtn.performClick()
//            }

            signUpBtn.setOnClickListener {
                isForgetPassword(this@LoginScreen, false)
                UserUtils.setMail(this@LoginScreen, "")
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
                        mobileNo.error = phone_no_alert
                        mobileNo.requestFocus()
                    }
                    phoneNo.length != 10 -> {
                        mobileNo.error = phone_no_alert
                        mobileNo.requestFocus()
                    }
                    pwd.isEmpty() -> {
                        password.error = password_alert
                        password.requestFocus()
                    }
                    else -> {
                        UserUtils.setGoogleId(this@LoginScreen, "")
                        UserUtils.setFacebookId(this@LoginScreen, "")
                        loginToServer(phoneNo, pwd, login)
                    }
                }

            }

            forgetPassword.setOnClickListener {
                isForgetPassword(this@LoginScreen, true)
                startActivity(Intent(this@LoginScreen, ForgotPasswordScreenOne::class.java))
            }

            trueCallerBtn.setOnClickListener {
                UserUtils.setMail(this@LoginScreen, "")
                TruecallerSDK.getInstance().getUserProfile(this@LoginScreen)
            }
        }

        mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) toast(this, it.path!!)
        }

        mGetPermission =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    toast(this, "Permission Given In Android 11")
                } else {
                    toast(this, "Permission Denied")
                }
            }

        val trueScope = TruecallerSdkScope.Builder(this, trueCallerCallback)
            .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
            .buttonColor(Color.MAGENTA)
            .buttonTextColor(Color.WHITE)
            .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
            .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
            .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
            .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
            .privacyPolicyUrl("https://www.squill.in/privacy")
            .termsOfServiceUrl("https://www.squill.in/privacy")
            .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
            .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
            .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITH_OTP)
            .build()
        TruecallerSDK.init(trueScope)
    }

    val trueCallerCallback = object : ITrueCallback {
        override fun onSuccessProfileShared(data: TrueProfile) {
//            toast(this@LoginScreen, "Truecaller Success")
            UserUtils.setGoogleId(this@LoginScreen, "")
            UserUtils.setFacebookId(this@LoginScreen, "trueCaller")
            UserUtils.setFirstName(this@LoginScreen, data.firstName)
            UserUtils.setMail(this@LoginScreen, data.email)
            UserUtils.setLastName(this@LoginScreen, data.lastName)
            UserUtils.setPhoneNo(this@LoginScreen, data.phoneNumber.takeLast(10))
            loginToServer(data.phoneNumber.takeLast(10), "", resources.getString(R.string.userTrueCallerLogin))
        }

        override fun onFailureProfileShared(error: TrueError) {
            toast(this@LoginScreen, "Error01" + error.errorType.toString())
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("HardwareIds")
        override fun onVerificationRequired(error: TrueError?) {
            askPermissionAndGetPhoneNumbers()
//            toast(this@LoginScreen, "Error02" + error!!.errorType.toString())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun askPermissionAndGetPhoneNumbers() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 23
            val readPhoneStatePermission =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//            val readSMSStatePermission =
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            val readPhoneNosStatePermission =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
            if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED ||
//                readSMSStatePermission != PackageManager.PERMISSION_GRANTED ||
                readPhoneNosStatePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS), 100)
                return
            }
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getPhoneNumbers() {
        try {
            val manager = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val phoneNumber1 = manager.line1Number
            try {
                TruecallerSDK.getInstance().requestVerification("IN", phoneNumber1, apiCallback, this)
//                toast(this, phoneNumber1)
            } catch (e: RuntimeException) {
                toast(this, e.message.toString())
            }
        } catch (ex: java.lang.Exception) {
            Log.e(LOG_TAG, "Error: ", ex)
            Toast.makeText(
                this, "Error: " + ex.message,
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }
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

    @SuppressLint("HardwareIds")
    private fun loginToServer(phoneNo: String, password: String, type: String) {

        if (!PermissionUtils.isNetworkConnected(this@LoginScreen)) {
            PermissionUtils.connectionAlert(this) { loginToServer(phoneNo, password, type) }
            return
        }

        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val requestBody = UserLoginModel(phoneNo, password, androidId, type, RetrofitBuilder.USER_KEY)
        viewModel.userLogin(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.mobileNo.clearFocus()
                    binding.password.clearFocus()
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
//                    toast(this, "Login success")
                    UserUtils.setUserLoggedInVia(this, type, it.data!!)
                    UserUtils.setPhoneNo(this, phoneNo)
                    progressDialog.dismiss()
                    UserUtils.setLatitude(this, "0")
                    UserUtils.setLongitude(this, "0")
                    UserUtils.setCity(this, "")
                    startActivity(Intent(this@LoginScreen, UserLoginTypeScreen::class.java))
                    setupFirebaseUserDatabase()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    if (UserUtils.getGoogleId(this).isNotEmpty() || UserUtils.getFacebookId(this)
                            .isNotEmpty()
                    ) {
                        startActivity(Intent(this, UserSignUpScreenThree::class.java))
                    } else {
                        snackBar(binding.signUpBtn, "Invalid Credentials")
                    }
                }
            }
        }
    }

    private fun setupFirebaseUserDatabase() {
        firebaseDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseDatabaseReference.child(getString(R.string.users))
                    .child(UserUtils.getUserId(this@LoginScreen))
                    .child(getString(R.string.user_name))
                    .setValue(UserUtils.getUserName(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users))
                    .child(UserUtils.getUserId(this@LoginScreen))
                    .child(getString(R.string.date_time)).setValue(Date().time)
                firebaseDatabaseReference.child(getString(R.string.users))
                    .child(UserUtils.getUserId(this@LoginScreen))
                    .child(getString(R.string.mobile))
                    .setValue(UserUtils.getPhoneNo(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users))
                    .child(UserUtils.getUserId(this@LoginScreen))
                    .child(getString(R.string.profile_image))
                    .setValue(UserUtils.getUserProfilePic(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users))
                    .child(UserUtils.getUserId(this@LoginScreen))
                    .child(getString(R.string.user_name))
                    .setValue(UserUtils.getUserName(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users))
                    .child(UserUtils.getUserId(this@LoginScreen))
                    .child(getString(R.string.online_status)).setValue(getString(R.string.online))
//                toast(this@LoginScreen, "Success")
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@LoginScreen, error.message)
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        facebookCallBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
            TruecallerSDK.getInstance()
                .onActivityResultObtained(this, requestCode, resultCode, data)
        }
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!.idToken)
            } catch (e: ApiException) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    val user = mAuth!!.currentUser
                    val account = mAuth!!.currentUser
                    val userName = account!!.displayName
                    val email = account.email
                    val googleId = account.uid
                    val image = account.photoUrl
//                    toast(this, account.email!!)
                    UserUtils.setGoogleId(this, googleId)
                    UserUtils.setFacebookId(this, "")
                    UserUtils.setMail(this, email!!)
                    try {
                        UserUtils.setFirstName(this, userName!!.split(" ")[0])
                    } catch (e: Exception) {
                    }
                    try {
                        UserUtils.setLastName(this, userName!!.split(" ")[1])
                    } catch (e: IndexOutOfBoundsException) {
                    }
                    loginToServer(email, "", resources.getString(R.string.userGoogleLogin))
                } else {
                    Toast.makeText(
                        this,
                        "Failed to login. Try again later!: ${task.exception!!.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtils.PERMISSIONS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mGetContent.launch("pdf/*")
            toast(this, "Permission Granted")
        }
        when (requestCode) {
            100 -> {
                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (SEND_SMS).
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show()
                    getPhoneNumbers()
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onStart() {
        super.onStart()
        if (UserUtils.getLoginCredentials(this@LoginScreen)[StorageConstants.phone_no]!!.isNotEmpty() && UserUtils.getLoginCredentials(
                this@LoginScreen
            )[StorageConstants.password]!!.isNotEmpty() && UserUtils.getUserId(this)
                .isNotEmpty()
        ) {
            UserUtils.setLatitude(this, "0")
            UserUtils.setLongitude(this, "0")
            UserUtils.setCity(this, "")
            startActivity(Intent(this@LoginScreen, UserLoginTypeScreen::class.java))
        }
    }

    val apiCallback = object : VerificationCallback {

        override fun onRequestSuccess(requestCode: Int, extras: VerificationDataBundle?) {
            if (requestCode == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
                toast(this@LoginScreen, "TYPE_MISSED_CALL_INITIATED:${extras?.getString(VerificationDataBundle.KEY_TTL)}")
            }

            if (requestCode == VerificationCallback.TYPE_MISSED_CALL_RECEIVED) {
                toast(this@LoginScreen, "TYPE_MISSED_CALL_RECEIVED")
            }

            if (requestCode == VerificationCallback.TYPE_OTP_INITIATED) {
                toast(this@LoginScreen, "TYPE_OTP_INITIATED:${extras?.getString(VerificationDataBundle.KEY_TTL)}")
            }

            if (requestCode == VerificationCallback.TYPE_OTP_RECEIVED) {
                toast(this@LoginScreen, "TYPE_OTP_RECEIVED")
            }

            if (requestCode == VerificationCallback.TYPE_VERIFICATION_COMPLETE) {
                toast(this@LoginScreen, "TYPE_VERIFICATION_COMPLETE")
            }

            if (requestCode == VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE) {
                toast(this@LoginScreen, "TYPE_PROFILE_VERIFIED_BEFORE")
            }
        }

        override fun onRequestFailure(p0: Int, p1: TrueException) {
            toast(this@LoginScreen, "OnRequestFailure:" + p1.exceptionMessage)
        }

    }

}