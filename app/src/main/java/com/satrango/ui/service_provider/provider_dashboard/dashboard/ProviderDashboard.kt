package com.satrango.ui.service_provider.provider_dashboard.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderDashboardBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.ui.auth.login_screen.LoginRepository
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.login_screen.LoginViewModel
import com.satrango.ui.auth.login_screen.LogoutReqModel
import com.satrango.ui.auth.provider_signup.ProviderSignUpSeven
import com.satrango.ui.auth.provider_signup.ProviderSignUpSix
import com.satrango.ui.auth.provider_signup.provider_sign_up_five.ProviderSignUpFive
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOne
import com.satrango.ui.service_provider.provider_dashboard.ProviderLocationReqModel
import com.satrango.ui.service_provider.provider_dashboard.ProviderRejectBookingScreen
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderMyAccountScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.ProviderMyBidsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderMyBookingsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.profile.ProviderProfileScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.ProviderMyTrainingScreen
import com.satrango.ui.service_provider.provider_dashboard.home.ProviderHomeScreen
import com.satrango.ui.service_provider.provider_dashboard.models.ProviderOnlineReqModel
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOffersScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.bookings.view_booking_details.models.ProviderResponseReqModel
import com.satrango.ui.user.user_dashboard.UserChatScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*


class ProviderDashboard : AppCompatActivity() {

    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackBtn: ImageView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var userProviderSwitch: SwitchCompat
    private lateinit var response: BookingDetailsResModel
    private lateinit var viewModel: ProviderDashboardViewModel
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var binding: ActivityProviderDashboardBinding

    private var flag: Boolean = true
    private lateinit var backStack: Deque<Int>

    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        var FROM_FCM_SERVICE = false
        var minutes = 2
        var seconds = 59
        var progressTime = 180
        var bookingId = ""
        var bottomSheetDialog: BottomSheetDialog? = null
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }

        initializeProgressDialog()

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
            Log.e("Error" + Thread.currentThread().stackTrace[2], paramThrowable.localizedMessage!!)
        }

        val factory = ViewModelFactory(ProviderDashboardRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderDashboardViewModel::class.java]

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolBar,
            R.string.app_name,
            R.string.app_name
        )
        binding.navigationView.itemIconTintList = null
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.black)
        binding.drawerLayout.addDrawerListener(toggle)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val params = binding.navigationView.layoutParams
        params.width = metrics.widthPixels
        binding.navigationView.layoutParams = params
        toggle.syncState()

        val headerView = binding.navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.profileImage)
        userProviderSwitch = headerView.findViewById(R.id.userProviderSwitch)
        referralId = headerView.findViewById(R.id.referralId)
        toolBarTitle = headerView.findViewById(R.id.toolBarTitle)
        toolBarBackTVBtn = headerView.findViewById(R.id.toolBarBackTVBtn)
        toolBarBackBtn = headerView.findViewById(R.id.toolBarBackBtn)
        updateHeaderDetails()
        loadProfileImage(binding.image)
        userProviderSwitch.isEnabled = true
        userProviderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                finish()
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
        }

        backStack = ArrayDeque(4)
        backStack.push(R.id.navigation_home)
        loadFragment(ProviderHomeScreen())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            val id = it.itemId
            if (backStack.contains(id)) {
                if (id == R.id.navigation_home) {
                    if (backStack.size != 1) {
                        if (flag) {
                            backStack.addFirst(R.id.navigation_home)
                            flag = false
                        }
                    }
                }
                backStack.remove(id)
            }
            backStack.push(id)
            loadFragment(getFragment(it.itemId))
            true
        }


        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.providerOptHome -> {
                    loadFragment(ProviderHomeScreen())
                }
                R.id.providerOptMyAccount -> {
                    startActivity(Intent(this, ProviderMyAccountScreen::class.java))
                }
                R.id.providerOptMyBooking -> {
                    startActivity(Intent(this, ProviderMyBookingsScreen::class.java))
                }
                R.id.providerOptMyBids -> {
                    UserUtils.saveSearchFilter(this, "")
                    startActivity(Intent(this, ProviderMyBidsScreen::class.java))
                }
                R.id.providerOptMyProfile -> {
                    startActivity(Intent(this, ProviderProfileScreen::class.java))
                }
                R.id.providerOptTraining -> {
                    startActivity(Intent(this, ProviderMyTrainingScreen::class.java))
                }
                R.id.providerOptSettings -> {
                    UserSettingsScreen.FROM_PROVIDER = true
                    startActivity(Intent(this, UserSettingsScreen::class.java))
                }
                R.id.providerOptLogOut -> {
                    logoutDialog()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

//        binding.onlineSwitch.isChecked = UserUtils.getSpStatus(this)
        binding.onlineSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                updateOnlineStatus(1)
            } else {
                updateOnlineStatus(0)
            }
        }

        binding.providerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.providerSwitch.text = "User"
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
        }

        Log.e("FROM_FCM_SERVICE:", FROM_FCM_SERVICE.toString())
        if (FROM_FCM_SERVICE) {
            bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val categoryId = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            val bookingFactory = ViewModelFactory(BookingRepository())
            val bookingViewModel =
                ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]
            val requestBody = BookingDetailsReqModel(
                bookingId.toInt(),
                categoryId.toInt(),
                RetrofitBuilder.USER_KEY,
                userId.toInt()
            )
            Log.e("RequestBody:", Gson().toJson(requestBody))
            bookingViewModel.viewBookingDetails(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                        Log.e("Loading...:", "Loading...")
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        response = it.data!!
                        Log.e("Response:", Gson().toJson(response))
                        CoroutineScope(Dispatchers.Main).launch {
                            toast(this@ProviderDashboard, "$bookingId|Userid:$userId")
                            showBookingAlert(bookingViewModel, bookingId, userId, response)
                        }
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
//                    snackBar(binding.bottomNavigationView, it.message!!)
                        Log.e("Error:", Gson().toJson(response))
                    }
                }
            })
        } else {
            bookingId = ""
        }
    }

    private fun updateOnlineStatus(statusId: Int) {
        val requestBody = ProviderOnlineReqModel(
            RetrofitBuilder.PROVIDER_KEY,
            statusId,
            UserUtils.getUserId(this)
        )
        viewModel.onlineStatus(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    toast(this, it.data!!.toString())
                    progressDialog.dismiss()
                    if (statusId == 0) {
                        binding.onlineText.text = resources.getString(R.string.offline)
                        UserUtils.setOnline(this, false)
                    } else {
                        binding.onlineText.text = resources.getString(R.string.online)
                        UserUtils.setOnline(this, true)
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    binding.onlineSwitch.isChecked = !binding.onlineSwitch.isChecked
                    toast(this, it.message!!)
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun showBookingAlert(
        bookingViewModel: BookingViewModel,
        bookingId: String,
        userId: String,
        response: BookingDetailsResModel
    ) {

        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.provider_booking_alert_dialog, null)
        bottomSheetDialog!!.setCancelable(false)
        val acceptBtn = bottomSheet.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = bottomSheet.findViewById<TextView>(R.id.rejectBtn)
        val time = bottomSheet.findViewById<TextView>(R.id.time)
        val jobDescription = bottomSheet.findViewById<TextView>(R.id.jobDescription)
        val jobLocation = bottomSheet.findViewById<TextView>(R.id.jobLocation)
        val timeFrom = bottomSheet.findViewById<TextView>(R.id.timeFrom)
        val date = bottomSheet.findViewById<TextView>(R.id.date)
        val closeBtn = bottomSheet.findViewById<MaterialCardView>(R.id.closeBtn)
        val progressBar = bottomSheet.findViewById<CircularProgressIndicator>(R.id.progressBar)

        timeFrom.text = response.booking_details.from
        date.text = response.booking_details.scheduled_date
        if (response.job_details.isNotEmpty()) {
            jobDescription.text = response.job_details[0].job_description
            jobLocation.text =
                response.job_details[0].city + ", " + response.job_details[0].state + ", " + response.job_details[0].country + ", " + response.job_details[0].zipcode
        }

        closeBtn.setOnClickListener {
            bottomSheetDialog!!.dismiss()
            FROM_FCM_SERVICE = false
//            IN_PROVIDER_DASHBOARD = false
        }

        Log.e("ResponseDialog:", Gson().toJson(response))

        acceptBtn.setOnClickListener {
            val requestBody = ProviderResponseReqModel(
                this.response.booking_details.amount,
                bookingId.toInt(),
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                "",
                RetrofitBuilder.USER_KEY,
                UserUtils.getUserId(this).toInt(),
                5,
                userId.toInt()
            )
            bookingViewModel.setProviderResponse(this@ProviderDashboard, requestBody)
                .observe(this@ProviderDashboard, {
                    when (it) {
                        is NetworkResponse.Loading -> {
                            progressDialog.show()
                        }
                        is NetworkResponse.Success -> {
                            progressDialog.dismiss()
                            UserUtils.sendFCM(
                                this@ProviderDashboard,
                                this.response.booking_details.fcm_token,
                                "accept",
                                "accept|" + this.response.booking_details.amount + "|${this.response.booking_details.sp_id}|provider"
                            )
                            FROM_FCM_SERVICE = false
                            Companion.bookingId = ""
                            bottomSheetDialog!!.dismiss()
                            snackBar(binding.bottomNavigationView, "Booking Accepted Successfully")
                        }
                        is NetworkResponse.Failure -> {
                            progressDialog.dismiss()
                            toast(this, it.message!!)
                        }
                    }
                })
        }

        rejectBtn.setOnClickListener {
            ProviderRejectBookingScreen.userId = userId
            ProviderRejectBookingScreen.bookingId = bookingId
            ProviderRejectBookingScreen.response = response
            startActivity(Intent(this, ProviderRejectBookingScreen::class.java))
        }

        val dashboardInstant = Instant.now()
        val diff: Duration = Duration.between(FCMService.fcmInstant, dashboardInstant)
        val mins = diff.toMinutes()
        val secs = diff.seconds
        seconds = (59 - secs).toInt()
        minutes = (2 - mins).toInt()

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                time.text = "$minutes:$seconds"
                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    Companion.bookingId = ""
                    FROM_FCM_SERVICE = false
                    bottomSheetDialog!!.dismiss()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }
                Log.e("DASHBOARD THREAD: ", "ONGOING....")
                mainHandler.postDelayed(this, 1000)
            }
        })

        bottomSheetDialog!!.setContentView(bottomSheet)
        bottomSheetDialog!!.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateHeaderDetails() {
        loadProfileImage(profileImage)
        referralId.text = resources.getString(R.string.referralId) + UserUtils.getReferralId(this)
        toolBarTitle.text = resources.getString(R.string.welcome) + UserUtils.getUserName(this)
        profileImage.setOnClickListener {
            startActivity(Intent(this, ProviderProfileScreen::class.java))
        }
        binding.image.setOnClickListener {
            startActivity(Intent(this, ProviderProfileScreen::class.java))
        }
        toolBarBackBtn.setOnClickListener { onBackPressed() }
        toolBarBackTVBtn.setOnClickListener { onBackPressed() }
    }

    private fun showActivationDialog(context: Context, activationCode: String) {
        val dialog = BottomSheetDialog(context)
        val dialogView = layoutInflater.inflate(R.layout.service_provider_activation_dialog, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserLoginTypeScreen::class.java))
        }
        yesBtn.setOnClickListener {
            dialog.dismiss()
            when (activationCode) {
                "0" -> {
                    startActivity(Intent(this, ProviderSignUpOne::class.java))
                }
                "1" -> {
                    startActivity(Intent(this, ProviderSignUpFive::class.java))
                }
                "2" -> {
                    startActivity(Intent(this, ProviderSignUpSix::class.java))
                }
                "3" -> {
                    startActivity(Intent(this, ProviderSignUpSeven::class.java))
                }
                "4" -> {
                    FROM_FCM_SERVICE = false
                    startActivity(Intent(this, ProviderDashboard::class.java))
                }
            }
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserLoginTypeScreen::class.java))
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(fragment.tag)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                backStack.pop()
                if (!backStack.isEmpty()) {
                    loadFragment(getFragment(backStack.peek()))
                } else {
                    finish()
                    moveTaskToBack(true)
                }
            }
        }
    }

    private fun logoutDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Are you sure to logout?")
        dialog.setCancelable(false)
        dialog.setPositiveButton("YES") { dialogInterface, _ ->

            val factory = ViewModelFactory(LoginRepository())
            val viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
            val requestBody =
                LogoutReqModel(UserUtils.getUserId(this).toInt(), RetrofitBuilder.USER_KEY)
            viewModel.userLogout(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        dialogInterface.dismiss()
                        UserUtils.setUserLoggedInVia(this, "", "")
                        UserUtils.deleteUserCredentials(this)
                        startActivity(Intent(this, LoginScreen::class.java))
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        dialogInterface.dismiss()
                        snackBar(
                            UserDashboardScreen.binding.navigationView,
                            "Something went wrong. Please try again"
                        )
                    }
                }
            })
        }
        dialog.setNegativeButton("NO") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.show()
    }

    private fun getFragment(itemId: Int): Fragment {
        when (itemId) {
            R.id.navigation_home -> {
                binding.toolBarLayout.visibility = View.VISIBLE
                binding.bottomNavigationView.menu.getItem(0).isChecked = true
                return ProviderHomeScreen()
            }
            R.id.navigation_chats -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(1).isChecked = true
                return UserChatScreen()
            }
            R.id.navigation_alerts -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(3).isChecked = true
                return ProviderAlertsScreen()
            }
            R.id.navigation_offers -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(4).isChecked = true
                return ProviderOffersScreen()
            }
        }
        binding.bottomNavigationView.menu.getItem(0).isChecked = true
        return ProviderHomeScreen()
    }

    override fun onResume() {
        super.onResume()
        PermissionUtils.checkAndRequestPermissions(this)
        loadUserProfileData()
    }

    private fun loadUserProfileData() {
        viewModel.userProfile(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    val response = it.data!!
                    when (response.sp_activated) {
                        "1" -> {
                            // Service Provider Not Activated
                            showActivationDialog(this, response.activation_code)
                        }
                        "2" -> {
                            // Service Provider Approval Waiting
                            alertDialog("Approval Waiting")
                        }
                        "3" -> {
                            // Service Provider Activated
                            if (PermissionUtils.checkGPSStatus(this) && networkAvailable(this)) {
                                fetchLocation(this)
                            }
                        }
                        "4" -> {
                            // Service Provider Not Approved
                            alertDialog("Not Approved")
                        }
                        "5" -> {
                            // Service Provider Banned
                            alertDialog("Your Service Provider Account has been Banned!")
                        }
                        else -> {
                            updateSpProfile()
                        }
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, "Error : ${it.data.toString()}")
                    startActivity(Intent(this, UserLoginTypeScreen::class.java))
                }
            }
        })
    }

    private fun updateSpProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = BrowseCategoryReqModel(
                    UserUtils.getUserId(this@ProviderDashboard),
                    RetrofitBuilder.USER_KEY
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    if (responseData.profile_pic != null) {
                        val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
                        UserUtils.saveUserProfilePic(this@ProviderDashboard, imageUrl)
                        loadProfileImage(binding.image)
                    } else {
                        UserUtils.saveUserProfilePic(this@ProviderDashboard, "")
                    }
                    UserUtils.saveUserName(
                        this@ProviderDashboard,
                        responseData.fname + " " + responseData.lname
                    )
                    if (responseData.referral_id != null) {
                        UserUtils.saveReferralId(this@ProviderDashboard, responseData.referral_id)
                    }

                    updateHeaderDetails()
                } else {
                    Snackbar.make(
                        binding.navigationView,
                        "Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                Snackbar.make(
                    binding.navigationView,
                    "Server Busy",
                    Snackbar.LENGTH_SHORT
                ).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(
                    binding.navigationView,
                    "Something Went Wrong",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(
                    binding.navigationView,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun alertDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Alert")
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setPositiveButton("OK") { dialogInterface, _ ->
            startActivity(Intent(this, UserLoginTypeScreen::class.java))
            dialogInterface.dismiss()
        }
        dialog.show()
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
                    return
                }
            }
            fetchLocation(this)
        } else {
            toast(this, "No permission")
        }
    }

    private fun fetchLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    fetchLocationDetails(context, latitude, longitude)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()!!
        )
    }

    @SuppressLint("SetTextI18n")
    private fun fetchLocationDetails(context: Context, latitude: Double, longitude: Double) {
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val address: List<Address> = geoCoder.getFromLocation(latitude, longitude, 1)
            val addressName: String = address.get(0).getAddressLine(0)
            val city: String = address.get(0).locality
            val state: String = address.get(0).adminArea
            val country: String = address.get(0).countryName
            val postalCode: String = address.get(0).postalCode
            val knownName: String = address.get(0).featureName
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            UserUtils.setLatitude(this, latitude.toString())
            UserUtils.setLongitude(this, longitude.toString())
            UserUtils.setCity(this, city)
            UserUtils.setState(this, state)
            UserUtils.setCountry(this, country)
            UserUtils.setPostalCode(this, postalCode)
            UserUtils.setAddress(this, knownName)
            binding.userLocation.text = UserUtils.getCity(this)

            val requestBody = ProviderLocationReqModel(
                UserUtils.getAddress(this),
                UserUtils.getCity(this),
                UserUtils.getCountry(this),
                RetrofitBuilder.PROVIDER_KEY,
                1,
                UserUtils.getPostalCode(this),
                UserUtils.getState(this),
                UserUtils.getLatitude(this),
                UserUtils.getLongitude(this),
                UserUtils.getUserId(this).toInt()
            )
            viewModel.saveLocation(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {

                    }
                    is NetworkResponse.Success -> {
//                        toast(this, it.data!!)
                        if (!UserUtils.getSpStatus(this)) {
                            updateOnlineStatus(1)
                        }
                        if (UserUtils.getSpStatus(this)) {
                            binding.onlineText.text = resources.getString(R.string.online)
                            binding.onlineSwitch.isChecked = true
                        } else {
                            binding.onlineText.text = resources.getString(R.string.offline)
                            binding.onlineSwitch.isChecked = false
                        }
                    }
                    is NetworkResponse.Failure -> {
                        if (PermissionUtils.checkGPSStatus(this) && networkAvailable(this)) {
                            fetchLocation(this)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
                .show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

}