package com.satrango.ui.user.user_dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityUserDashboardScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.fcm.FCMService
import com.satrango.ui.auth.login_screen.LoginRepository
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.login_screen.LoginViewModel
import com.satrango.ui.auth.login_screen.LogoutReqModel
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.ChangeExtraDemandStatusReqModel
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsReqModel
import com.satrango.ui.user.bookings.view_booking_details.models.BookingDetailsResModel
import com.satrango.ui.user.user_dashboard.chats.UserChatScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.MyJobPostsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.models.UserProfileReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobTypeScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.refer_earn.UserReferAndEarn
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationSelectionScreen
import com.satrango.ui.user.user_dashboard.user_offers.UserOffersScreen
import com.satrango.utils.*
import com.satrango.utils.UserUtils.isProvider
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.*

class   UserDashboardScreen : AppCompatActivity() {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var toolBarBackBtn: ImageView
    private lateinit var userProviderSwitch: SwitchMaterial
    private lateinit var profileImage: CircleImageView

    private var flag: Boolean = true
    private lateinit var backStack: Deque<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(myReceiver, IntentFilter(FCMService.EXTRA_DEMAND_ACCEPT_REJECT));

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
//            Log.e("Error" + Thread.currentThread().stackTrace[2], paramThrowable.localizedMessage!!)
        }

        initializeProgressDialog()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        userProviderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                finish()
                UserUtils.saveFromFCMService(this, false)
//                ProviderDashboard.FROM_FCM_SERVICE = false
                startActivity(Intent(this, ProviderDashboard::class.java))
            }
        }

        backStack = ArrayDeque(4)
        backStack.push(R.id.navigation_home)
        loadFragment(UserHomeScreen())
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

        binding.userLocation.setOnClickListener {
            UserLocationSelectionScreen.FROM_USER_DASHBOARD = true
            startActivity(Intent(this, UserLocationSelectionScreen::class.java))
        }

        if (UserUtils.getCity(this).isNotEmpty()) {
            binding.userLocation.text = UserUtils.getCity(this)
            SearchServiceProvidersScreen.userLocationText = binding.userLocation.text.toString().trim()
        } else {
            fetchLocation(this, binding.userLocation)
        }


        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.userOptHome -> {
                    loadFragment(UserHomeScreen())
                }
                R.id.userOptPostJob -> {
                    startActivity(Intent(this, PostJobTypeScreen::class.java))
                }
                R.id.userOptBrowseCategories -> {
                    startActivity(Intent(this, BrowseCategoriesScreen::class.java))
                }
                R.id.userOptMyAccount -> {
                    startActivity(Intent(this, UserMyAccountScreen::class.java))
                }
                R.id.userOptMyBooking -> {
                    startActivity(Intent(this, UserMyBookingsScreen::class.java))
                }
                R.id.userOptMyJobPosts -> {
                    startActivity(Intent(this, MyJobPostsScreen::class.java))
                }
                R.id.userOptMyProfile -> {
                    startActivity(Intent(this, UserProfileScreen::class.java))
                }
                R.id.userOptReferEarn -> {
                    startActivity(Intent(this, UserReferAndEarn::class.java))
                }
                R.id.userOptSettings -> {
                    isProvider(this, false)
                    startActivity(Intent(this, UserSettingsScreen::class.java))
                }
                R.id.userOptLogOut -> {
                    logoutDialog()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateHeaderDetails() {
        loadProfileImage(profileImage)
        referralId.text = resources.getString(R.string.referralId) + UserUtils.getReferralId(this)
        toolBarTitle.text = resources.getString(R.string.welcome) + UserUtils.getUserName(this)
        profileImage.setOnClickListener {
            startActivity(Intent(this, UserProfileScreen::class.java))
        }
        binding.image.setOnClickListener {
            startActivity(Intent(this, UserProfileScreen::class.java))
        }
        toolBarBackBtn.setOnClickListener { onBackPressed() }
        toolBarBackTVBtn.setOnClickListener { onBackPressed() }
    }

    private fun logoutDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Are you sure to logout?")
        dialog.setCancelable(false)
        dialog.setPositiveButton("YES") { dialogInterface, _ ->
            dialogInterface.dismiss()
            val factory = ViewModelFactory(LoginRepository())
            val viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
            val requestBody = LogoutReqModel(UserUtils.getUserId(this).toInt(), RetrofitBuilder.USER_KEY)
            viewModel.userLogout(this, requestBody).observe(this) {
                when (it) {
                    is NetworkResponse.Loading -> {
                        progressDialog.show()
                    }
                    is NetworkResponse.Success -> {
                        progressDialog.dismiss()
                        UserUtils.setUserLoggedInVia(this, "", "")
                        UserUtils.saveUserProfilePic(this@UserDashboardScreen, "")
                        UserUtils.deleteUserCredentials(this)
                        val databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url)).child(getString(R.string.users))
                        databaseReference.child(UserUtils.getUserId(this)).child(getString(R.string.online_status)).setValue(getString(R.string.offline))
                        startActivity(Intent(this, LoginScreen::class.java))
                    }
                    is NetworkResponse.Failure -> {
                        progressDialog.dismiss()
                        dialogInterface.dismiss()
                        snackBar(binding.navigationView, "Something went wrong. Please try again")
                    }
                }
            }

        }
        dialog.setNegativeButton("NO") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.show()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
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

    override fun onResume() {
        super.onResume()
        isProvider(this, false)
//        PermissionUtils.checkAndRequestPermissions(this)
        getUserProfilePicture()
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
//            fetchLocation(this)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityUserDashboardScreenBinding

        // Fused Location Objects
        private lateinit var locationCallBack: LocationCallback

        @SuppressLint("StaticFieldLeak")
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        fun fetchLocation(context: Context, userLocation: TextView) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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
                        fetchLocationDetails(context, latitude, longitude, userLocation)
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
        private fun fetchLocationDetails(
            context: Context,
            latitude: Double,
            longitude: Double,
            userLocation: TextView
        ) {
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
                userLocation.text = UserUtils.getCity(context)
                SearchServiceProvidersScreen.userLocationText = binding.userLocation.text.toString().trim()
            } catch (e: Exception) {
                Toast.makeText(context, "Please Check you Internet Connection!", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getUserProfilePicture() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = UserProfileReqModel(
                    RetrofitBuilder.USER_KEY,
                    UserUtils.getUserId(this@UserDashboardScreen).toInt(),
                    UserUtils.getCity(this@UserDashboardScreen)
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
//                    updateProfilePicInFirebase(imageUrl, "${response.data.fname} ${response.data.lname}")
                    UserUtils.saveUserProfilePic(this@UserDashboardScreen, imageUrl)
                    loadProfileImage(binding.image)
                    UserUtils.saveGoogleMapsKey(this@UserDashboardScreen, response.data.maps_key)
                    UserUtils.saveFCMServerKey(this@UserDashboardScreen, response.data.fcm_key)
                    UserUtils.saveUserName(
                        this@UserDashboardScreen,
                        responseData.fname + " " + responseData.lname
                    )
                    if (responseData.referral_id != null) {
                        UserUtils.saveReferralId(this@UserDashboardScreen, responseData.referral_id)
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
                Snackbar.make(binding.navigationView, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(binding.navigationView, "Something Went Wrong", Snackbar.LENGTH_SHORT)
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

    private fun updateProfilePicInFirebase(imageUrl: String, userName: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_database_reference_url))
        databaseReference.child(getString(R.string.users)).child(UserUtils.getPhoneNo(this)).child(getString(R.string.profile_image)).setValue(imageUrl)
        databaseReference.child(getString(R.string.users)).child(UserUtils.getPhoneNo(this)).child(getString(R.string.user_name)).setValue(userName)
    }

    private fun getFragment(itemId: Int): Fragment {
        when (itemId) {
            R.id.navigation_home -> {
                binding.toolBarLayout.visibility = View.VISIBLE
                binding.bottomNavigationView.menu.getItem(0).isChecked = true
                return UserHomeScreen()
            }
            R.id.navigation_chats -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(1).isChecked = true
                return UserChatScreen()
            }
            R.id.navigation_alerts -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(3).isChecked = true
                return UserAlertScreen()
            }
            R.id.navigation_offers -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(4).isChecked = true
                return UserOffersScreen()
            }
        }
        binding.bottomNavigationView.menu.getItem(0).isChecked = true
        return UserHomeScreen()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(this, BeautifulProgressDialog.withGIF, resources.getString(R.string.loading))
        progressDialog.setGifLocation(Uri.parse("android.resource://$packageName/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val bookingId = intent.getStringExtra(getString(R.string.booking_id))!!
            val categoryId = intent.getStringExtra(getString(R.string.category_id))!!
            val userId = intent.getStringExtra(getString(R.string.user_id))!!
            openBookingDetails(bookingId, categoryId, userId)
        }
    }

    fun openBookingDetails(bookingId: String, categoryId: String, userId: String) {
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        val requestBody = BookingDetailsReqModel(bookingId.toInt(), categoryId.toInt(), RetrofitBuilder.USER_KEY, userId.split("|")[0].toInt())
//        Log.e("PROVIDER RESPONSE", Gson().toJson(requestBody))
        viewModel.viewBookingDetails(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    updateUI(it.data!!, bookingId, viewModel)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.navigationView, it.message!!)
                }
            }
        }
    }

    private fun updateUI(
        response: BookingDetailsResModel,
        bookingId: String,
        viewModel: BookingViewModel
    ) {
        if (!response.booking_details.extra_demand_status.isNullOrBlank()) {
            if (response.booking_details.extra_demand_total_amount.isNotEmpty()) {
                if (response.booking_details.extra_demand_status == "0") {
                    showExtraDemandAcceptDialog(
                        bookingId.toInt(),
                        response.booking_details.material_advance,
                        response.booking_details.technician_charges,
                        response.booking_details.extra_demand_total_amount,
                        progressDialog,
                        viewModel,
                        response
                    )
                }
            }
        }
    }

    private fun showExtraDemandAcceptDialog(
        bookingId: Int,
        materialAdvance: String,
        technicalCharges: String,
        extraDemandTotalAmount: String,
        progressDialog: BeautifulProgressDialog,
        viewModel: BookingViewModel,
        response: BookingDetailsResModel
    ) {
        val extraDemandAcceptRejectDialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.provider_extra_demand_accept_dialog, null)
        val materialCharges = dialogView.findViewById<TextView>(R.id.materialCharges)
        val technicianCharges = dialogView.findViewById<TextView>(R.id.technicianCharges)
        val totalCost = dialogView.findViewById<TextView>(R.id.totalCost)
        val acceptBtn = dialogView.findViewById<TextView>(R.id.acceptBtn)
        val rejectBtn = dialogView.findViewById<TextView>(R.id.rejectBtn)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)

        materialCharges.text = materialAdvance
        technicianCharges.text = technicalCharges
        totalCost.text = extraDemandTotalAmount

        closeBtn.setOnClickListener { extraDemandAcceptRejectDialog.dismiss() }

        acceptBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 1, extraDemandAcceptRejectDialog, progressDialog, viewModel, response)
        }

        rejectBtn.setOnClickListener {
            changeExtraDemandStatus(bookingId, 2, extraDemandAcceptRejectDialog, progressDialog, viewModel, response)
        }

        extraDemandAcceptRejectDialog.setCancelable(false)
        extraDemandAcceptRejectDialog.setContentView(dialogView)
        extraDemandAcceptRejectDialog.show()
    }

    private fun changeExtraDemandStatus(
        bookingId: Int,
        status: Int,
        dialog: BottomSheetDialog,
        progressDialog: BeautifulProgressDialog,
        viewModel: BookingViewModel,
        response: BookingDetailsResModel
    ) {
        val requestBody = ChangeExtraDemandStatusReqModel(bookingId, RetrofitBuilder.USER_KEY, status)
//        Log.e("STATUS:", Gson().toJson(requestBody))
        viewModel.changeExtraDemandStatus(this, requestBody).observe(this) {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
//                    Log.e("STATUS:", Gson().toJson(it.data!!))
                    progressDialog.dismiss()
                    dialog.dismiss()
                    if (status == 1) {
                        UserUtils.sendExtraDemandFCM(
                            this, response.booking_details.sp_fcm_token,
                            bookingId.toString(),
                            "0",
                            "${UserUtils.getUserId(this)}|1"
                        )
                        snackBar(binding.navigationView, "Extra Demand Accepted")
                    } else {
                        UserUtils.sendExtraDemandFCM(
                            this, response.booking_details.sp_fcm_token,
                            bookingId.toString(),
                            "0",
                            "${UserUtils.getUserId(this)}|2"
                        )
                        snackBar(binding.navigationView, "Extra Demand Rejected")
                    }
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    toast(this, it.message!!)
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver((myReceiver), IntentFilter(FCMService.EXTRA_DEMAND_ACCEPT_REJECT))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }
}
