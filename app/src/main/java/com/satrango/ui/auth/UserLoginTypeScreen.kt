package com.satrango.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.satrango.databinding.ActivityUserLoginTypeScreenBinding
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.utils.LocaleHelper
import com.satrango.utils.UserUtils
import java.util.*


class UserLoginTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginTypeScreenBinding

    @SuppressLint("SimpleDateFormat", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserLoginTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Firebase.messaging.isAutoInitEnabled = true

        if (!UserUtils.updateNewFCMToken(this)) {
//            snackBar(binding.userBtn, "Please check internet connection!")
            Handler().postDelayed({
                finish()
            }, 3000)
        }

        binding.apply {

            userBtn.setOnClickListener {
                startActivity(Intent(this@UserLoginTypeScreen, UserDashboardScreen::class.java))
                finish()
            }
            serviceProviderBtn.setOnClickListener {
                UserUtils.saveFromFCMService(this@UserLoginTypeScreen, false)
                startActivity(Intent(this@UserLoginTypeScreen, ProviderDashboard::class.java))
                finish()
            }

        }

        checkGooglePlayServices()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.wrap(newBase!!, UserUtils.getAppLanguage(newBase)))
    }

    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e("TAG", "Error: ask user to update google play services and manage the error.")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i("TAG", "Google play services updated")
            true
        }
    }

//    private fun setLocale(lang: String) {
//        val myLocale = Locale(lang)
//        val res: Resources = resources
//        val dm: DisplayMetrics = res.displayMetrics
//        val conf: Configuration = res.configuration
//        conf.locale = myLocale
//        res.updateConfiguration(conf, dm)
////        val refresh = Intent(this, UserLoginTypeScreen::class.java)
////        finish()
////        startActivity(refresh)
//        finish()
//        startActivity(intent)
//    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


}