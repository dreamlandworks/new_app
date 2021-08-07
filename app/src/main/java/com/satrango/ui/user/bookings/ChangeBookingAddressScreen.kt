package com.satrango.ui.user.bookings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityChangeBookingAddressScreenBinding

class ChangeBookingAddressScreen : AppCompatActivity() {

    private lateinit var binding: ActivityChangeBookingAddressScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeBookingAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}