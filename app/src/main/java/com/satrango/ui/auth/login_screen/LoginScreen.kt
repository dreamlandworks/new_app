package com.satrango.ui.auth.login_screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
//import com.facebook.*
//import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
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
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import java.text.SimpleDateFormat
import java.util.*

class LoginScreen : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private val GOOGLE_SIGN_IN: Int = 100
    private lateinit var binding: ActivityLoginScreenBinding

    private lateinit var mGetContent: ActivityResultLauncher<String>
    private lateinit var mGetPermission: ActivityResultLauncher<Intent>

    // Google SignIn Objects
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    // Facebook SignIn Object
    // private lateinit var facebookCallBackManager: CallbackManager
    private lateinit var progressDialog: BeautifulProgressDialog

    private lateinit var firebaseDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url))

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
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

//            facebookSignInBtn.setOnClickListener {
//                facebookSignBtn.performClick()
//            }

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
                UserUtils.FORGOT_PWD = true
                startActivity(Intent(this@LoginScreen, ForgotPasswordScreenOne::class.java))
            }
        }

        mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) toast(this, it.path!!)
        }

        mGetPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                toast(this, "Permission Given In Android 11")
            } else {
                toast(this, "Permission Denied")
            }
        }

//        takePermission()
    }

    private fun takePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", packageName))
                mGetPermission.launch(intent)
            } catch (e: java.lang.Exception) {
                toast(this, e.message!!)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), PermissionUtils.PERMISSIONS_CODE)
        }
    }

    private fun isPermissionGranted(): Boolean {
        var permissionOne = false
        var permissionTwo = false
        var permissionThree = false
        var permissionFour = false
        var permissionFive = false
        var permissionSix = false
        var permissionSeven = false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissionOne = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            permissionTwo = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            permissionThree = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
            permissionFour = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            permissionFive = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            permissionSix = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            permissionSeven = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            permissionOne && permissionTwo && permissionThree && permissionFour && permissionFive && permissionSix && permissionSeven
        }
    }

    private fun takePermission() {
        if (isPermissionGranted()) {
//            mGetContent.launch("pdf/*")
        } else {
            takePermissions()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
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
                    setupFirebaseUserDatabase()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    if (UserUtils.getGoogleId(this).isNotEmpty() || UserUtils.getFacebookId(this).isNotEmpty()) {
                        startActivity(Intent(this, UserSignUpScreenThree::class.java))
                    } else {
                        snackBar(binding.signUpBtn, it.message!!)
                    }
                }
            }
        }
    }

    private fun setupFirebaseUserDatabase() {
        firebaseDatabaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseDatabaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(this@LoginScreen)).child(getString(R.string.user_name)).setValue(UserUtils.getUserName(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(this@LoginScreen)).child(getString(R.string.date_time)).setValue(Date().time)
                firebaseDatabaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(this@LoginScreen)).child(getString(R.string.mobile)).setValue(UserUtils.getPhoneNo(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(this@LoginScreen)).child(getString(R.string.profile_image)).setValue(UserUtils.getUserProfilePic(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(this@LoginScreen)).child(getString(R.string.user_name)).setValue(UserUtils.getUserName(this@LoginScreen))
                firebaseDatabaseReference.child(getString(R.string.users)).child(UserUtils.getUserId(this@LoginScreen)).child(getString(R.string.online_status)).setValue(getString(R.string.online))
                toast(this@LoginScreen, "Success")
                startActivity(Intent(this@LoginScreen, UserLoginTypeScreen::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                toast(this@LoginScreen, error.message)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        facebookCallBackManager.onActivityResult(requestCode, resultCode, data)
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
//        if (PermissionUtils.PERMISSIONS_CODE == requestCode && grantResults.isNotEmpty()) {
//            for (grant in grantResults) {
//                if (grant != PackageManager.PERMISSION_GRANTED) {
//                    PermissionUtils.checkAndRequestPermissions(this)
//                }
//            }
//        }
        if (requestCode == PermissionUtils.PERMISSIONS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mGetContent.launch("pdf/*")
            toast(this, "Permission Granted")
        } else {
//            takePermission()
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
            UserUtils.setFirstName(this, userName!!.split(" ")[0])
            UserUtils.setMail(this, email!!)
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

    override fun onStart() {
        super.onStart()
        if (UserUtils.getLoginCredentials(this@LoginScreen)[resources.getString(R.string.phoneNo)]!!.isNotEmpty() && UserUtils.getLoginCredentials(this@LoginScreen)[resources.getString(R.string.password)]!!.isNotEmpty() && UserUtils.getUserId(this).isNotEmpty()) {
            startActivity(Intent(this@LoginScreen, UserLoginTypeScreen::class.java))
        }
    }

}