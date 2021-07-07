package com.satrango.ui.user.bookings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.satrango.databinding.ActivityAddBookingAddressBinding

class AddBookingAddress : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookingAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}