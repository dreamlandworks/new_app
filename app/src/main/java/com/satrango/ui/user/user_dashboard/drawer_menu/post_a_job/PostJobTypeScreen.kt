package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityPostJobTypeScreenBinding
import com.satrango.utils.UserUtils

class PostJobTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPostJobTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            specifiedLocationBtn.setOnClickListener {
                UserUtils.EDIT_MY_JOB_POST = false
                UserUtils.setFromJobPostSingleMove(this@PostJobTypeScreen, true)
                UserUtils.setFromJobPostMultiMove(this@PostJobTypeScreen, false)
                UserUtils.setFromJobPostBlueCollar(this@PostJobTypeScreen, false)
                startActivity(Intent(this@PostJobTypeScreen, PostJobDateTimeScreen::class.java))
            }

            multiLocationsBtn.setOnClickListener {
                UserUtils.EDIT_MY_JOB_POST = false
                UserUtils.setFromJobPostMultiMove(this@PostJobTypeScreen, true)
                UserUtils.setFromJobPostSingleMove(this@PostJobTypeScreen, false)
                UserUtils.setFromJobPostBlueCollar(this@PostJobTypeScreen, false)
                startActivity(Intent(this@PostJobTypeScreen, PostJobDateTimeScreen::class.java))
            }

            onlineBtn.setOnClickListener {
                UserUtils.EDIT_MY_JOB_POST = false
                UserUtils.setFromJobPostBlueCollar(this@PostJobTypeScreen, true)
                UserUtils.setFromJobPostSingleMove(this@PostJobTypeScreen, false)
                UserUtils.setFromJobPostMultiMove(this@PostJobTypeScreen, false)
                startActivity(Intent(this@PostJobTypeScreen, PostJobDateTimeScreen::class.java))
            }

        }
    }
}