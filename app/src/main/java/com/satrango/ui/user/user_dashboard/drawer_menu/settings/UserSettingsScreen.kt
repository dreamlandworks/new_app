package com.satrango.ui.user.user_dashboard.drawer_menu.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
import com.satrango.utils.UserUtils.isProvider
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.ZohoLiveChat
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
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
//                reachCustomerSupport()
                if (ZohoSalesIQ.isLiveChatAvailable()) {
                    ZohoLiveChat.Chat.show()
                } else {
                    ZohoSalesIQ.showLauncher(true)
                }
            }
        }

        reachCustomerSupport()

    }

    private fun reachCustomerSupport() {
        val initConfig = InitConfig()
        ZohoSalesIQ.init(
            application,
            "ZqUGlb8vQgTD%2B2nQ29l8D8omc3VIw5JY7e%2F5zKbaWIlaHOWhQkgKjONc01%2F8BrdODDQan%2BMS85znVdDi3X4fqb0Jg1lKFfev_in",
            "xfuLcMoNAd0Fsx6u3Jr7h57LR6dqWDHACHgTdHZkQpexceH4uHQFBbtcHz%2Bw2zHOU5MY68vPnvd04ktPS8%2Bq%2Bo%2BUjY93hdvudrkbCbaAfxY66iHDOw81Yi6tqHFhW0wU4mSp9seXEj%2FtjLNtagh%2BvuXCkQb0krleRxTIyYyhFJwKn9iU1KaanHoXu8Z4WViD",
            initConfig,
            object : InitListener {

                override fun onInitSuccess() {
//                if (ZohoSalesIQ.isLiveChatAvailable()) {
//                    ZohoLiveChat.Chat.show()
//                } else {
//                    ZohoSalesIQ.showLauncher(true)
//                }
                }

                override fun onInitError(errorCode: Int, errorMessage: String?) {
                    Toast.makeText(this@UserSettingsScreen, errorMessage!!, Toast.LENGTH_SHORT)
                        .show()
                }
            })


//        val config = FreshchatConfig(
//            "242471f0-5f77-451e-9875-91d4f6d8df65",
//            "c8300a05-b9cf-4f23-a225-6d663305959f"
//        )
//        config.domain = "msdk.in.freshchat.com"
//        config.isCameraCaptureEnabled = true
//        config.isGallerySelectionEnabled = true
//        val freshChat = Freshchat.getInstance(applicationContext)
//        freshChat.init(config)
//
//        val freshChatUser = Freshchat.getInstance(applicationContext).user
//        freshChatUser.firstName = getFirstName(applicationContext)
//        freshChatUser.lastName = getLastName(applicationContext)
//        freshChatUser.email = getMail(applicationContext)
//        freshChatUser.setPhone("+91", getPhoneNo(applicationContext))
//        freshChat.user = freshChatUser


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

//        Freshchat.showConversations(applicationContext)

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
                window.statusBarColor = resources.getColor(R.color.purple_700)
            }
        }
    }
}