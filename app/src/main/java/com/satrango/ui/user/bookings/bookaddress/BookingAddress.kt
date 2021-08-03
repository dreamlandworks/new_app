package com.satrango.ui.user.bookings.bookaddress

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ActivityBookingAddressBinding
import com.satrango.ui.user.bookings.AddBookingAddress
import com.satrango.ui.user.bookings.booklater.MonthsAdapter
import com.satrango.ui.user.bookings.booklater.MonthsInterface
import com.satrango.ui.user.bookings.booklater.MonthsModel
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class BookingAddress : AppCompatActivity(), MonthsInterface {

    private lateinit var addressList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookingAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        binding.apply {
            addressList = arrayListOf()
            addressList.add(MonthsModel("Krishna Ganga Spinning Mill, Thimmapuram, 522619", "",false))
            addressList.add(MonthsModel("Nrt Center, Chilakaluripet, 522616", "",false))
            addressRv.layoutManager = LinearLayoutManager(this@BookingAddress, LinearLayoutManager.HORIZONTAL, false)
            addressRv.adapter = MonthsAdapter(addressList, this@BookingAddress, "AA")

            addNewAddress.setOnClickListener { startActivity(Intent(this@BookingAddress, AddBookingAddress::class.java)) }
        }

    }

    override fun selectedMonth(position: Int, listType: String) {
        val tempAddress = arrayListOf<MonthsModel>()
        addressList.onEachIndexed { index, month ->
            if (index == position) {
                tempAddress.add(MonthsModel(month.month, "",true))
            } else {
                tempAddress.add(MonthsModel(month.month, "",false))
            }
        }
        binding.addressRv.adapter = MonthsAdapter(tempAddress, this@BookingAddress, "AA")
    }
}