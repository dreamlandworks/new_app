package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityPostJobDateTimeScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.booking_date_time.MonthsAdapter
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.PostJobDescriptionScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.post_job_multi_move.PostJobMultiMoveDescriptionScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.checktimings
import com.satrango.utils.loadProfileImage
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList

class PostJobDateTimeScreen : AppCompatActivity(), MonthsInterface {

    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var timeSlots: java.util.ArrayList<MonthsModel>
    private lateinit var morningTimings: java.util.ArrayList<MonthsModel>
    private lateinit var afternoonTimings: java.util.ArrayList<MonthsModel>
    private lateinit var eveningTimings: java.util.ArrayList<MonthsModel>
    private lateinit var nightTimings: java.util.ArrayList<MonthsModel>
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var calendar: Calendar
    private lateinit var binding: ActivityPostJobDateTimeScreenBinding
    private var today = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostJobDateTimeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()

        binding.apply {

            nextBtn.setOnClickListener {
                validateFields()
            }

        }

        calendar = Calendar.getInstance()
        binding.selectedMonth.text = LocalDate.now().month.name
        loadDates()
        loadTimings()

        if (UserUtils.EDIT_MY_JOB_POST) {
            loadData()
        }

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.post_a_job)
        val image = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        loadProfileImage(image)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withImage,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.white))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {

        val factory = ViewModelFactory(PostJobRepository())
        val viewModel = ViewModelProvider(this, factory)[PostJobViewModel::class.java]
        val requestBody = MyJobPostViewReqModel(
            ViewBidsScreen.bookingId,
            ViewBidsScreen.categoryId,
            RetrofitBuilder.USER_KEY,
            ViewBidsScreen.postJobId,
            UserUtils.getUserId(this).toInt(),
        )
        viewModel.myJobPostsViewDetails(this, requestBody).observe(this, {
            when (it) {
                is NetworkResponse.Loading -> {
                    progressDialog.show()
                }
                is NetworkResponse.Success -> {
                    progressDialog.dismiss()
                    updateUI(it.data!!)
                }
                is NetworkResponse.Failure -> {
                    progressDialog.dismiss()
                    snackBar(binding.dayRv, it.message!!)
                }

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(data: MyJobPostViewResModel) {
        UserUtils.EDIT_MY_JOB_POST_DETAILS = Gson().toJson(data)
        Log.e("POSTJOB:", UserUtils.EDIT_MY_JOB_POST_DETAILS)
        for (index in daysList.indices) {
            if (daysList[index].month == data.job_post_details.scheduled_date) {
                daysList[index] = MonthsModel(daysList[index].month, daysList[index].day, true)
                binding.dayRv.adapter!!.notifyItemChanged(index)
                binding.dayRv.scrollToPosition(index)
            }
        }
        binding.dayRv.adapter = MonthsAdapter(daysList, this, "D")
        for (index in timeSlots.indices) {
            if (timeSlots[index].month.split("\n")[0] == data.job_post_details.from) {
                timeSlots[index] = MonthsModel(timeSlots[index].month, timeSlots[index].day, true)
                morningTimings = ArrayList()
                afternoonTimings = ArrayList()
                eveningTimings = ArrayList()
                nightTimings = ArrayList()
            }
        }
//                when {
//                    UserUtils.isNowTimeBetween("07:00", "12:00", timeSlots[index].month) -> {
////                        for (morning in morningTimings.indices) {
//////                            Log.e("MORNING:", Gson().toJson(morning) + "|" + morningTimings[morning].month.split("\n")[0])
//////                            if (morningTimings[morning].month.split("\n")[0] == data.job_post_details.from) {
//////                                morningTimings[morning] = MonthsModel(timeSlots[index].month, timeSlots[index].day, true)
//////                            }
////                        }
//                        binding.morningTimeRv.adapter = MonthsAdapter(morningTimings, this, "T")
//                        binding.morningTimeRv.scrollToPosition(index)
//                    }
//                    UserUtils.isNowTimeBetween("12:00", "16:00", timeSlots[index].month) -> {
//                        for (morning in afternoonTimings.indices) {
//                            if (afternoonTimings[morning].month.split("\n")[0] == data.job_post_details.from) {
//                                afternoonTimings[morning] = MonthsModel(timeSlots[index].month, timeSlots[index].day, true)
//                            }
//                        }
//                        binding.afternoonTimeRv.adapter = MonthsAdapter(afternoonTimings, this, "T")
//                        binding.afternoonTimeRv.scrollToPosition(index)
//                    }
//                    UserUtils.isNowTimeBetween("16:00", "21:00", timeSlots[index].month) -> {
//                        for (morning in eveningTimings.indices) {
//                            if (eveningTimings[morning].month.split("\n")[0] == data.job_post_details.from) {
//                                eveningTimings[morning] = MonthsModel(timeSlots[index].month, timeSlots[index].day, true)
//                            }
//                        }
//                        binding.eveningTimeRv.adapter = MonthsAdapter(eveningTimings, this, "T")
//                        binding.eveningTimeRv.scrollToPosition(index)
//                    }
//                    UserUtils.isNowTimeBetween("21:00", "07:00", timeSlots[index].month) -> {
//                        for (morning in nightTimings.indices) {
//                            if (nightTimings[morning].month.split("\n")[0] == data.job_post_details.from) {
//                                nightTimings[morning] = MonthsModel(timeSlots[index].month, timeSlots[index].day, true)
//                            }
//                        }
//                        binding.nightTimeRv.adapter = MonthsAdapter(nightTimings, this, "T")
//                        binding.nightTimeRv.scrollToPosition(index)
//                    }
//                }
//            }
//        }
        timeSlots.forEachIndexed { index, monthsModel ->
            when {
                UserUtils.isNowTimeBetween("07:00", "12:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!morningTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                morningTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                        morningTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("12:00", "16:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!afternoonTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                afternoonTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                        afternoonTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("16:00", "21:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!eveningTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                eveningTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                        eveningTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("21:00", "07:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!nightTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                nightTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(
                            monthsModel.month,
                            SimpleDateFormat("HH:mm a").format(Date())
                        )
                    ) {
                        nightTimings.add(monthsModel)
                    }
                }
            }
        }
        binding.morningTimeRv.adapter = MonthsAdapter(morningTimings, this, "T")
        binding.afternoonTimeRv.adapter = MonthsAdapter(afternoonTimings, this, "T")
        binding.eveningTimeRv.adapter = MonthsAdapter(eveningTimings, this, "T")
        binding.nightTimeRv.adapter = MonthsAdapter(nightTimings, this, "T")
    }

    private fun validateFields() {
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
            return
//            snackBar(binding.nextBtn, "Please Select Date")
        } else if (UserUtils.time_slot_from.isEmpty() || UserUtils.time_slot_to.isEmpty()) {
            return
//            snackBar(binding.nextBtn, "Please Select TimeSlot")
        } else {
            if (UserUtils.getFromJobPostMultiMove(this@PostJobDateTimeScreen)) {
                startActivity(
                    Intent(
                        this@PostJobDateTimeScreen,
                        PostJobMultiMoveDescriptionScreen::class.java
                    )
                )
            } else {
                startActivity(
                    Intent(
                        this@PostJobDateTimeScreen,
                        PostJobDescriptionScreen::class.java
                    )
                )
            }
        }
    }

    private fun loadDates() {
//        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
        binding.dayRv.layoutManager =
            LinearLayoutManager(this@PostJobDateTimeScreen, LinearLayoutManager.HORIZONTAL, false)
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
                daysList.add(
                    MonthsModel(
                        calendar.get(Calendar.YEAR).toString() + "-" + String.format(
                            "%02d",
                            month
                        ) + "-" + String.format(
                            "%02d",
                            day
                        ), day.toString(), false
                    )
                )
            }
        }
        daysInMonth = getDaysInMonth(year, month + 1)
        for (day in 1..daysInMonth) {
            daysList.add(
                MonthsModel(
                    calendar.get(Calendar.YEAR).toString() + "-" + String.format(
                        "%02d",
                        month + 1
                    ) + "-" + String.format(
                        "%02d",
                        day
                    ), day.toString(), false
                )
            )
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun selectedMonth(position: Int, dateTime: String, listType: String) {
        var tempMonths = arrayListOf<MonthsModel>()
        if (listType == "D") { // Days List
            today = position == 0
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
//        if (listType == "T") { // Timings List
        tempMonths = ArrayList()
        timeSlots.onEachIndexed { index, month ->
            if (month.month == dateTime) {
                tempMonths.add(MonthsModel(month.month, month.day, true))
            } else {
                tempMonths.add(MonthsModel(month.month, month.day, false))
            }
        }
        timeSlots = tempMonths
        morningTimings = ArrayList()
        afternoonTimings = ArrayList()
        eveningTimings = ArrayList()
        nightTimings = ArrayList()
        timeSlots.forEachIndexed { index, monthsModel ->
            when {
                UserUtils.isNowTimeBetween("07:00", "12:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!morningTimings.contains(timeSlots[index - 1])) {
                            if (today) {
                                if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                    morningTimings.add(timeSlots[index - 1])
                                }
                            } else {
                                morningTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (today) {
                        if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                            morningTimings.add(monthsModel)
                        }
                    } else {
                        morningTimings.add(monthsModel)
                    }

                }
                UserUtils.isNowTimeBetween("12:00", "16:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!afternoonTimings.contains(timeSlots[index - 1])) {
                            if (today) {
                                if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                    afternoonTimings.add(timeSlots[index - 1])
                                }
                            } else {
                                afternoonTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (today) {
                        if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                            afternoonTimings.add(monthsModel)
                        }
                    } else {
                        afternoonTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("16:00", "21:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!eveningTimings.contains(timeSlots[index - 1])) {
                            if (position == 0) {
                                if (checktimings(
                                        monthsModel.month,
                                        SimpleDateFormat("HH:mm a").format(Date())
                                    )
                                ) {
                                    eveningTimings.add(timeSlots[index - 1])
                                }
                            } else {
                                eveningTimings.add(timeSlots[index - 1])
                            }

                        }
                    }
                    if (today) {
                        if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                            eveningTimings.add(monthsModel)
                        }
                    } else {
                        eveningTimings.add(monthsModel)
                    }

                }
                UserUtils.isNowTimeBetween("21:00", "07:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!nightTimings.contains(timeSlots[index - 1])) {
                            if (today) {
                                if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                    nightTimings.add(timeSlots[index - 1])
                                }
                            } else {
                                nightTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (today) {
                        if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                            nightTimings.add(monthsModel)
                        }
                    } else {
                        nightTimings.add(monthsModel)
                    }
                }
            }
        }
        if (morningTimings.isEmpty()) {
            binding.morningText.visibility = View.GONE
            binding.morningTimeRv.visibility = View.GONE
        } else {
            binding.morningText.visibility = View.VISIBLE
            binding.morningTimeRv.visibility = View.VISIBLE
            binding.morningTimeRv.adapter =
                MonthsAdapter(morningTimings, this@PostJobDateTimeScreen, "T")
        }
        if (afternoonTimings.isEmpty()) {
            binding.afternoonText.visibility = View.GONE
            binding.afternoonTimeRv.visibility = View.GONE
        } else {
            binding.afternoonText.visibility = View.VISIBLE
            binding.afternoonTimeRv.visibility = View.VISIBLE
            binding.afternoonTimeRv.adapter =
                MonthsAdapter(afternoonTimings, this@PostJobDateTimeScreen, "T")
        }
        if (eveningTimings.isEmpty()) {
            binding.eveningText.visibility = View.GONE
            binding.eveningTimeRv.visibility = View.GONE
        } else {
            binding.eveningText.visibility = View.VISIBLE
            binding.eveningTimeRv.visibility = View.VISIBLE
            binding.eveningTimeRv.adapter =
                MonthsAdapter(eveningTimings, this@PostJobDateTimeScreen, "T")
        }
        if (nightTimings.isEmpty()) {
            binding.nightText.visibility = View.GONE
            binding.nightTimeRv.visibility = View.GONE
        } else {
            binding.nightText.visibility = View.VISIBLE
            binding.nightTimeRv.visibility = View.VISIBLE
            binding.nightTimeRv.adapter =
                MonthsAdapter(nightTimings, this@PostJobDateTimeScreen, "T")
        }
//        }
        validateFields()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTimings() {
        timeSlots = ArrayList()
        val timingsList = resources.getStringArray(R.array.bookingTimings)
        for (index in timingsList.indices) {
            timeSlots.add(MonthsModel(timingsList[index], "", false))
        }
        binding.morningTimeRv.layoutManager = GridLayoutManager(this, 2)
        binding.afternoonTimeRv.layoutManager = GridLayoutManager(this, 2)
        binding.eveningTimeRv.layoutManager = GridLayoutManager(this, 2)
        binding.nightTimeRv.layoutManager = GridLayoutManager(this, 2)
        morningTimings = ArrayList()
        afternoonTimings = ArrayList()
        eveningTimings = ArrayList()
        nightTimings = ArrayList()
        timeSlots.forEachIndexed { index, monthsModel ->
            when {
                UserUtils.isNowTimeBetween("07:00", "12:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!morningTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                morningTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                        morningTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("12:00", "16:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!afternoonTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                afternoonTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                        afternoonTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("16:00", "21:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!eveningTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                eveningTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                        eveningTimings.add(monthsModel)
                    }
                }
                UserUtils.isNowTimeBetween("21:00", "07:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!nightTimings.contains(timeSlots[index - 1])) {
                            if (checktimings(monthsModel.month, SimpleDateFormat("HH:mm a").format(Date()))) {
                                nightTimings.add(timeSlots[index - 1])
                            }
                        }
                    }
                    if (checktimings(
                            monthsModel.month,
                            SimpleDateFormat("HH:mm a").format(Date())
                        )
                    ) {
                        nightTimings.add(monthsModel)
                    }
                }
            }
        }
        if (morningTimings.isEmpty()) {
            binding.morningText.visibility = View.GONE
        } else {
            binding.morningText.visibility = View.VISIBLE
            binding.morningTimeRv.adapter = MonthsAdapter(morningTimings, this@PostJobDateTimeScreen, "T")
        }
        if (afternoonTimings.isEmpty()) {
            binding.afternoonText.visibility = View.GONE
        } else {
            binding.afternoonText.visibility = View.VISIBLE
            binding.afternoonTimeRv.adapter =
                MonthsAdapter(afternoonTimings, this@PostJobDateTimeScreen, "T")
        }
        if (eveningTimings.isEmpty()) {
            binding.eveningText.visibility = View.GONE
        } else {
            binding.eveningText.visibility = View.VISIBLE
            binding.eveningTimeRv.adapter =
                MonthsAdapter(eveningTimings, this@PostJobDateTimeScreen, "T")
        }
        if (nightTimings.isEmpty()) {
            binding.nightText.visibility = View.GONE
        } else {
            binding.nightText.visibility = View.VISIBLE
            binding.nightTimeRv.adapter =
                MonthsAdapter(nightTimings, this@PostJobDateTimeScreen, "T")
        }

    }
}