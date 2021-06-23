package com.satrango.ui.user_dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
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
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import com.satrango.R
import com.satrango.databinding.ActivityUserDashboardScreenBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.auth.LoginScreen
import com.satrango.ui.user_dashboard.drawer_menu.UserSearchViewProfileScreen
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesScreen
import com.satrango.ui.user_dashboard.drawer_menu.browse_categories.models.BrowseCategoryReqModel
import com.satrango.ui.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.ui.user_dashboard.drawer_menu.refer_earn.UserReferAndEarn
import com.satrango.ui.user_dashboard.user_home_screen.UserHomeScreen
import com.satrango.utils.PermissionUtils
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.*

class UserDashboardScreen : AppCompatActivity() {

    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var toolBarBackBtn: ImageView

    private lateinit var userProviderSwitch: SwitchCompat
    private lateinit var profileImage: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolBar, R.string.app_name, com.satrango.R.string.app_name)
        binding.navigationView.itemIconTintList = null
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.black)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        Toast.makeText(this, "Your Referral User ID: ${UserUtils.getReferralId(this)} | ${UserUtils.getUserId(this)}", Toast.LENGTH_SHORT).show()

        val headerView = binding.navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.profileImage)
        userProviderSwitch = headerView.findViewById(R.id.userProviderSwitch)
        referralId = headerView.findViewById(R.id.referralId)
        toolBarTitle = headerView.findViewById(R.id.toolBarTitle)
        toolBarBackTVBtn = headerView.findViewById(R.id.toolBarBackTVBtn)
        toolBarBackBtn = headerView.findViewById(R.id.toolBarBackBtn)
        updateHeaderDetails()
        userProviderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            toast(this, isChecked.toString())
        }

        loadFragment(UserHomeScreen())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.toolBarLayout.visibility = View.VISIBLE
                    loadFragment(UserHomeScreen())
                }
                R.id.navigation_offers -> {
                    binding.toolBarLayout.visibility = View.GONE
                    loadFragment(UserOffersScreen())
                }
                R.id.navigation_alerts -> {
                    binding.toolBarLayout.visibility = View.GONE
                    loadFragment(UserAlertScreen())
                }
                R.id.navigation_chats -> {
                    binding.toolBarLayout.visibility = View.GONE
                    loadFragment(UserChatScreen())
                }
            }
            true
        }

        binding.navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.userOptHome -> {
                    loadFragment(UserHomeScreen())
                }
                R.id.userOptPostJob -> {
                    startActivity(Intent(this, UserSearchViewProfileScreen::class.java))
                    Toast.makeText(this, "Post A Job Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptBrowseCategories -> {
                    startActivity(Intent(this, BrowseCategoriesScreen::class.java))
                }
                R.id.userOptMyAccount -> {
                    startActivity(Intent(this, UserMyAccountScreen::class.java))
                }
                R.id.userOptMyBooking -> {
                    Toast.makeText(this, "My Booking Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyJobPosts -> {
                    Toast.makeText(this, "My Job Posts Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyProfile -> {
                    startActivity(Intent(this, UserProfileScreen::class.java))
                }
                R.id.userOptReferEarn -> {
                    startActivity(Intent(this, UserReferAndEarn::class.java))
                }
                R.id.userOptSettings -> {
                    Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
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
        Glide.with(profileImage).load(UserUtils.getUserProfilePic(this)).into(profileImage)
        referralId.text = resources.getString(R.string.referralId) + UserUtils.getReferralId(this)
        toolBarTitle.text = resources.getString(R.string.welcome) + UserUtils.getUserName(this)
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
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(fragment.tag).commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (binding.bottomNavigationView.selectedItemId == R.id.userOptHome) {
                binding.toolBar.visibility = View.VISIBLE
            }
            super.onBackPressed()
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
            fetchLocation(this)
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
                UserUtils.latitude = latitude.toString()
                UserUtils.longitute = longitude.toString()
                UserUtils.city = city
                UserUtils.state = state
                UserUtils.country = country
                UserUtils.postalCode = postalCode
                UserUtils.address = knownName
                binding.userLocation.text = UserUtils.city
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
                val requestBody =
                    BrowseCategoryReqModel(UserUtils.getUserId(this@UserDashboardScreen))
                val response = RetrofitBuilder.getRetrofitInstance().getUserProfile(requestBody)
                val responseData = response.data
                if (response.status == 200) {
                    if (responseData.profile_pic.isNotEmpty()) {
                        val imageUrl = RetrofitBuilder.BASE_URL + responseData.profile_pic
                        UserUtils.saveUserName(this@UserDashboardScreen, responseData.fname + " " + responseData.lname)
                        UserUtils.saveUserProfilePic(this@UserDashboardScreen, imageUrl)
                        if (responseData.referral_id != null) {
                            UserUtils.saveReferralId(this@UserDashboardScreen, responseData.referral_id)
                        }
                        Glide.with(binding.image).load(UserUtils.getUserProfilePic(this@UserDashboardScreen)).into(binding.image)
                        updateHeaderDetails()
                    }
                } else {
                    Snackbar.make(binding.navigationView, "Something went wrong!", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Snackbar.make(binding.navigationView, "Server Busy", Snackbar.LENGTH_SHORT).show()
            } catch (e: JsonSyntaxException) {
                Snackbar.make(binding.navigationView, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                    .show()
            } catch (e: SocketTimeoutException) {
                Snackbar.make(binding.navigationView, "Please check internet Connection", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

}
