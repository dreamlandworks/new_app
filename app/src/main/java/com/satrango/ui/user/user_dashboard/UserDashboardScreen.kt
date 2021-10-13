package com.satrango.ui.user.user_dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivityUserDashboardScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.service_provider.provider_dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.MyJobPostsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobTypeScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.refer_earn.UserReferAndEarn
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.user_location_change.UserLocationSelectionScreen
import com.satrango.ui.user.user_dashboard.user_offers.UserOffersScreen
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class UserDashboardScreen : AppCompatActivity() {

    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var toolBarBackBtn: ImageView
    private lateinit var userProviderSwitch: SwitchCompat
    private lateinit var profileImage: CircleImageView

    private var flag: Boolean = true
    private lateinit var backStack: Deque<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
            Log.e("Error" + Thread.currentThread().stackTrace[2], paramThrowable.localizedMessage)
        }

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
            startActivity(Intent(this, UserLocationSelectionScreen::class.java))
        }

        binding.userLocation.text = UserUtils.getCity(this)
        SearchServiceProvidersScreen.userLocationText = binding.userLocation.text.toString().trim()

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
                    UserSettingsScreen.FROM_PROVIDER = false
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
            UserUtils.setUserLoggedInVia(this, "", "")
            startActivity(Intent(this, LoginScreen::class.java))
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
        PermissionUtils.checkAndRequestPermissions(this)
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
                    PermissionUtils.checkAndRequestPermissions(this)
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

        fun fetchLocation(context: Context) {
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
                UserUtils.setLatitude(context, latitude.toString())
                UserUtils.setLongitude(context, longitude.toString())
                UserUtils.setCity(context, city)
                UserUtils.setState(context, state)
                UserUtils.setCountry(context, country)
                UserUtils.setPostalCode(context, postalCode)
                UserUtils.setAddress(context, knownName)
                binding.userLocation.text = UserUtils.getCity(context)
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
                val requestBody = BrowseCategoryReqModel(
                    UserUtils.getUserId(this@UserDashboardScreen),
                    RetrofitBuilder.USER_KEY
                )
                val response = RetrofitBuilder.getUserRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    if (responseData.profile_pic != null) {
                        val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
                        UserUtils.saveUserProfilePic(this@UserDashboardScreen, imageUrl)
                        loadProfileImage(binding.image)
                    } else {
                        UserUtils.saveUserProfilePic(this@UserDashboardScreen, "")
                    }
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

}
