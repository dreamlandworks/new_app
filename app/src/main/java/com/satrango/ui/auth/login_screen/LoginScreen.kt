package com.satrango.ui.auth.login_screen

//import com.facebook.*
//import com.facebook.login.LoginResult
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
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
import com.satrango.utils.UserUtils.isForgetPassword
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import com.truecaller.android.sdk.*
import java.util.*

class LoginScreen : AppCompatActivity() {

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


    // Facebook SignIn Object
    // private lateinit var facebookCallBackManager: CallbackManager
    private lateinit var progressDialog: BeautifulProgressDialog

    private lateinit var firebaseDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getString(R.string.firebase_database_reference_url))

        initializeSocialLogins()
        PermissionUtils.checkAndRequestPermissions(this)
        initializeProgressDialog()

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
//            Log.e("Error" + Thread.currentThread().stackTrace[2], paramThrowable.localizedMessage)
        }

        val factory = ViewModelFactory(LoginRepository())
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.apply {

            val userCredentials = UserUtils.getLoginCredentials(this@LoginScreen)
            if (userCredentials[resources.getString(R.string.phoneNo)]!!.isNotEmpty()) {
                mobileNo.setText(userCredentials[resources.getString(R.string.phoneNo)])
                password.setText(userCredentials[resources.getString(R.string.password)])
            }

            googleSigInBtn.setOnClickListener {
//                val signInRequest = BeginSignInRequest.builder()
//                    .setGoogleIdTokenRequestOptions(
//                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                            .setSupported(true)
//                             Your server's client ID, not your Android client ID.
//                            .setServerClientId(getString(R.string.your_web_client_id))
//                             Only show accounts previously used to sign in.
//                            .setFilterByAuthorizedAccounts(true)
//                            .build())
                mAuth = FirebaseAuth.getInstance()
//                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestEmail()
//                    .build()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("175510734309-kamrkdlu5359pdi4jos8sv6voicg8qqa.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
                mGoogleSignInClient = GoogleSignIn.getClient(this@LoginScreen, gso)
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(
                    signInIntent,
                    RC_SIGN_IN
                )
//                val signInIntent = googleSignInClient.signInIntent
//                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
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
                        mobileNo.error = "Please Enter Valid 10 digit Mobile number"
                        mobileNo.requestFocus()
                    }
                    phoneNo.length != 10 -> {
                        mobileNo.error = "Please Enter Valid 10 digit Mobile number"
                        mobileNo.requestFocus()
                    }
                    pwd.isEmpty() -> {
                        password.error = "Please Enter Password"
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
                isForgetPassword(this@LoginScreen, true)
                startActivity(Intent(this@LoginScreen, ForgotPasswordScreenOne::class.java))
            }

            trueCallerBtn.setOnClickListener {
                UserUtils.setMail(this@LoginScreen, "")
                if (TruecallerSDK.getInstance().isUsable) {
                    TruecallerSDK.getInstance().getUserProfile(this@LoginScreen)
                }
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

//        takePermission()

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
            UserUtils.setGoogleId(this@LoginScreen, "")
            UserUtils.setFacebookId(this@LoginScreen, "trueCaller")
            UserUtils.setFirstName(this@LoginScreen, data.firstName)
            UserUtils.setMail(this@LoginScreen, data.email)
            UserUtils.setLastName(this@LoginScreen, data.lastName)
            UserUtils.setPhoneNo(this@LoginScreen, data.phoneNumber.takeLast(10))
            loginToServer(
                data.phoneNumber.takeLast(10),
                "",
                resources.getString(R.string.userLogin)
            )
        }

        override fun onFailureProfileShared(error: TrueError) {
            toast(this@LoginScreen, "Error01" + error.errorType.toString())
        }

        override fun onVerificationRequired(error: TrueError?) {
            toast(this@LoginScreen, "Error02" + error!!.errorType.toString())
        }

    }

//    private fun takePermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse(String.format("package:%s", packageName))
//                mGetPermission.launch(intent)
//            } catch (e: java.lang.Exception) {
//                toast(this, e.message!!)
//            }
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_NETWORK_STATE,
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.RECORD_AUDIO
//                ),
//                PermissionUtils.PERMISSIONS_CODE
//            )
//        }
//    }

//    private fun isPermissionGranted(): Boolean {
//        var permissionOne = false
//        var permissionTwo = false
//        var permissionThree = false
//        var permissionFour = false
//        var permissionFive = false
//        var permissionSix = false
//        var permissionSeven = false
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            permissionOne = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//            permissionTwo = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) == PackageManager.PERMISSION_GRANTED
//            permissionThree = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_NETWORK_STATE
//            ) == PackageManager.PERMISSION_GRANTED
//            permissionFour = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//            permissionFive = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//            permissionSix = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//            permissionSeven = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Environment.isExternalStorageManager()
//        } else {
//            permissionOne && permissionTwo && permissionThree && permissionFour && permissionFive && permissionSix && permissionSeven
//        }
//    }

//    private fun takePermission() {
//        if (isPermissionGranted()) {
//            mGetContent.launch("pdf/*")
//        } else {
//            takePermissions()
//        }
//    }

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

    private fun loginToServer(phoneNo: String, password: String, type: String) {

        if (!PermissionUtils.isNetworkConnected(this@LoginScreen)) {
            PermissionUtils.connectionAlert(this) { loginToServer(phoneNo, password, type) }
            return
        }

        val requestBody = UserLoginModel(phoneNo, password, type, RetrofitBuilder.USER_KEY)
        viewModel.userLogin(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    binding.mobileNo.clearFocus()
                    binding.password.clearFocus()
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    UserUtils.setUserLoggedInVia(this, type, it.data!!)
                    UserUtils.setPhoneNo(this, phoneNo)
                    progressDialog.dismiss()
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
                toast(this@LoginScreen, "Success")
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@LoginScreen, error.message)
            }

        })
    }

    private fun initializeSocialLogins() {
//        mAuth = FirebaseAuth.getInstance()
        // Google SignIn Object Initialization
//        googleSignInOptions = GoogleSignInOptions
//            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestIdToken("175510734309-fsc4plk38o4hhfhbmp237tngccn795p5.apps.googleusercontent.com")
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Facebook SignIn Object Initialization
//        facebookCallBackManager = CallbackManager.Factory.create()

//        binding.facebookSignBtn.registerCallback(
//            facebookCallBackManager,
//            object : FacebookCallback<LoginResult> {
//
//                override fun onSuccess(result: LoginResult?) {
//                    val token = result!!.accessToken
//                    val request = GraphRequest.newMeRequest(
//                        token
//                    ) { jsonObject, _ ->
//                        val userId = jsonObject.getString("id")
//                        val userName = jsonObject.getString("name")
//                        UserUtils.setFacebookId(this@LoginScreen, userId)
//                        UserUtils.setGoogleId(this@LoginScreen, "")
//                        UserUtils.setFirstName(this@LoginScreen, userName.split(" ")[0])
//                        try {
//                            UserUtils.setLastName(this@LoginScreen, userName.split(" ")[1])
//                        } catch (e: IndexOutOfBoundsException) {
//                        }
//                        loginToServer(userId, "", resources.getString(R.string.userFacebookLogin))
//                    }
//                    val parameters = Bundle()
//                    parameters.putString("fields", "id, name")
//                    request.parameters = parameters
//                    request.executeAsync()
//                }
//
//                override fun onCancel() {
//                    snackBar(binding.signUpBtn, "Login Cancelled")
//                }
//
//                override fun onError(error: FacebookException?) {
//                    snackBar(binding.signUpBtn, error!!.message!!)
//                }
//
//            })

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        facebookCallBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!.idToken)
            } catch (e: ApiException) {

                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
//        if (requestCode == GOOGLE_SIGN_IN) {
//            val task: com.google.android.gms.tasks.Task<GoogleSignInAccount> =
//                GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
//                validateGoogleSignInResult(account.idToken)
//            } catch (e: ApiException) {
//                toast(this, "Error01 " + e.message!!)
//            }
//        } else {
//            TruecallerSDK.getInstance()
//                .onActivityResultObtained(this, requestCode, resultCode, data)
//        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    toast(this, user!!.email!!)
                } else {
                    Toast.makeText(
                        this,
                        "Failed to login. Try again later!",
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
//        else {
//            takePermission()
//        }
    }

    private fun validateGoogleSignInResult(result: String?) {
        val credential = GoogleAuthProvider.getCredential(result!!, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val account = mAuth!!.currentUser
                    val userName = account!!.displayName
                    val email = account.email
                    val googleId = account.tenantId
                    val image = account.photoUrl
                    UserUtils.setGoogleId(this, googleId!!)
                    UserUtils.setFacebookId(this, "")
                    UserUtils.setFirstName(this, userName!!.split(" ")[0])
                    UserUtils.setMail(this, email!!)
                    try {
                        UserUtils.setLastName(this, userName.split(" ")[1])
                    } catch (e: IndexOutOfBoundsException) {
                    }
                    loginToServer(email, "", resources.getString(R.string.userGoogleLogin))
                } else {
                    snackBar(binding.signUpBtn, "Google SignIn Failed:${task.exception!!.message}")
                }
            }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onStart() {
        super.onStart()
        if (UserUtils.getLoginCredentials(this@LoginScreen)[resources.getString(R.string.phoneNo)]!!.isNotEmpty() && UserUtils.getLoginCredentials(
                this@LoginScreen
            )[resources.getString(R.string.password)]!!.isNotEmpty() && UserUtils.getUserId(this)
                .isNotEmpty()
        ) {
            startActivity(Intent(this@LoginScreen, UserLoginTypeScreen::class.java))
        }
    }

}