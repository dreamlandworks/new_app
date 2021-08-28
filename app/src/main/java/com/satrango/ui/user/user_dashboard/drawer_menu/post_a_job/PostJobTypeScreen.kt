package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityPostJobTypeScreenBinding

class PostJobTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPostJobTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            specifiedLocationBtn.setOnClickListener {
                startActivity(Intent(this@PostJobTypeScreen, PostJobDateTimeScreen::class.java))
            }

            multiLocationsBtn.setOnClickListener {

            }

            onlineBtn.setOnClickListener {

            }

        }
    }
}