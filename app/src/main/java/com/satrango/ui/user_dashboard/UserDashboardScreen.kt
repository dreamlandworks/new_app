package com.satrango.ui.user_dashboard

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.satrango.R
import com.satrango.databinding.ActivityUserDashboardScreenBinding
import de.hdodenhof.circleimageview.CircleImageView


class UserDashboardScreen : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolBar,
            com.satrango.R.string.app_name,
            com.satrango.R.string.app_name
        )
        binding.navigationView.itemIconTintList = null
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val headerView = binding.navigationView[0]
        val profileImage = headerView.findViewById<CircleImageView>(R.id.profileImage)
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val balanceAmount = headerView.findViewById<TextView>(R.id.balance)
        val userBtn = headerView.findViewById<LinearLayout>(R.id.userBtn)
        val providerBtn = headerView.findViewById<LinearLayout>(R.id.providerBtn)

        userBtn.setOnClickListener { Toast.makeText(this, "User Clicked", Toast.LENGTH_SHORT).show() }
        providerBtn.setOnClickListener { Toast.makeText(this, "Provider Clicked", Toast.LENGTH_SHORT).show() }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    loadFragment(UserHomeScreen())
                }
                R.id.navigation_offers -> {
                    binding.toolBar.visibility = View.GONE
                    loadFragment(UserOffersScreen())
                }
                R.id.navigation_alerts -> {
                    binding.toolBar.visibility = View.GONE
                    loadFragment(UserAlertScreen())
                }
                R.id.navigation_chats -> {
                    binding.toolBar.visibility = View.GONE
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
                    Toast.makeText(this, "Post A Job Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyAccount -> {
                    Toast.makeText(this, "My Account Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyBooking -> {
                    Toast.makeText(this, "My Booking Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyJobPosts -> {
                    Toast.makeText(this, "My Job Posts Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptMyProfile -> {
                    Toast.makeText(this, "My Profile Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptSettings -> {
                    Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.userOptLogOut -> {
                    Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

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
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.toolBar.visibility = View.VISIBLE
    }
}