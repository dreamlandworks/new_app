package com.satrango.ui.user_dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.satrango.R
import com.satrango.databinding.ActivityUserDashboardScreenBinding


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

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_offers -> {
                    Toast.makeText(this, "Offers Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_alerts -> {
                    Toast.makeText(this, "Alerts Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_chats -> {
                    Toast.makeText(this, "Chats Clicked", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        binding.navigationView.setNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.userOptHome -> {
                    Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}