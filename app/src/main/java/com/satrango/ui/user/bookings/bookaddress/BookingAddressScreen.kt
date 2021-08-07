package com.satrango.ui.user.bookings.bookaddress

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingAddressScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.ChangeBookingAddressScreen
import com.satrango.ui.user.bookings.bookaddress.models.BlueCollarBookingReqModel
import com.satrango.ui.user.bookings.bookaddress.models.SingleMoveBookingReqModel
import com.satrango.ui.user.bookings.booklater.MonthsAdapter
import com.satrango.ui.user.bookings.booklater.MonthsInterface
import com.satrango.ui.user.bookings.booklater.MonthsModel
import com.satrango.ui.user.bookings.booknow.BookingAttachmentsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileRepository
import com.satrango.ui.user.user_dashboard.drawer_menu.my_profile.UserProfileViewModel
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class BookingAddressScreen: AppCompatActivity(), MonthsInterface {

    private lateinit var addressList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookingAddressScreenBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var data: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingAddressScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data

        initializeProgressDialog()

        addressList = arrayListOf()
        addressList.add(MonthsModel(UserUtils.getAddress(this@BookingAddressScreen) + ", " + UserUtils.getCity(this@BookingAddressScreen) + ", " + UserUtils.getPostalCode(this@BookingAddressScreen), UserUtils.getTempAddressId(this@BookingAddressScreen),false))

        val factory = ViewModelFactory(UserProfileRepository())
        val viewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        viewModel.userProfileInfo(this).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    val responseData = it.data!!
                    for (address in responseData.address) {
                        addressList.add(MonthsModel(address.locality + ", " + address.city + ", " + address.zipcode, address.id, false))
                    }
                    binding.addressRv.layoutManager = LinearLayoutManager(this@BookingAddressScreen, LinearLayoutManager.HORIZONTAL, false)
                    binding.addressRv.adapter = MonthsAdapter(addressList, this@BookingAddressScreen, "AA")
                    progressDialog.dismiss()
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })

        binding.apply {

            addNewAddress.setOnClickListener {
                startActivity(Intent(this@BookingAddressScreen, ChangeBookingAddressScreen::class.java))
            }

            nextBtn.setOnClickListener {
                UserUtils.temp_address_id = "0"
                UserUtils.address_id = "0"
                for (address in addressList) {
                    if (address.isSelected) {
                        if (UserUtils.getTempAddressId(this@BookingAddressScreen) == address.day) {
                            UserUtils.temp_address_id = UserUtils.getTempAddressId(this@BookingAddressScreen)
                            UserUtils.address_id = "0"
                        } else {
                            UserUtils.temp_address_id = "0"
                            UserUtils.address_id = address.day
                        }
                    }
                }

                if (UserUtils.temp_address_id.isEmpty() && UserUtils.address_id.isEmpty()) {
                    snackBar(binding.nextBtn, "Select Address to Provider Service")
                } else {
                    when(data.category_id) {
                        "1" -> {
                            bookSingleMoveServiceProvider()
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun bookSingleMoveServiceProvider() {
        val requestBody = SingleMoveBookingReqModel(
            UserUtils.address_id.toInt(),
            data.per_hour,
            BookingAttachmentsScreen.encodedImages,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
            1,
            1,
            UserUtils.job_description,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            data.users_id.toInt(),
            UserUtils.started_at,
            UserUtils.temp_address_id.toInt(),
            UserUtils.time_slot_from,
            UserUtils.time_slot_to,
            UserUtils.getUserId(this).toInt()
        )
        Log.e("SINGLE MOVE", Gson().toJson(requestBody))
        val factory = ViewModelFactory(BookingRepository())
        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
        viewModel.singleMoveBooking(this, requestBody).observe(this, {
            when(it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    toast(this, it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.nextBtn, it.message!!)
                }
            }
        })

    }

    override fun selectedMonth(position: Int, listType: String) {
        val tempAddress = arrayListOf<MonthsModel>()
        addressList.onEachIndexed { index, month ->
            if (index == position) {
                tempAddress.add(MonthsModel(month.month, month.day,true))
            } else {
                tempAddress.add(MonthsModel(month.month, month.day,false))
            }
        }
        addressList = tempAddress
        binding.addressRv.adapter = MonthsAdapter(addressList, this@BookingAddressScreen, "AA")
    }

    private fun initializeProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading...")
    }
}