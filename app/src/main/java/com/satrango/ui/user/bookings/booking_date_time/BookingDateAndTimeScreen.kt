package com.satrango.ui.user.bookings.booking_date_time

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ActivityBookingDateAndTimeScreenBinding
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.UserSearchViewProfileScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.BlockedTimeSlot
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.ui.user.user_dashboard.search_service_providers.models.SearchServiceProviderResModel
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList

class BookingDateAndTimeScreen : AppCompatActivity(), MonthsInterface {


    private lateinit var availableTimeSlots: java.util.ArrayList<MonthsModel>
    private lateinit var availableSlots: java.util.ArrayList<MonthsModel>
    private lateinit var spDetails: SearchServiceProviderResModel
    private lateinit var calendar: Calendar
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookingDateAndTimeScreenBinding
    private lateinit var data: Data

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDateAndTimeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        data = intent.getSerializableExtra(getString(R.string.service_provider)) as Data
        spDetails = Gson().fromJson(UserUtils.getSelectedSPDetails(this), SearchServiceProviderResModel::class.java)

        calendar = Calendar.getInstance()
        loadDates()
        loadTimings(0)
        updateUI(data)

        binding.nextBtn.setOnClickListener {
            validateFields()
        }


    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = data.per_hour
    }

    private fun validateFields() {
        UserUtils.scheduled_date = ""
        for (day in availableSlots) {
            if (day.isSelected) {
                UserUtils.scheduled_date = day.month
            }
        }
        UserUtils.time_slot_from = ""
        UserUtils.time_slot_to = ""
        for (time in availableTimeSlots) {
            if (time.isSelected) {
                val timing = time.month.trim().split("to")
                UserUtils.time_slot_from = timing[0].trim() + time.day.trim()
                UserUtils.time_slot_to = timing[1] + time.day.trim()
            }
        }
        if (UserUtils.scheduled_date.isEmpty()) {
            snackBar(binding.nextBtn, "Please Select Date")
        } else if (UserUtils.time_slot_from.isEmpty() || UserUtils.time_slot_to.isEmpty()) {
            snackBar(binding.nextBtn, "Please Select TimeSlot")
        } else {
            if (data.category_id == "3") {
                val intent = Intent(this@BookingDateAndTimeScreen, BookingAddressScreen::class.java)
                intent.putExtra(getString(R.string.service_provider), data)
                startActivity(intent)
            } else {
                val intent = Intent(
                    this@BookingDateAndTimeScreen,
                    BookingAttachmentsScreen::class.java
                )
                intent.putExtra(getString(R.string.service_provider), data)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadDates() {
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        availableSlots = ArrayList()
        for(day in daysList) {
            for (slots in spDetails.slots_data) {
                if (slots.user_id == data.users_id) {
                    for(slot in slots.preferred_time_slots) {
                        val inFormat = SimpleDateFormat("dd-MM-yyyy")
                        val d = day.month.split("-")
                        val date= inFormat.parse("${d[2]}-${d[1]}-${d[0]}")
                        val outFormat = SimpleDateFormat("EEEE")
                        val goal: String = outFormat.format(date!!)
                        val weekDays = resources.getStringArray(R.array.days)
                        for (index in weekDays.indices) {
                            if (weekDays[index] == goal) {
                                if (slot.day_slot == (index + 1).toString()) {
                                    availableSlots.add(day)
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.dayRv.layoutManager = LinearLayoutManager(this@BookingDateAndTimeScreen, LinearLayoutManager.HORIZONTAL, false)
        binding.dayRv.adapter = MonthsAdapter(availableSlots, this@BookingDateAndTimeScreen, "D")
    }

    private fun loadTimings(position: Int) {
        availableTimeSlots = ArrayList()
        val bookedSlots = ArrayList<MonthsModel>()
        val actualTimeSlots = ArrayList<MonthsModel>()
        for (slot in spDetails.slots_data)  {
            if (slot.user_id == data.users_id) {
                for (time in slot.blocked_time_slots) {
                    if (time.date == availableSlots[position].month) {
                        bookedSlots.add(MonthsModel(time.time_slot_from, "", false))}
                    }
            }
        }
        val timingsList = resources.getStringArray(R.array.bookingTimings)
        for (index in timingsList.indices) {
            actualTimeSlots.add(MonthsModel(timingsList[index], "", false))
        }
        for (index in actualTimeSlots.indices) {
            if (bookedSlots.isNotEmpty()) {
                for (book in bookedSlots) {
                    if (book.month != actualTimeSlots[index].month.split("\nto\n")[0]) {
                        availableTimeSlots.add(actualTimeSlots[index])
                    }
                }
            } else {
                availableTimeSlots.add(actualTimeSlots[index])
            }
        }
        binding.timeRv.layoutManager = LinearLayoutManager(this@BookingDateAndTimeScreen, LinearLayoutManager.HORIZONTAL, false)
        binding.timeRv.adapter = MonthsAdapter(availableTimeSlots, this@BookingDateAndTimeScreen, "T")
    }

    private fun loadDays(
        year: Int,
        month: Int
    ): ArrayList<MonthsModel> {
        var daysInMonth = getDaysInMonth(year, month)
        daysList = arrayListOf()
        for (day in 1..daysInMonth) {
            if (day >= calendar.get(Calendar.DAY_OF_MONTH)) {
                daysList.add(MonthsModel(calendar.get(Calendar.YEAR).toString() + "-" + String.format("%02d", month) + "-" + String.format("%02d", day), day.toString(), false))
//                Log.e("DATE:", Gson().toJson(MonthsModel(calendar.get(Calendar.YEAR).toString() + "-" + String.format("%02d", month) + "-" + String.format("%02d", day), day.toString(), false)))
            }
        }
        daysInMonth = getDaysInMonth(year, month + 1)
        for (day in 1..daysInMonth) {
            daysList.add(MonthsModel(calendar.get(Calendar.YEAR).toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day), day.toString(), false))
//            Log.e("DATE:", Gson().toJson(MonthsModel(calendar.get(Calendar.YEAR).toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day), day.toString(), false)))
        }
        return daysList
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val yearMonthObject: YearMonth =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                YearMonth.of(year, month)
            } else {
                return 30
            }
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject.lengthOfMonth()
        } else {
            30
        }
    }

    override fun selectedMonth(position: Int, listType: String) {
        val tempMonths = arrayListOf<MonthsModel>()

        if (listType == "D") { // Days List
            availableSlots.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                }
            }
            availableSlots = tempMonths
            binding.dayRv.adapter = MonthsAdapter(availableSlots, this, "D")
            binding.dayRv.scrollToPosition(position)
            loadTimings(position)
        }
        if (listType == "T") { // Timings List
            availableTimeSlots.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                }
            }
            availableTimeSlots = tempMonths
            binding.timeRv.adapter = MonthsAdapter(availableTimeSlots, this, "T")
            binding.timeRv.scrollToPosition(position)
        }

    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this, UserSearchViewProfileScreen::class.java)
        intent.putExtra(getString(R.string.service_provider), data)
        startActivity(intent)
    }

}