package com.satrango.ui.splash

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.satrango.databinding.ActivitySplashScreenBinding
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.UserLoginTypeScreen
import com.satrango.utils.AuthUtils
import com.satrango.utils.UserUtils

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUserNavigation() {
        Handler().postDelayed({
            if (AuthUtils.getFirstTimeLaunch(this)) {
                startActivity(Intent(this, IntroScreen::class.java))
            } else {
                if (UserUtils.getUserLoggedInVia(this).isNotEmpty()) {
                    startActivity(Intent(this, UserLoginTypeScreen::class.java))
                } else {
                    if (UserUtils.getUserId(this).isNotEmpty()) {
                        startActivity(Intent(this, UserLoginTypeScreen::class.java))
                    } else {
                        startActivity(Intent(this, LoginScreen::class.java))
                    }
                }
            }
            finish()
        }, 1000)

    }

    override fun onStart() {
        super.onStart()
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    val referralUserId = deepLink.toString().split("=")[1]
                    UserUtils.setReferralId(this, referralUserId)
                }
                setUserNavigation()
            }
            .addOnFailureListener(this) {
                Toast.makeText(this, "Refer Failed", Toast.LENGTH_SHORT).show()
                setUserNavigation()
            }
    }
}