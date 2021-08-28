package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.satrango.R
import com.satrango.databinding.ActivityPostJobDateTimeScreenBinding
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.booking_date_time.MonthsAdapter
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.PostJobDescriptionScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.snackBar
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList

class PostJobDateTimeScreen : AppCompatActivity(), MonthsInterface {

    private lateinit var timeSlots: java.util.ArrayList<MonthsModel>
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var calendar: Calendar
    private lateinit var binding: ActivityPostJobDateTimeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobDateTimeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)

        binding.apply {

            nextBtn.setOnClickListener {

                UserUtils.scheduled_date = ""
                for (day in daysList) {
                    if (day.isSelected) {
                        UserUtils.scheduled_date = day.month
                    }
                }
                UserUtils.time_slot_from = ""
                UserUtils.time_slot_to = ""
                for (time in timeSlots) {
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
                    startActivity(Intent(this@PostJobDateTimeScreen, PostJobDescriptionScreen::class.java))
                }
            }

        }

        calendar = Calendar.getInstance()
        loadDates()
        loadTimings()
    }

    private fun loadDates() {
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        binding.dayRv.layoutManager = LinearLayoutManager(this@PostJobDateTimeScreen, LinearLayoutManager.HORIZONTAL, false)
        binding.dayRv.adapter = MonthsAdapter(daysList, this@PostJobDateTimeScreen, "D")
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
            }
        }
        daysInMonth = getDaysInMonth(year, month + 1)
        for (day in 1..daysInMonth) {
            daysList.add(MonthsModel(calendar.get(Calendar.YEAR).toString() + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day), day.toString(), false))
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
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                }
            }
            daysList = tempMonths
            binding.dayRv.adapter = MonthsAdapter(daysList, this, "D")
            binding.dayRv.scrollToPosition(position)
        }
        if (listType == "T") { // Timings List
            timeSlots.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                }
            }
            timeSlots = tempMonths
            binding.timeRv.adapter = MonthsAdapter(timeSlots, this, "T")
            binding.timeRv.scrollToPosition(position)
        }
    }

    private fun loadTimings() {
        timeSlots = ArrayList()
        val timingsList = resources.getStringArray(R.array.bookingTimings)
        for (index in timingsList.indices) {
            timeSlots.add(MonthsModel(timingsList[index], "", false))
        }
        binding.timeRv.layoutManager = LinearLayoutManager(this@PostJobDateTimeScreen, LinearLayoutManager.HORIZONTAL, false)
        binding.timeRv.adapter = MonthsAdapter(timeSlots, this@PostJobDateTimeScreen, "T")
    }
}