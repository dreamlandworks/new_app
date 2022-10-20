package com.satrango.ui.user.user_dashboard.drawer_menu.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.satrango.R
import com.satrango.databinding.ActivityUserSettingsScreenBinding
import com.satrango.ui.auth.user_signup.TermsAndConditionScreen
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.ComplaintScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.faqs.UserFAQScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.feedback.FeedBackScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.ComplaintRequestScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.getFirstName
import com.satrango.utils.UserUtils.getLastName
import com.satrango.utils.UserUtils.getMail
import com.satrango.utils.UserUtils.getPhoneNo
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class UserSettingsScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserSettingsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()

        binding.apply {
            termsAndConditions.setOnClickListener {
                startActivity(
                    Intent(
                        this@UserSettingsScreen,
                        TermsAndConditionScreen::class.java
                    )
                )
            }
            privacyPolicy.setOnClickListener {
                startActivity(
                    Intent(
                        this@UserSettingsScreen,
                        UserPrivacyPolicyScreen::class.java
                    )
                )
            }
            faqs.setOnClickListener {
                startActivity(
                    Intent(
                        this@UserSettingsScreen,
                        UserFAQScreen::class.java
                    )
                )
            }
            raiseComplaintBtn.setOnClickListener {
                ComplaintScreen.bookingId = 0
                startActivity(Intent(this@UserSettingsScreen, ComplaintScreen::class.java))
            }
            feedBackBtn.setOnClickListener {
                startActivity(
                    Intent(
                        this@UserSettingsScreen,
                        FeedBackScreen::class.java
                    )
                )
            }
            myRequests.setOnClickListener {
                startActivity(
                    Intent(
                        this@UserSettingsScreen,
                        ComplaintRequestScreen::class.java
                    )
                )
            }
            customerCare.setOnClickListener {
                reachCustomerSupport()
            }
        }

    }

    private fun reachCustomerSupport() {
//        val config = FreshchatConfig(
//            "20874dd7-eda0-4655-b0e0-e18a97b4f80a",
//            "579f96b9-4621-439d-b18a-5ce523f6b20a"
//        )
        val config = FreshchatConfig(
            "242471f0-5f77-451e-9875-91d4f6d8df65",
            "c8300a05-b9cf-4f23-a225-6d663305959f"
        )
        config.domain = "msdk.in.freshchat.com"
        config.isCameraCaptureEnabled = true
        config.isGallerySelectionEnabled = true
        val freshChat = Freshchat.getInstance(applicationContext)
        freshChat.init(config)

        val freshChatUser = Freshchat.getInstance(applicationContext).user
        freshChatUser.firstName = getFirstName(applicationContext)
        freshChatUser.lastName = getLastName(applicationContext)
        freshChatUser.email = getMail(applicationContext)
        freshChatUser.setPhone("+91", getPhoneNo(applicationContext))
        freshChat.user = freshChatUser
//        toast(
//            this,
//            getFirstName(this) + "|" + getLastName(this) + "|" + getMail(this) + "|" + getPhoneNo(
//                this
//            )
//        )

//        val userMeta: MutableMap<String, String> = HashMap()
//        userMeta["userLoginType"] = "Facebook"
//        userMeta["city"] = "SpringField"
//        userMeta["age"] = "22"
//        userMeta["userType"] = "premium"
//        userMeta["numTransactions"] = "5"
//        userMeta["usedWishlistFeature"] = "yes"
//        Freshchat.getInstance(applicationContext).setUserProperties(userMeta)

        Freshchat.showConversations(applicationContext)

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
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text =
            resources.getString(R.string.settings)
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