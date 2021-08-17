package com.satrango.ui.service_provider.provider_dashboard.dashboard

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
import android.view.LayoutInflater
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityProviderDashboardBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOne
import com.satrango.ui.service_provider.provider_dashboard.provider_home.ProviderHomeScreen
import com.satrango.ui.user.user_dashboard.UserChatScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.UserOffersScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertScreen
import com.satrango.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class ProviderDashboard : AppCompatActivity() {

    private lateinit var viewModel: ProviderDashboardViewModel
    private lateinit var binding: ActivityProviderDashboardBinding
    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var toolBarBackBtn: ImageView
    private lateinit var userProviderSwitch: SwitchCompat
    private lateinit var profileImage: CircleImageView

    private var flag: Boolean = true
    private lateinit var backStack: Deque<Int>

    private lateinit var locationCallBack: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                }
                R.id.providerOptMyBooking -> {

                }
                R.id.providerOptMyBids -> {

                }
                R.id.providerOptMyProfile -> {

                }
                R.id.providerOptSettings -> {

                }
                R.id.providerOptLogOut -> {
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

    private fun showActivationDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.service_provider_activation_dialog, null)
        dialog.setCancelable(false)
        val closeBtn = dialogView.findViewById<MaterialCardView>(R.id.closeBtn)
        val yesBtn = dialogView.findViewById<TextView>(R.id.yesBtn)
        val noBtn = dialogView.findViewById<TextView>(R.id.noBtn)
        closeBtn.setOnClickListener { dialog.dismiss() }
        yesBtn.setOnClickListener {
            startActivity(Intent(this, ProviderSignUpOne::class.java))
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
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
            dialogInterface.dismiss()
            UserUtils.setUserLoggedInVia(this, "", "")
            startActivity(Intent(this, LoginScreen::class.java))
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
                return UserAlertScreen()
            }
            R.id.navigation_offers -> {
                binding.toolBarLayout.visibility = View.GONE
                binding.bottomNavigationView.menu.getItem(4).isChecked = true
                logoutDialog()
                return UserOffersScreen()
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

                }
                is NetworkResponse.Success -> {
                    val response = it.data!!
                    when (response.sp_activated) {
                        "1" -> {
                            // Service Provider Not Activated
                            showActivationDialog()
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
                    }
                }
                is NetworkResponse.Failure -> {
                    snackBar(binding.fab, it.message!!)
                }
            }
        })
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
            toast(this, "Permission Granted")
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
                UserUtils.getCity(
                    this
                ),
                UserUtils.getCountry(this),
                RetrofitBuilder.PROVIDER_KEY,
                1,
                UserUtils.getPostalCode(
                    this
                ),
                UserUtils.getState(this),
                UserUtils.getLatitude(this),
                UserUtils.getLongitude(this),
                UserUtils.getUserId(
                    this
                ).toInt()
            )
            viewModel.saveLocation(this, requestBody).observe(this, {
                when (it) {
                    is NetworkResponse.Loading -> {

                    }
                    is NetworkResponse.Success -> {
                        toast(this, it.data!!)
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

    @SuppressLint("SetTextI18n")
    private fun getUserProfilePicture() {
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
                        loadProfileImage(UserDashboardScreen.binding.image)
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
                        UserDashboardScreen.binding.navigationView,
                        "Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } catch (e: HttpException) {
                Snackbar.make(
                    UserDashboardScreen.binding.navigationView,
                    "Server Busy",
                    Snackbar.LENGTH_SHORT
                ).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(
                    UserDashboardScreen.binding.navigationView,
                    "Something Went Wrong",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(
                    UserDashboardScreen.binding.navigationView,
                    "Please check internet Connection",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }


}