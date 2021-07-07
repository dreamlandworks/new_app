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
import de.hdodenhof.circleimageview.CircleImageView
import java.time.YearMonth
import java.util.*

class BookLater : AppCompatActivity(), MonthsInterface {

    private lateinit var calendar: Calendar
    private lateinit var timings: ArrayList<MonthsModel>
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var monthsList: ArrayList<MonthsModel>
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

        loadMonths()
        calendar = Calendar.getInstance()
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
        loadTimings()

        binding.am.setOnClickListener {
            binding.am.setBackgroundResource(R.drawable.category_bg)
            binding.am.setTextColor(resources.getColor(R.color.white))
            binding.pm.setBackgroundResource(R.drawable.blue_out_line)
            binding.pm.setTextColor(resources.getColor(R.color.black))
            binding.timeRv.scrollToPosition(0)
        }
        binding.pm.setOnClickListener {
            binding.pm.setBackgroundResource(R.drawable.category_bg)
            binding.pm.setTextColor(resources.getColor(R.color.white))
            binding.am.setBackgroundResource(R.drawable.blue_out_line)
            binding.am.setTextColor(resources.getColor(R.color.black))
            binding.timeRv.scrollToPosition(14)
        }
        binding.nextBtn.setOnClickListener {
            startActivity(Intent(this, BookingNow::class.java))
        }
    }

    private fun loadTimings() {
        timings = arrayListOf()
        for (time in resources.getStringArray(R.array.bookingTimings)) {
            timings.add(MonthsModel(time, false))
        }
        binding.timeRv.layoutManager = LinearLayoutManager(this@BookLater, LinearLayoutManager.HORIZONTAL, false)
        binding.timeRv.adapter = MonthsAdapter(timings, this@BookLater, "T")

    }

    private fun loadDays(year: Int, month: Int) {
        val yearMonthObject: YearMonth = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            YearMonth.of(year, month)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val daysInMonth = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject.lengthOfMonth()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        daysList = arrayListOf()
        for (day in 1..daysInMonth) {
            when (day) {
                1 -> {
                    daysList.add(MonthsModel("$day st", false))
                }
                2 -> {
                    daysList.add(MonthsModel("$day nd", false))
                }
                3 -> {
                    daysList.add(MonthsModel("$day rd", false))
                }
                else -> {
                    daysList.add(MonthsModel("$day th", false))
                }
            }
        }
        binding.dayRv.layoutManager = LinearLayoutManager(this@BookLater, LinearLayoutManager.HORIZONTAL, false)
        binding.dayRv.adapter = MonthsAdapter(daysList, this@BookLater, "DD")

    }

    private fun loadMonths() {
        monthsList = arrayListOf()
        for (month in resources.getStringArray(R.array.months)){
            monthsList.add(MonthsModel(month, false))
        }
        binding.monthsRv.layoutManager = LinearLayoutManager(this@BookLater, LinearLayoutManager.HORIZONTAL, false)
        binding.monthsRv.adapter = MonthsAdapter(monthsList, this@BookLater, "MM")
        binding.dayRv.scrollToPosition(0)

    }

    override fun selectedMonth(position: Int, listType: String) {
        val tempMonths = arrayListOf<MonthsModel>()

        if (listType == "MM") {
            monthsList.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, true))
                    Log.e("MONTHS TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, false))
                    Log.e("MONTHS FALSE", tempMonths[index].toString())
                }
            }
            binding.monthsRv.adapter = MonthsAdapter(tempMonths, this, "MM")
            binding.monthsRv.scrollToPosition(position)
            loadDays(calendar.get(Calendar.YEAR), position + 1)
        }
        if (listType == "DD") {
            daysList.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, true))
                    Log.e("MONTHS TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, false))
                    Log.e("MONTHS FALSE", tempMonths[index].toString())
                }
            }
            binding.dayRv.adapter = MonthsAdapter(tempMonths, this, "DD")
            binding.dayRv.scrollToPosition(position)
        }
        if (listType == "T") {
            timings.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, true))
                    Log.e("MONTHS TRUE", tempMonths[index].toString())
                } else {
                    tempMonths.add(MonthsModel(month.month, false))
                    Log.e("MONTHS FALSE", tempMonths[index].toString())
                }
            }
            binding.timeRv.adapter = MonthsAdapter(tempMonths, this, "T")
            binding.timeRv.scrollToPosition(position)
        }

    }

}