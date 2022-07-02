package com.satrango.ui.service_provider.provider_dashboard.dashboard

import android.Manifest
import android.R.attr.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.load.HttpException
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.JsonSyntaxException
import com.satrango.GpsLocationReceiver
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
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.chats.UserChatScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.utils.*
import com.satrango.R
import com.satrango.base.MyApp
import com.satrango.utils.UserUtils.isProvider
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.RoundingMode
import java.net.SocketTimeoutException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.round


class ProviderDashboard : AppCompatActivity() {

    private lateinit var gpsReceiver: GpsLocationReceiver
    private var bookingType = ""
    private var categoryId = ""
    private var userId = ""
    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackBtn: ImageView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var userProviderSwitch: SwitchCompat
    private lateinit var response: BookingDetailsResModel

    private var flag: Boolean = true
    private lateinit var backStack: Deque<Int>

    companion object {
        private lateinit var locationCallBack: LocationCallback
        private lateinit var viewModel: ProviderDashboardViewModel
        private lateinit var progressDialog: BeautifulProgressDialog
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        private lateinit var binding: ActivityProviderDashboardBinding

        var FROM_FCM_SERVICE = false
        var minutes = 2
        var seconds = 59
        var bookingId = "0"
        var bottomSheetDialog: BottomSheetDialog? = null


        fun fetchLocation(context: Context) {
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

        private fun updateOnlineStatus(context: Context, statusId: Int) {
            val requestBody = ProviderOnlineReqModel(
                RetrofitBuilder.PROVIDER_KEY,
                statusId,
                UserUtils.getUserId(context)
            )
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitBuilder.getServiceProviderRetrofitInstance().updateSpOnlineStatus(requestBody)
                    val jsonResponse = JSONObject(response.string())
                    if (jsonResponse.getInt("status") == 200) {
                        progressDialog.dismiss()
                        if (statusId == 0) {
                            binding.onlineText.text = context.resources.getString(R.string.offline)
                            UserUtils.setOnline(context, false)
                        } else {
                            binding.onlineText.text = context.resources.getString(R.string.online)
                            UserUtils.setOnline(context, true)
                        }
                    } else {
                        progressDialog.dismiss()
                        binding.onlineSwitch.isChecked = !binding.onlineSwitch.isChecked
                    }
                } catch (e: java.lang.Exception) {
                    progressDialog.dismiss()
                    Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
                }
            }
//            viewModel.onlineStatus(context, requestBody).observe(this) {
//                when (it) {
//                    is NetworkResponse.Loading -> {
////                    progressDialog.show()
//                    }
//                    is NetworkResponse.Success -> {
////                    toast(this, it.data!!.toString())
//                        progressDialog.dismiss()
//                        if (statusId == 0) {
//                            binding.onlineText.text = context.resources.getString(R.string.offline)
//                            UserUtils.setOnline(context, false)
//                        } else {
//                            binding.onlineText.text = context.resources.getString(R.string.online)
//                            UserUtils.setOnline(context, true)
//                        }
//                    }
//                    is NetworkResponse.Failure -> {
//                        progressDialog.dismiss()
//                        binding.onlineSwitch.isChecked = !binding.onlineSwitch.isChecked
//                        Toast.makeText(context, it.message!!, Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
        }

        fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }

        @SuppressLint("SetTextI18n")
        private fun fetchLocationDetails(context: Context, latitude: Double, longitude: Double) {
            if (UserUtils.getLatitude(context).isNotEmpty() && UserUtils.getLongitude(context).isNotEmpty()) {
                if (roundOffDecimal(latitude) == roundOffDecimal(UserUtils.getLatitude(context).toDouble()) && roundOffDecimal(longitude) == roundOffDecimal(UserUtils.getLongitude(context).toDouble())) {
                    binding.userLocation.text = UserUtils.getCity(context)
                    updateSpOnlineStatus(context)
                    return
                }
            }
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
                UserUtils.setLatitude(context, latitude.toString())
                UserUtils.setLongitude(context, longitude.toString())
                UserUtils.setCity(context, city)
                UserUtils.setState(context, state)
                UserUtils.setCountry(context, country)
                UserUtils.setPostalCode(context, postalCode)
                UserUtils.setAddress(context, knownName)
                binding.userLocation.text = UserUtils.getCity(context)

                val requestBody = ProviderLocationReqModel(
                    UserUtils.getAddress(context),
                    UserUtils.getCity(context),
                    UserUtils.getCountry(context),
                    RetrofitBuilder.PROVIDER_KEY,
                    1,
                    UserUtils.getPostalCode(context),
                    UserUtils.getState(context),
                    UserUtils.getLatitude(context),
                    UserUtils.getLongitude(context),
                    UserUtils.getUserId(context).toInt()
                )
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = RetrofitBuilder.getServiceProviderRetrofitInstance().saveProviderLocation(requestBody)
                        val jsonResponse = JSONObject(response.string())
                        if (jsonResponse.getInt("status") == 200) {
                            updateSpOnlineStatus(context)
                        }
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(context, e.message!!, Toast.LENGTH_SHORT).show()
                    }
                }

//                viewModel.saveLocation(context, requestBody).observe(context) {
//                    when (it) {
//                        is NetworkResponse.Loading -> {
//
//                        }
//                        is NetworkResponse.Success -> {
////                        toast(this, it.data!!)
//                            if (!UserUtils.getSpStatus(context)) {
//                                updateOnlineStatus(1)
//                            }
//                            if (UserUtils.getSpStatus(context)) {
//                                binding.onlineText.text = context.resources.getString(R.string.online)
//                                binding.onlineSwitch.isChecked = true
//                            } else {
//                                binding.onlineText.text = resources.getString(R.string.offline)
//                                binding.onlineSwitch.isChecked = false
//                            }
//                        }
//                        is NetworkResponse.Failure -> {
//                            if (PermissionUtils.checkGPSStatus(this) && networkAvailable(this)) {
//                                fetchLocation(this)
//                            }
//                        }
//                    }
//                }
            } catch (e: Exception) {
//                Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
//                    .show()
            }
        }

        private fun updateSpOnlineStatus(context: Context) {
            if (!UserUtils.getSpStatus(context)) {
                updateOnlineStatus(context, 1)
            }
            if (UserUtils.getSpStatus(context)) {
                binding.onlineText.text = context.resources.getString(R.string.online)
                binding.onlineSwitch.isChecked = true
            } else {
                binding.onlineText.text = context.resources.getString(R.string.offline)
                binding.onlineSwitch.isChecked = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gpsReceiver = GpsLocationReceiver()
        registerReceiver(myReceiver, IntentFilter(FCMService.INTENT_FILTER))

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
//            Log.e("Error" + Thread.currentThread().stackTrace[2], paramThrowable.localizedMessage!!)
        }

//        binding.fab.setOnClickListener {
//            startActivity(Intent(this, ProviderMyBidsScreen::class.java))
//        }

        val factory = ViewModelFactory(ProviderDashboardRepository())
        viewModel = ViewModelProvider(this, factory)[ProviderDashboardViewModel::class.java]

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolBar, R.string.app_name, R.string.app_name)
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

        if (!UserUtils.updateNewFCMToken(this)) {
            snackBar(binding.bottomNavigationView, "Please check internet connection!")
            Handler().postDelayed({
                finish()
                startActivity(Intent(this, UserLoginTypeScreen::class.java))
            }, 3000)
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
                    isProvider(this, true)
                    startActivity(Intent(this, UserSettingsScreen::class.java))
                }
                R.id.providerOptLogOut -> {
                    logoutDialog()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.onlineSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                updateOnlineStatus(this, 1)
            } else {
                updateOnlineStatus(this, 0)
            }
        }

        binding.providerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.providerSwitch.text = "User"
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
        }

//        Log.e("FROM_FCM_SERVICE:", UserUtils.getFromFCMService(this).toString())
        if (UserUtils.getFromFCMService(this)) {
            if (bookingId != null) {
                bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
                categoryId = intent.getStringExtra(getString(R.string.category_id))!!
                userId = intent.getStringExtra(getString(R.string.user_id))!!
                try {
                    toast(this@ProviderDashboard, bookingId)
                    getInstantBookingDetails()
                } catch (e: NumberFormatException) { }
            }
        } else {
            bookingId = "0"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInstantBookingDetails() {
        val bookingFactory = ViewModelFactory(BookingRepository())
        val bookingViewModel = ViewModelProvider(this, bookingFactory)[BookingViewModel::class.java]
        val requestBody = BookingDetailsReqModel(
            bookingId.toInt(),
            categoryId.toInt(),
            RetrofitBuilder.USER_KEY,
            userId.toInt()
        )
//        Log.e("RequestBody:", Gson().toJson(requestBody))
//        toast(this, Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            progressDialog.show()
            try {
                val apiResponse = RetrofitBuilder.getUserRetrofitInstance().getUserBookingDetails(requestBody)
                if (apiResponse.status == 200) {
                    progressDialog.dismiss()
                    response = apiResponse
//                Log.e("Response:", Gson().toJson(response))
                    showBookingAlert(bookingId, userId, response, categoryId)
                } else {
                    progressDialog.dismiss()
                    snackBar(binding.bottomNavigationView, response.message)
                }
            } catch (e: java.lang.Exception) {
                snackBar(binding.bottomNavigationView, e.message!!)
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(gpsReceiver)
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun showBookingAlert(
        bookingId: String,
        userId: String,
        response: BookingDetailsResModel,
        categoryId: String
    ) {
        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheet = LayoutInflater.from(this).inflate(R.layout.provider_booking_alert_dialog, null)
        bottomSheetDialog!!.setCancelable(false)
        val acceptBtn = bottomSheet.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = bottomSheet.findViewById<TextView>(R.id.rejectBtn)
        val time = bottomSheet.findViewById<TextView>(R.id.time)
        val jobDescription = bottomSheet.findViewById<TextView>(R.id.jobDescription)
        val jobLocation = bottomSheet.findViewById<TextView>(R.id.jobLocation)
        val jobLocationText = bottomSheet.findViewById<TextView>(R.id.locationText)
        val timeFrom = bottomSheet.findViewById<TextView>(R.id.timeFrom)
        val date = bottomSheet.findViewById<TextView>(R.id.date)
        val closeBtn = bottomSheet.findViewById<MaterialCardView>(R.id.closeBtn)
        val progressBar = bottomSheet.findViewById<CircularProgressIndicator>(R.id.progressBar)

        timeFrom.text = response.booking_details.from
        date.text = "${response.booking_details.scheduled_date.split("-")[2]}-${response.booking_details.scheduled_date.split("-")[1]}-${response.booking_details.scheduled_date.split("-")[0]}"
        if (response.job_details.isNotEmpty()) {
            jobDescription.text = response.job_details[0].job_description
            if (categoryId != "2") {
                if (response.job_details[0].locality.isNullOrBlank()) {
                    jobLocation.text = response.job_details[0].city + ", " + response.job_details[0].state + ", " + response.job_details[0].country + ", " + response.job_details[0].zipcode
                } else {
                    jobLocation.text = response.job_details[0].locality + ", " + response.job_details[0].city + ", " + response.job_details[0].state + ", " + response.job_details[0].country + ", " + response.job_details[0].zipcode
                }
            } else {
                jobLocation.visibility = View.GONE
                jobLocationText.visibility = View.GONE
            }
        }

        closeBtn.setOnClickListener {
            UserUtils.saveFromFCMService(this, false)
            toast(this, "Close Btn")
            bottomSheetDialog!!.dismiss()
            if (FCMService.notificationManager != null) {
                FCMService.notificationManager.cancelAll()
            }
        }

//        Log.e("ResponseDialog:", Gson().toJson(response))

        acceptBtn.setOnClickListener {
            toast(this, "Accept Closed")
            bottomSheetDialog!!.dismiss()
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
//            toast(this, Gson().toJson(requestBody))
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bookingResponse = RetrofitBuilder.getUserRetrofitInstance().setProviderResponse(requestBody)
                    val jsonResponse = JSONObject(bookingResponse.string())
                    progressDialog.show()
                    if (jsonResponse.getInt("status") == 200) {
                        progressDialog.dismiss()
                        Companion.bookingId = "0"
//                        Log.e("RESPONSE TOKEN", )
                        UserUtils.sendFCM(this@ProviderDashboard, response.booking_details.fcm_token, "accept", "accept", "accept|${response.booking_details.amount}|${UserUtils.getUserId(this@ProviderDashboard)}|$bookingType")
                        UserUtils.saveFromFCMService(this@ProviderDashboard, false)
                        snackBar(binding.bottomNavigationView, "Booking Accepted Successfully")
                        if (FCMService.notificationManager != null) {
                            FCMService.notificationManager.cancelAll()
                        }
                        finish()
                        startActivity(intent)
                    } else {
                        progressDialog.dismiss()
                        toast(this@ProviderDashboard, jsonResponse.getString("message"))
                    }
                } catch (e: java.lang.Exception) {
                    toast(this@ProviderDashboard, e.message!!)
                }
            }
        }

        rejectBtn.setOnClickListener {
            ProviderRejectBookingScreen.userId = userId
            ProviderRejectBookingScreen.bookingId = bookingId
            ProviderRejectBookingScreen.response = response
            ProviderRejectBookingScreen.bookingType = bookingType
            startActivity(Intent(this, ProviderRejectBookingScreen::class.java))
        }

        val dashboardInstant = Instant.now()
        val diff: Duration = Duration.between(FCMService.fcmInstant, dashboardInstant)
        val mins = diff.toMinutes()
        val secs = diff.seconds
        var seconds = (59 - secs).toInt()
        var minutes = (2 - mins).toInt()
//        var minutes = 2
//        var seconds = 59
        val mainHandler = Handler(Looper.getMainLooper())
        var progressTime = 180
        mainHandler.post(object : Runnable {Git
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                if (seconds < 10) {
                    time.text = "0$minutes:0$seconds"
                } else {
                    time.text = "0$minutes:$seconds"
                }

                progressTime -= 1
                progressBar.progress = progressTime

                seconds -= 1
                if (minutes == 0 && seconds == 0) {
                    toast(this@ProviderDashboard, "Closed")
                    bottomSheetDialog!!.dismiss()
                }
                if (seconds == 0) {
                    seconds = 59
                    minutes -= 1
                }

                mainHandler.postDelayed(this, 1000)
            }
        })
        bottomSheetDialog!!.setContentView(bottomSheet)
        if (!(this as Activity).isFinishing) {
            bottomSheetDialog!!.show()
        }
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
        val message = dialogView.findViewById<TextView>(R.id.text)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        message.text = resources.getString(R.string.activation_note)
        noBtn.visibility = View.VISIBLE

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
                    startActivity(Intent(this, ProviderSignUpOne::class.java))
//                    startActivity(Intent(this, ProviderSignUpSix::class.java))
                }
                "3" -> {
                    startActivity(Intent(this, ProviderSignUpSeven::class.java))
                }
                "4" -> {
                    UserUtils.saveFromFCMService(this, false)
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

    private fun showApprovalWaitingDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        val dialogView = layoutInflater.inflate(R.layout.service_provider_activation_dialog, null)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val message = dialogView.findViewById<TextView>(R.id.text)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        val params = noBtn.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        message.text = resources.getString(R.string.activation_awaiting_message)
        yesBtn.layoutParams = params
        noBtn.visibility = View.GONE
        yesBtn.text = resources.getString(R.string.ok)
        closeBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserLoginTypeScreen::class.java))
        }
        yesBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UserLoginTypeScreen::class.java))
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
            viewModel.userLogout(this, requestBody).observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        dialogInterface.dismiss()
                        val databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url)).child(getString(R.string.users)).child(UserUtils.getUserId(this))
                        databaseReference.child(getString(R.string.online_status)).setValue(getString(R.string.offline))
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
            }
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
        isProvider(this, true)
//        PermissionUtils.checkAndRequestPermissions(this)
        loadUserProfileData()
//        loadUserProfileData()
        updateSpProfile()
        registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private fun loadUserProfileData() {
        val requestBody = UserProfileReqModel(
            RetrofitBuilder.USER_KEY,
            UserUtils.getUserId(this@ProviderDashboard).toInt(),
            UserUtils.getCity(this@ProviderDashboard)
        )
        viewModel.userProfile(this, requestBody).observe(this) {
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
//                            alertDialog("Approval Waiting")
                            showApprovalWaitingDialog(this)
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
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, "Error : ${it.data.toString()}")

                    startActivity(Intent(this, UserLoginTypeScreen::class.java))
                }
            }
        }
    }

    private fun updateSpProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = UserProfileReqModel(
                    RetrofitBuilder.USER_KEY,
                    UserUtils.getUserId(this@ProviderDashboard).toInt(),
                    UserUtils.getCity(this@ProviderDashboard)
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    val imageUrl = responseData.profile_pic
//                    updateProfilePicInFirebase(imageUrl, "${response.data.fname} ${response.data.lname}")
                    UserUtils.saveGoogleMapsKey(this@ProviderDashboard, response.data.maps_key)
                    UserUtils.saveFCMServerKey(this@ProviderDashboard, response.data.fcm_key)
                    UserUtils.saveUserProfilePic(this@ProviderDashboard, imageUrl)
                    loadProfileImage(binding.image)
                    UserUtils.saveUserName(this@ProviderDashboard, responseData.fname + " " + responseData.lname)
                    if (responseData.referral_id != null) {
                        UserUtils.saveReferralId(this@ProviderDashboard, responseData.referral_id)
                    }
                    updateHeaderDetails()
                } else {
                    Snackbar.make(binding.navigationView, "Something went wrong!", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Snackbar.make(binding.navigationView, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(binding.navigationView, "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(binding.navigationView, "Please check internet Connection", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfilePicInFirebase(imageUrl: String, userName: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
        databaseReference.child(getString(R.string.users)).child(UserUtils.getPhoneNo(this)).child(getString(R.string.profile_image)).setValue(imageUrl)
        databaseReference.child(getString(R.string.users)).child(UserUtils.getPhoneNo(this)).child(getString(R.string.user_name)).setValue(userName)
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
//                    PermissionUtils.checkAndRequestPermissions(this)
                    return
                }
            }
            fetchLocation(this)
        } else {
            toast(this, "No permission")
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((myReceiver), IntentFilter(FCMService.INTENT_FILTER))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.purple_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
                bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
                categoryId = intent.getStringExtra(getString(R.string.category_id))!!
                userId = intent.getStringExtra(getString(R.string.user_id))!!
                bookingType = intent.getStringExtra(getString(R.string.booking_type))!!
                toast(this@ProviderDashboard, bookingId)
                try {
                    getInstantBookingDetails()
                } catch (e: NumberFormatException) {}

        }
    }

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

}