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
import com.satrango.databinding.ActivityBookLaterBinding
import com.satrango.ui.user.bookings.booknow.BookingNow
import com.satrango.utils.UserUtils
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.time.YearMonth
import java.util.*

class BookLater : AppCompatActivity(), MonthsInterface {


    private lateinit var calendar: Calendar
    private lateinit var timings: ArrayList<MonthsModel>
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookLaterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookLaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)

        calendar = Calendar.getInstance()
        loadDates()
        loadTimings()

        binding.nextBtn.setOnClickListener {
            startActivity(Intent(this, BookingNow::class.java))
        }
    }

    private fun loadDates() {
        val months = resources.getStringArray(R.array.months)
        val month = months[calendar.get(Calendar.MONTH)]
        val nextMonth = months[calendar.get(Calendar.MONTH) + 1]
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), month, nextMonth)
        binding.dayRv.layoutManager = LinearLayoutManager(this@BookLater, LinearLayoutManager.HORIZONTAL, false)
        binding.dayRv.adapter = MonthsAdapter(daysList, this@BookLater, "D")
    }

    private fun loadTimings() {
        timings = arrayListOf()
        val timingsList = resources.getStringArray(R.array.bookingTimings)
        for (index in timingsList.indices) {
            if (index < 12) {
                timings.add(MonthsModel(timingsList[index],"AM",  false))
            } else {
                timings.add(MonthsModel(timingsList[index],"PM",  false))
            }
        }
        binding.timeRv.layoutManager =
            LinearLayoutManager(this@BookLater, LinearLayoutManager.HORIZONTAL, false)
        binding.timeRv.adapter = MonthsAdapter(timings, this@BookLater, "T")

    }

    private fun loadDays(year: Int, month: Int, currentMonth: String, nextMonth: String): ArrayList<MonthsModel> {
        var daysInMonth = getDaysInMonth(year, month)
        daysList = arrayListOf()
        for (day in 1..daysInMonth) {
            if (day >= calendar.get(Calendar.DAY_OF_MONTH)) {
                when (day.toString().last()) {
                    1.toChar() -> {
                        daysList.add(MonthsModel(currentMonth, "$day st", false))
                    }
                    2.toChar() -> {
                        daysList.add(MonthsModel(currentMonth,"$day nd", false))
                    }
                    3.toChar() -> {
                        daysList.add(MonthsModel(currentMonth,"$day rd", false))
                    }
                    else -> {
                        daysList.add(MonthsModel(currentMonth,"$day th", false))
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
                    tempMonths.add(MonthsModel(month.month, month.day ,true))
                    Log.e("MONTHS TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                    Log.e("MONTHS FALSE", tempMonths[index].toString())
                }
            }
            binding.dayRv.adapter = MonthsAdapter(tempMonths, this, "DD")
            binding.dayRv.scrollToPosition(position)
        }
        if (listType == "T") { // Timings List
            timings.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
                    Log.e("MONTHS TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                    Log.e("MONTHS FALSE", tempMonths[index].toString())
                }
            }
            binding.timeRv.adapter = MonthsAdapter(tempMonths, this, "T")
            binding.timeRv.scrollToPosition(position)
        }

    }

}