package com.satrango.ui.auth.user_signup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.satrango.databinding.ActivitySignUpScreenTwoBinding
import com.satrango.utils.PermissionUtils
import java.util.*

class UserSignUpScreenTwo : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpScreenTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionUtils.checkAndRequestPermissions(this)

        binding.apply {
            nextBtn.setOnClickListener { startActivity(Intent(this@UserSignUpScreenTwo, UserSignUpScreenThree::class.java)) }
        }

    }




}