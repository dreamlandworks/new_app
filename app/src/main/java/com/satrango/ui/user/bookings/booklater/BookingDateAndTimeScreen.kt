package com.satrango.ui.user.bookings.booklater

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
import com.satrango.databinding.ActivityBookingDateAndTimeScreenBinding
import com.satrango.ui.user.bookings.booknow.BookingAttachmentsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.time.YearMonth
import java.util.*

class BookingDateAndTimeScreen : AppCompatActivity(), MonthsInterface {


    private lateinit var calendar: Calendar
    private lateinit var timings: ArrayList<MonthsModel>
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookingDateAndTimeScreenBinding
    private lateinit var data: Data

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

        calendar = Calendar.getInstance()
        loadDates()
        loadTimings()

        binding.nextBtn.setOnClickListener {
            validateFields()
        }
    }

    private fun validateFields() {
        UserUtils.scheduled_date = ""
        for (day in daysList) {
            if (day.isSelected) {
                val months = resources.getStringArray(R.array.months)
                for (index in months.indices) {
                    if (day.month == months[index]) {
                        UserUtils.scheduled_date = "${calendar.get(Calendar.YEAR)}-${index}-${day.day.split(" ")[0]}"
                        toast(this, UserUtils.scheduled_date)
                    }
                }
            }
        }
        UserUtils.time_slot_from = ""
        UserUtils.time_slot_to = ""
        for (time in timings) {
            if (time.isSelected) {
//                    Log.e("TIME", time.month)
                val timing = time.month.trim().split("to")
                UserUtils.time_slot_from = timing[0].trim() + time.day.trim()
                UserUtils.time_slot_to = timing[1].toString() + time.day.trim()
            }
        }
        if (UserUtils.scheduled_date.isEmpty()) {
            snackBar(binding.nextBtn, "Please Select Date")
        } else if (UserUtils.time_slot_from.isEmpty() || UserUtils.time_slot_to.isEmpty()) {
            snackBar(binding.nextBtn, "Please Select TimeSlot")
        } else {
            val intent = Intent(this@BookingDateAndTimeScreen, BookingAttachmentsScreen::class.java)
            intent.putExtra(getString(R.string.service_provider), data)
            startActivity(intent)
        }
    }

    private fun loadDates() {
        val months = resources.getStringArray(R.array.months)
        val month = months[calendar.get(Calendar.MONTH)]
        val nextMonth = months[calendar.get(Calendar.MONTH) + 1]
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), month, nextMonth)
        binding.dayRv.layoutManager = LinearLayoutManager(this@BookingDateAndTimeScreen, LinearLayoutManager.HORIZONTAL, false)
        binding.dayRv.adapter = MonthsAdapter(daysList, this@BookingDateAndTimeScreen, "D")
    }

    private fun loadTimings() {
        timings = arrayListOf()
        val timingsList = resources.getStringArray(R.array.bookingTimings)
        for (index in timingsList.indices) {
            if (index < 12) {
                timings.add(MonthsModel(timingsList[index], "AM", false))
            } else {
                timings.add(MonthsModel(timingsList[index], "PM", false))
            }
        }
        binding.timeRv.layoutManager = LinearLayoutManager(this@BookingDateAndTimeScreen, LinearLayoutManager.HORIZONTAL, false)
        binding.timeRv.adapter = MonthsAdapter(timings, this@BookingDateAndTimeScreen, "T")

    }

    private fun loadDays(
        year: Int,
        month: Int,
        currentMonth: String,
        nextMonth: String
    ): ArrayList<MonthsModel> {
        var daysInMonth = getDaysInMonth(year, month)
        daysList = arrayListOf()
        for (day in 1..daysInMonth) {
            if (day >= calendar.get(Calendar.DAY_OF_MONTH)) {
                when (day.toString().last()) {
                    1.toChar() -> {
                        daysList.add(MonthsModel(currentMonth, "$day st", false))
                    }
                    2.toChar() -> {
                        daysList.add(MonthsModel(currentMonth, "$day nd", false))
                    }
                    3.toChar() -> {
                        daysList.add(MonthsModel(currentMonth, "$day rd", false))
                    }
                    else -> {
                        daysList.add(MonthsModel(currentMonth, "$day th", false))
                    }
                }
            }
        }
        daysInMonth = getDaysInMonth(year, month + 1)
        for (day in 1..daysInMonth) {
            when (day) {
                1 -> {
                    daysList.add(MonthsModel(nextMonth, "$day st", false))
                }
                2 -> {
                    daysList.add(MonthsModel(nextMonth, "$day nd", false))
                }
                3 -> {
                    daysList.add(MonthsModel(nextMonth, "$day rd", false))
                }
                else -> {
                    daysList.add(MonthsModel(nextMonth, "$day th", false))
                }
            }
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
            daysList.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
//                    Log.e("DAY TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
//                    Log.e("DAY FALSE", tempMonths[index].toString())
                }
            }
            daysList = tempMonths
            binding.dayRv.adapter = MonthsAdapter(daysList, this, "D")
            binding.dayRv.scrollToPosition(position)
        }
        if (listType == "T") { // Timings List
            timings.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
//                    Log.e("TIME TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
//                    Log.e("TIME FALSE", tempMonths[index].toString())
                }
            }
            timings = tempMonths
            binding.timeRv.adapter = MonthsAdapter(timings, this, "T")
            binding.timeRv.scrollToPosition(position)
        }

    }

}