package com.satrango.ui.service_provider.provider_dashboard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.satrango.R
import com.satrango.databinding.ActivityProviderDashboardBinding
import com.satrango.ui.auth.login_screen.LoginScreen
import com.satrango.ui.auth.provider_signup.provider_sign_up_one.ProviderSignUpOne
import com.satrango.ui.user.bookings.booklater.BookLater
import com.satrango.ui.user.user_dashboard.UserChatScreen
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.UserOffersScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.UserSearchViewProfileScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.browse_categories.BrowseCategoriesScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.UserMyAccountScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.refer_earn.UserReferAndEarn
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.UserSettingsScreen
import com.satrango.ui.user.user_dashboard.user_alerts.UserAlertScreen
import com.satrango.ui.user.user_dashboard.user_home_screen.UserHomeScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.loadProfileImage
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView

class ProviderDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityProviderDashboardBinding
    private lateinit var referralId: TextView
    private lateinit var toolBarTitle: TextView
    private lateinit var toolBarBackTVBtn: TextView
    private lateinit var toolBarBackBtn: ImageView
    private lateinit var userProviderSwitch: SwitchCompat
    private lateinit var profileImage: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            toast(this, isChecked.toString())
        }

        loadFragment(ProviderHomeScreen())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.toolBarLayout.visibility = View.VISIBLE
                    loadFragment(UserHomeScreen())
                }
                R.id.navigation_offers -> {
                    binding.toolBarLayout.visibility = View.GONE
//                    loadFragment(UserOffersScreen())
                }
                R.id.navigation_alerts -> {
                    binding.toolBarLayout.visibility = View.GONE
//                    loadFragment(UserAlertScreen())
                }
                R.id.navigation_chats -> {
                    binding.toolBarLayout.visibility = View.GONE
//                    loadFragment(UserChatScreen())
                }
            }
            true
        }


        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.userOptHome -> {
                    loadFragment(ProviderHomeScreen())
                }
                R.id.userOptPostJob -> {
//                    startActivity(Intent(this, UserSearchViewProfileScreen::class.java))
//                    Toast.makeText(this, "Post A Job Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptBrowseCategories -> {
//                    startActivity(Intent(this, BrowseCategoriesScreen::class.java))
                }
                R.id.userOptMyAccount -> {
//                    startActivity(Intent(this, UserMyAccountScreen::class.java))
                }
                R.id.userOptMyBooking -> {
//                    startActivity(Intent(this, BookLater::class.java))
                }
                R.id.userOptMyJobPosts -> {
//                    Toast.makeText(this, "My Job Posts Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyProfile -> {
//                    startActivity(Intent(this, UserProfileScreen::class.java))
                }
                R.id.userOptReferEarn -> {
//                    startActivity(Intent(this, UserReferAndEarn::class.java))
                }
                R.id.userOptSettings -> {
//                    startActivity(Intent(this, UserSettingsScreen::class.java))
                }
                R.id.userOptLogOut -> {
                    logoutDialog()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        showActivationDialog()

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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.service_provider_activation_dialog, null)
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
            super.onBackPressed()
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

}