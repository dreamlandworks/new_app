package com.satrango.ui.user.user_dashboard.drawer_menu.settings

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivityUserSettingsScreenBinding
import com.satrango.databinding.UserDashboardHeaderBinding
import com.satrango.ui.auth.user_signup.TermsAndConditionScreen
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.ComplaintScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.UserFAQScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.FeedBackScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.ComplaintRequestScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import de.hdodenhof.circleimageview.CircleImageView

class UserSettingsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserSettingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        binding.apply {
            termsAndConditions.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, TermsAndConditionScreen::class.java)) }
            privacyPolicy.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, UserPrivacyPolicyScreen::class.java)) }
            faqs.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, UserFAQScreen::class.java)) }
            raiseComplaintBtn.setOnClickListener {
                ComplaintScreen.bookingId = 0
                startActivity(Intent(this@UserSettingsScreen, ComplaintScreen::class.java))
            }
            feedBackBtn.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, FeedBackScreen::class.java)) }
            myRequests.setOnClickListener { startActivity(Intent(this@UserSettingsScreen, ComplaintRequestScreen::class.java)) }
        }

    }

    override fun onBackPressed() {
        if (isProvider(this)) {
            startActivity(Intent(this, ProviderDashboard::class.java))
        } else {
            startActivity(Intent(this, UserDashboardScreen::class.java))
        }
    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener {
            if (isProvider(this)) {
                startActivity(Intent(this, ProviderDashboard::class.java))
            } else {
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
        }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener {
            if (isProvider(this)) {
                startActivity(Intent(this, ProviderDashboard::class.java))
            } else {
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
        }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.settings)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)
        if (isProvider(this)) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(resources.getColor(R.color.purple_700))
            }
        }
    }
}