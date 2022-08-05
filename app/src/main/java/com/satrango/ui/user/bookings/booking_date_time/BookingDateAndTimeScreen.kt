package com.satrango.ui.user.bookings.booking_date_time

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.basusingh.beautifulprogressdialog.BeautifulProgressDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.satrango.R
import com.satrango.base.ViewModelFactory
import com.satrango.databinding.ActivityBookingDateAndTimeScreenBinding
import com.satrango.remote.NetworkResponse
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.dashboard.ProviderDashboard
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.ProviderMyBookingsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.bookings.booking_address.BookingAddressScreen
import com.satrango.ui.user.bookings.booking_address.BookingRepository
import com.satrango.ui.user.bookings.booking_address.BookingViewModel
import com.satrango.ui.user.bookings.booking_attachments.BookingAttachmentsScreen
import com.satrango.ui.user.bookings.view_booking_details.models.RescheduleBookingReqModel
import com.satrango.ui.user.user_dashboard.UserDashboardScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.UserMyBookingsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.UserSearchViewProfileScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.models.*
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.UserUtils.isReschedule
import com.satrango.utils.snackBar
import com.satrango.utils.toast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList
import kotlin.math.round

class BookingDateAndTimeScreen : AppCompatActivity(), MonthsInterface {

    private lateinit var rightNow: Calendar
    private lateinit var morningTimings: java.util.ArrayList<MonthsModel>
    private lateinit var afternoonTimings: java.util.ArrayList<MonthsModel>
    private lateinit var eveningTimings: java.util.ArrayList<MonthsModel>
    private lateinit var nightTimings: java.util.ArrayList<MonthsModel>
    private lateinit var progressDialog: BeautifulProgressDialog
    private lateinit var blocked_time_slots: List<BlockedTimeSlot>
    private lateinit var preferred_time_slots: List<PreferredTimeSlot>
    private lateinit var slots_data: SlotsData
    private lateinit var availableTimeSlots: java.util.ArrayList<MonthsModel>
    private lateinit var availableSlots: java.util.ArrayList<MonthsModel>
//    private lateinit var spDetails: SearchServiceProviderResModel
    private lateinit var calendar: Calendar
    private lateinit var daysList: ArrayList<MonthsModel>
    private lateinit var binding: ActivityBookingDateAndTimeScreenBinding
    private lateinit var data: Data
    private lateinit var rescheduleData: BookingDetail
    private var userType = ""
    private var today = true

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDateAndTimeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolBar()
        initializeProgressDialog()
        rightNow = Calendar.getInstance()

        userType = if (isProvider(this)) {
            "SP"
        } else {
            "User"
        }

        if (UserUtils.getSelectedSPDetails(this).isNotEmpty()) {
            if (isReschedule(this) && isProvider(this)) {
                rescheduleData = Gson().fromJson(UserUtils.getSelectedSPDetails(this), BookingDetail::class.java)
                updateUI(rescheduleData)
            } else {
                data = Gson().fromJson(UserUtils.getSelectedSPDetails(this), Data::class.java)
                updateUI(data)
            }
        }

        calendar = Calendar.getInstance()
        binding.selectedMonth.text = LocalDate.now().month.name

//        if (!isReschedule(this)) {
//            spDetails = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), SearchServiceProviderResModel::class.java)
//            loadDates()
//            loadTimings(0)
//        } else {
//
//        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitBuilder.getUserRetrofitInstance().getSpSlots(RetrofitBuilder.USER_KEY, UserUtils.getSpId(this@BookingDateAndTimeScreen).toInt())
                val jsonObject = JSONObject(JSONObject(response.string()).getJSONObject("slots_data").toString())
                jsonObject.put("user_id", UserUtils.getUserId(this@BookingDateAndTimeScreen))
                slots_data = Gson().fromJson(jsonObject.toString(), SlotsData::class.java)
                preferred_time_slots = slots_data.preferred_time_slots
                blocked_time_slots = slots_data.blocked_time_slots
                loadDates()
                loadTimings(0)
            } catch (e: Exception) {
                snackBar(binding.morningTimeRv, e.message!!)
            }
        }

//        val factory = ViewModelFactory(BookingRepository())
//        val viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]
//        viewModel.spSlots(this, UserUtils.getSpId(this).toInt()).observe(this) {
//            when (it) {
//                is NetworkResponse.Loading -> {
//                    progressDialog.show()
//                }
//                is NetworkResponse.Success -> {
//                    progressDialog.dismiss()
//                    slots_data = it.data!!
//                    preferred_time_slots = slots_data.preferred_time_slots
//                    blocked_time_slots = slots_data.blocked_time_slots
//                    loadDates()
//                    loadTimings(0)
//                }
//                is NetworkResponse.Failure -> {
//                    progressDialog.dismiss()
//                    snackBar(binding.nextBtn, it.message!!)
//                }
//            }
//        }

        binding.nextBtn.setOnClickListener {
            validateFields()
        }

    }

    private fun initializeToolBar() {
        val toolBar = binding.root.findViewById<View>(R.id.toolBar)
        toolBar.findViewById<ImageView>(R.id.toolBarBackBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarBackTVBtn).setOnClickListener { onBackPressed() }
        toolBar.findViewById<TextView>(R.id.toolBarTitle).text = resources.getString(R.string.booking)
        val profilePic = toolBar.findViewById<CircleImageView>(R.id.toolBarImage)
        Glide.with(profilePic).load(UserUtils.getUserProfilePic(this)).into(profilePic)
        if (isProvider(this)) {
            toolBar.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.card.setCardBackgroundColor(resources.getColor(R.color.purple_500))
            binding.nextBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            binding.borderLayout.setBackgroundResource(R.drawable.purple_out_line)
            binding.bookingLayout.setBackgroundResource(R.drawable.purple_circle)
            binding.paymentLayout.setBackgroundResource(R.drawable.purple_circle_color)
            binding.confirmationLayout.setBackgroundResource(R.drawable.purple_circle_color)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = data.profession
        binding.costPerHour.text = "Rs. ${round(data.final_amount.toDouble()).toInt()}/-"
        Glide.with(this).load(data.profile_pic).into(binding.profilePic)
//        toast(this, "Updated UI ONE")
    }

    @SuppressLint("SetTextI18n", "ObsoleteSdkInt")
    private fun updateUI(data: BookingDetail) {
        binding.userName.text = "${data.fname} ${data.lname}"
        binding.occupation.text = resources.getString(R.string.user)
        binding.costPerHour.text = "Rs. ${round(data.amount.toDouble()).toInt()}/-"
        Glide.with(this).load(data.profile_pic).into(binding.profilePic)
        binding.rating.setBackgroundResource(R.drawable.purple_circle_color)
        binding.reviews.setBackgroundResource(R.drawable.purple_circle_color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }
//        toast(this, "Updated UI ONE")
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
            return
        } else if (UserUtils.time_slot_from.isEmpty() || UserUtils.time_slot_to.isEmpty()) {
            return
        } else {
            if (isReschedule(this)) {
                rescheduleBooking()
            } else {
                if (data.category_id == "3") {
                    UserUtils.addressList = ArrayList()
                    val intent = Intent(this@BookingDateAndTimeScreen, BookingAddressScreen::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@BookingDateAndTimeScreen, BookingAttachmentsScreen::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeProgressDialog() {
        progressDialog = BeautifulProgressDialog(
            this,
            BeautifulProgressDialog.withGIF,
            resources.getString(R.string.loading)
        )
        progressDialog.setGifLocation(Uri.parse("android.resource://${packageName}/${R.drawable.blue_loading}"))
        progressDialog.setLayoutColor(resources.getColor(R.color.progressDialogColor))
    }

    private fun rescheduleBooking() {

        val requestBody = RescheduleBookingReqModel(
            ViewBidsScreen.bookingId,
            RetrofitBuilder.USER_KEY,
            UserUtils.scheduled_date,
            UserUtils.time_slot_from,
            UserUtils.re_scheduled_date,
            UserUtils.re_scheduled_time_slot_from.toInt(),
            UserUtils.getUserId(this).toInt(),
            userType
        )
//        Log.e("RESCHEDULE:", Gson().toJson(requestBody))
//        toast(this, "RESCHEDULE:" + Gson().toJson(requestBody))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val responseBody = RetrofitBuilder.getUserRetrofitInstance().rescheduleBooking(requestBody)
                if (responseBody.status == 200) {
                    progressDialog.dismiss()
                    showRescheduledDialog()
                } else {
                    progressDialog.dismiss()
                    toast(this@BookingDateAndTimeScreen, responseBody.message)
                }
            } catch (e: Exception) {
                toast(this@BookingDateAndTimeScreen, e.message!!)
            }
        }
    }

    private fun showRescheduledDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.reschedule_requested_dialog, null)
        val title = dialogView.findViewById<TextView>(R.id.title)
        val homeBtn = dialogView.findViewById<TextView>(R.id.homeBtn)
        val myBookingsBtn = dialogView.findViewById<TextView>(R.id.myBookingsBtn)
        val shield = dialogView.findViewById<ImageView>(R.id.shield)
        if (isProvider(this)) {
            shield.setImageResource(R.drawable.purple_shield)
            homeBtn.setBackgroundResource(R.drawable.purple_out_line)
            myBookingsBtn.setBackgroundResource(R.drawable.provider_btn_bg)
            homeBtn.setTextColor(resources.getColor(R.color.purple_500))
            title.setTextColor(resources.getColor(R.color.purple_500))

            homeBtn.setOnClickListener {
                UserUtils.saveFromFCMService(this, false)
                startActivity(Intent(this, ProviderDashboard::class.java))
            }
            myBookingsBtn.setOnClickListener {
                UserUtils.saveFromFCMService(this, false)
                startActivity(Intent(this, ProviderMyBookingsScreen::class.java))
            }
        } else {
            homeBtn.setOnClickListener {
                startActivity(Intent(this, UserDashboardScreen::class.java))
            }
            myBookingsBtn.setOnClickListener {
                startActivity(Intent(this, UserMyBookingsScreen::class.java))
            }
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadDates() {
        loadDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        availableSlots = ArrayList()

//        if (isReschedule(this)) {
        for (day in daysList) {
            for (slot in preferred_time_slots) {
                val inFormat = SimpleDateFormat("dd-MM-yyyy")
                val d = day.month.split("-")
                val date = inFormat.parse("${d[2]}-${d[1]}-${d[0]}")
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
//        } else {
//            for (day in daysList) {
//                for (slots in spDetails.slots_data) {
//                    if (slots.user_id == data.users_id) {
//                        for (slot in slots.preferred_time_slots) {
//                            val inFormat = SimpleDateFormat("dd-MM-yyyy")
//                            val d = day.month.split("-")
//                            val date = inFormat.parse("${d[2]}-${d[1]}-${d[0]}")
//                            val outFormat = SimpleDateFormat("EEEE")
//                            val goal: String = outFormat.format(date!!)
//                            val weekDays = resources.getStringArray(R.array.days)
//                            for (index in weekDays.indices) {
//                                if (weekDays[index] == goal) {
//                                    if (slot.day_slot == (index + 1).toString()) {
//                                        if (!availableSlots.contains(day)) {
//                                            availableSlots.add(day)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        binding.dayRv.layoutManager = LinearLayoutManager(
            this@BookingDateAndTimeScreen,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        availableSlots = availableSlots.distinctBy { monthsModel -> monthsModel.day } as java.util.ArrayList<MonthsModel>
        binding.dayRv.adapter = MonthsAdapter(availableSlots, this@BookingDateAndTimeScreen, "D")
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTimings(position: Int) {
        availableTimeSlots = ArrayList()
        val bookedSlots = ArrayList<MonthsModel>()
        val actualTimeSlots = ArrayList<MonthsModel>()

//        if (isReschedule(this)) {
        for (time in blocked_time_slots) {
            if (time.date == availableSlots[position].month) {
                bookedSlots.add(MonthsModel(time.time_slot_from, "", false))
            }
        }
//        } else {
//            for (slot in spDetails.slots_data) {
//                if (slot.user_id == data.users_id) {
//                    for (time in slot.blocked_time_slots) {
//                        if (time.date == availableSlots[position].month) {
//                            bookedSlots.add(MonthsModel(time.time_slot_from, "", false))
//                        }
//                    }
//                }
//            }
//        }
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

//        Log.e("SLOTS:", Gson().toJson(availableTimeSlots))

        binding.morningTimeRv.layoutManager = GridLayoutManager(this, 2)
        binding.afternoonTimeRv.layoutManager = GridLayoutManager(this, 2)
        binding.eveningTimeRv.layoutManager = GridLayoutManager(this, 2)
        binding.nightTimeRv.layoutManager = GridLayoutManager(this, 2)
        morningTimings = ArrayList()
        afternoonTimings = ArrayList()
        eveningTimings = ArrayList()
        nightTimings = ArrayList()
        availableTimeSlots.onEachIndexed { index, monthsModel ->
//            Log.e("SLOTS:", Gson().toJson(monthsModel))
            when {
                UserUtils.isNowTimeBetween("07:00", "12:00", monthsModel.month) -> {
                    if (index >= 0) {
                        if (!morningTimings.contains(availableTimeSlots[index - 1])) {
                            if (today) {
                                if (UserUtils.checktimings(
                                        monthsModel.month,
                                        SimpleDateFormat("HH:mm a").format(Date())
                                    )
                                ) {
                                    morningTimings.add(availableTimeSlots[index - 1])
                                    Log.e("MORNING ONE:",
                                        Gson().toJson(availableTimeSlots[index - 1])
                                    )
                                }
                            } else {
                                morningTimings.add(availableTimeSlots[index - 1])
                                Log.e("MORNING TWO:", Gson().toJson(availableTimeSlots[index - 1]))
                            }
                        }
                    }
                    if (today) {
                        if (UserUtils.checktimings(
                                monthsModel.month,
                                SimpleDateFormat("HH:mm a").format(Date())
                            )
                        ) {
                            morningTimings.add(monthsModel)
                            Log.e("MORNING THREE:", Gson().toJson(monthsModel))
                        }
                    } else {
                        morningTimings.add(monthsModel)
//                        Log.e("MORNING:", "${availableTimeSlots[index - 1]}")
                        Log.e("MORNING FOUR:", Gson().toJson(monthsModel))
                    }
                }
                UserUtils.isNowTimeBetween("12:00", "16:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!afternoonTimings.contains(availableTimeSlots[index - 1])) {
                            if (today) {
                                if (UserUtils.checktimings(
                                        monthsModel.month,
                                        SimpleDateFormat("HH:mm a").format(Date())
                                    )
                                ) {
                                    afternoonTimings.add(availableTimeSlots[index - 1])
                                    Log.e("AFTERNOON ONE:", Gson().toJson(availableTimeSlots[index - 1]))
                                }
                            } else {
                                afternoonTimings.add(availableTimeSlots[index - 1])
                                Log.e("AFTERNOON TWO:", Gson().toJson(availableTimeSlots[index - 1]))
                            }
                        }
                    }
                    if (today) {
                        if (UserUtils.checktimings(
                                monthsModel.month,
                                SimpleDateFormat("HH:mm a").format(Date())
                            )
                        ) {
                            afternoonTimings.add(monthsModel)
                            Log.e("AFTERNOON THREE:", Gson().toJson(monthsModel))
                        }
                    } else {
                        afternoonTimings.add(monthsModel)
                        Log.e("AFTERNOON FOUR:", Gson().toJson(monthsModel))
                    }
                }
                UserUtils.isNowTimeBetween("16:00", "21:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!eveningTimings.contains(availableTimeSlots[index - 1])) {
                            if (today) {
                                if (UserUtils.checktimings(
                                        monthsModel.month,
                                        SimpleDateFormat("HH:mm a").format(Date())
                                    )
                                ) {
                                    eveningTimings.add(availableTimeSlots[index - 1])
                                    Log.e("EVENING ONE:", Gson().toJson(availableTimeSlots[index - 1]))
                                }
                            } else {
                                eveningTimings.add(availableTimeSlots[index - 1])
                                Log.e("EVENING TWO:", Gson().toJson(availableTimeSlots[index - 1]))
                            }
                        }
                    }
                    if (today) {
                        if (UserUtils.checktimings(
                                monthsModel.month,
                                SimpleDateFormat("HH:mm a").format(Date())
                            )
                        ) {
                            eveningTimings.add(monthsModel)
                            Log.e("EVENING THREE:", Gson().toJson(monthsModel))
                        }
                    } else {
                        eveningTimings.add(monthsModel)
                        Log.e("EVENING FOUR:", Gson().toJson(monthsModel))
                    }
                }
                UserUtils.isNowTimeBetween("21:00", "07:00", monthsModel.month) -> {
                    if (index >= 1) {
                        if (!nightTimings.contains(availableTimeSlots[index - 1])) {
                            if (today) {
                                if (UserUtils.checktimings(
                                        monthsModel.month,
                                        SimpleDateFormat("HH:mm a").format(Date())
                                    )
                                ) {
                                    nightTimings.add(availableTimeSlots[index - 1])
//                                    Log.e("NIGHT ONE:", Gson().toJson(availableTimeSlots[index - 1]))
                                }
                            } else {
                                nightTimings.add(availableTimeSlots[index - 1])
//                                Log.e("NIGHT TWO:", Gson().toJson(availableTimeSlots[index - 1]))
                            }
                        }
                    }
                    if (today) {
                        if (UserUtils.checktimings(
                                monthsModel.month,
                                SimpleDateFormat("HH:mm a").format(Date())
                            )
                        ) {
                            nightTimings.add(monthsModel)
//                            Log.e("NIGHT THREE:", Gson().toJson(monthsModel))
                        }
                    } else {
                        nightTimings.add(monthsModel)
//                        Log.e("NIGHT FOUR:", Gson().toJson(monthsModel))
                    }
                }
            }
        }
        morningTimings = filterTimeSlots(morningTimings)
        if (morningTimings.isEmpty()) {
            binding.morningText.visibility = View.GONE
            binding.morningTimeRv.visibility = View.GONE
        } else {
            if (today) {
                morningTimings.removeAt(0)
            }
            if (morningTimings.isEmpty()) {
                binding.morningTimeRv.visibility = View.GONE
                binding.morningText.visibility = View.GONE
            } else {
                binding.morningText.visibility = View.VISIBLE
                binding.morningTimeRv.visibility = View.VISIBLE
                binding.morningTimeRv.adapter = MonthsAdapter(morningTimings, this@BookingDateAndTimeScreen, "T")
            }
        }
//        Log.e("AFTERNOON", Gson().toJson(afternoonTimings))
        afternoonTimings = filterTimeSlots(afternoonTimings)
        if (afternoonTimings.isEmpty()) {
            binding.afternoonText.visibility = View.GONE
            binding.afternoonTimeRv.visibility = View.GONE
        } else {
            if (today) {
                if (binding.morningTimeRv.visibility != View.VISIBLE) {
                    afternoonTimings.removeAt(0)
                }
            }
            if (afternoonTimings.isEmpty()) {
                binding.afternoonText.visibility = View.GONE
                binding.afternoonTimeRv.visibility = View.GONE
            } else {
                binding.afternoonText.visibility = View.VISIBLE
                binding.afternoonTimeRv.visibility = View.VISIBLE
                binding.afternoonTimeRv.adapter = MonthsAdapter(afternoonTimings, this@BookingDateAndTimeScreen, "T")
            }
        }
        eveningTimings = filterTimeSlots(eveningTimings)
        if (eveningTimings.isEmpty()) {
            binding.eveningText.visibility = View.GONE
            binding.eveningTimeRv.visibility = View.GONE
        } else {
            if (today) {
                if (binding.morningTimeRv.visibility != View.VISIBLE && binding.afternoonTimeRv.visibility != View.VISIBLE) {
                    eveningTimings.removeAt(0)
                }
            }
            if (eveningTimings.isEmpty()) {
                binding.eveningText.visibility = View.GONE
                binding.eveningTimeRv.visibility = View.GONE
            } else {
                binding.eveningText.visibility = View.VISIBLE
                binding.eveningTimeRv.visibility = View.VISIBLE
                binding.eveningTimeRv.adapter = MonthsAdapter(eveningTimings, this@BookingDateAndTimeScreen, "T")
            }
        }
        nightTimings = filterTimeSlots(nightTimings)
        if (nightTimings.isEmpty()) {
            binding.nightText.visibility = View.GONE
            binding.nightTimeRv.visibility = View.GONE
        } else {
            if (binding.morningTimeRv.visibility != View.VISIBLE && binding.afternoonTimeRv.visibility != View.VISIBLE && binding.eveningTimeRv.visibility != View.VISIBLE) {
                nightTimings.removeAt(0)
            }
            if (nightTimings.isEmpty()) {
                binding.nightText.visibility = View.GONE
                binding.nightTimeRv.visibility = View.GONE
            } else {
                binding.nightText.visibility = View.VISIBLE
                binding.nightTimeRv.visibility = View.VISIBLE
                binding.nightTimeRv.adapter = MonthsAdapter(nightTimings, this@BookingDateAndTimeScreen, "T")
            }
        }
//        Log.e("MORNING:", Gson().toJson(morningTimings))
//        Log.e("AFTERNOON:", Gson().toJson(afternoonTimings))
//        Log.e("EVENING:", Gson().toJson(eveningTimings))
//        Log.e("NIGHT:", Gson().toJson(nightTimings))
//        toast(this, "Timings Loaded: ${morningTimings.size} ${afternoonTimings.size} ${eveningTimings.size} ${nightTimings.size} ${blocked_time_slots.size} ${bookedSlots.size} ${availableSlots.size} ${availableTimeSlots.size}")
//        Log.e("AVAILABLE SLOTS:", Gson().toJson(availableSlots))
//        Log.e("AVAILABLE TIME SLOTS:", Gson().toJson(availableTimeSlots))
    }

    private fun filterTimeSlots(timings: java.util.ArrayList<MonthsModel>): java.util.ArrayList<MonthsModel> {
//        val timeSlotsData = Gson().fromJson(UserUtils.getSelectedAllSPDetails(this), SearchServiceProviderResModel::class.java)
//        Log.e("RESULTS:", Gson().toJson(timeSlotsData.slots_data))
        val tempAvailableTimeSlots = ArrayList<MonthsModel>()
//        val userId = if (isProvider(this)) {
//            rescheduleData.sp_id
//        } else {
//            data.sp_id
//        }
//        for (time in timeSlotsData.slots_data) {
//            if (time.user_id == userId) {
//                for (slot in timings) {
//                    for (sl in time.preferred_time_slots) {
//                        if (slot.month.split("\nto\n")[0] == sl.time_slot_from) {
//                            tempAvailableTimeSlots.add(slot)
//                            Log.e("SORTED:", Gson().toJson(slot))
//                        } else {
//                            Log.e("NOT SORTED:", Gson().toJson(slot))
//                        }
//                    }
//                }
//            }
//            else {
//                Log.e("USER ", "NOT FOUND: ${timeSlotsData.slots_data.size}: ${time.user_id}|${userId}")
//            }
//        }
        for (slot in timings) {
            for (sl in preferred_time_slots) {
                if (slot.month.split("\nto\n")[0] == sl.time_slot_from) {
                    var isExisted = false
                    for (time in blocked_time_slots) {
                        if (time.time_slot_from == slot.month.split("\nto\n")[0]) {
                            isExisted = !isExisted
                        }
                    }
                    if (!isExisted) {
                        tempAvailableTimeSlots.add(slot)
                    }
                    Log.e("SORTED:", Gson().toJson(slot))
                } else {
                    Log.e("NOT SORTED:", Gson().toJson(slot))
                }
            }
        }
        return tempAvailableTimeSlots.distinctBy { monthsModel -> monthsModel.month } as java.util.ArrayList<MonthsModel>
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
        if (month > 12) {
            daysInMonth = getDaysInMonth(year + 1, month + 1 - 12)
        } else {
            daysInMonth = getDaysInMonth(year, month + 1)
        }
        for (day in 1..daysInMonth) {
            val months = if (month + 1 > 12) {
                month + 1 - 12
            } else {
                month + 1
            }
            daysList.add(
                MonthsModel(
                    calendar.get(Calendar.YEAR).toString() + "-" + String.format(
                        "%02d",
                        months
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
                var years = 0
                var months = 0
                if (month > 12) {
                    years = year + 1
                    months = month - 12
                } else {
                    years = year
                    months = month
                }
                YearMonth.of(years, months)
            } else {
                return 30
            }
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject.lengthOfMonth()
        } else {
            30
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun selectedMonth(position: Int, dateTime: String, listType: String) {
        var tempMonths = arrayListOf<MonthsModel>()
        if (listType == "D") { // Days List
            today = position == 0
            availableSlots.onEachIndexed { index, month ->
                if (index == position) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")
                    val date: LocalDate = LocalDate.parse(month.month, formatter)
                    val dow: Month? = date.month
                    val output: String = dow!!.getDisplayName(TextStyle.FULL, Locale.US)
                    binding.selectedMonth.text = output
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                }
            }
            availableSlots = tempMonths
            binding.dayRv.adapter = MonthsAdapter(availableSlots, this, "D")
            binding.dayRv.scrollToPosition(position)
            loadTimings(position)
        } else {
            tempMonths = ArrayList()
            availableTimeSlots.onEachIndexed { index, month ->
                if (dateTime == month.month) {
                    tempMonths.add(MonthsModel(month.month, month.day, true))
                } else {
                    tempMonths.add(MonthsModel(month.month, month.day, false))
                }
            }

            availableTimeSlots = tempMonths

            morningTimings = ArrayList()
            afternoonTimings = ArrayList()
            eveningTimings = ArrayList()
            nightTimings = ArrayList()

            availableTimeSlots.onEachIndexed { index, monthsModel ->
                when {
                    UserUtils.isNowTimeBetween("07:00", "12:00", monthsModel.month) -> {
                        if (index >= 1) {
                            if (!morningTimings.contains(availableTimeSlots[index - 1])) {
                                if (today) {
                                    if (UserUtils.checktimings(
                                            monthsModel.month,
                                            SimpleDateFormat("HH:mm a").format(Date())
                                        )
                                    ) {
                                        morningTimings.add(availableTimeSlots[index - 1])
                                    }
                                } else {
                                    morningTimings.add(availableTimeSlots[index - 1])
                                }
                            }
                        }
                        if (today) {
                            if (UserUtils.checktimings(
                                    monthsModel.month,
                                    SimpleDateFormat("HH:mm a").format(Date())
                                )
                            ) {
                                morningTimings.add(monthsModel)
                            }
                        } else {
                            morningTimings.add(monthsModel)
                        }
                    }
                    UserUtils.isNowTimeBetween("12:00", "16:00", monthsModel.month) -> {
                        if (index >= 1) {
                            if (!afternoonTimings.contains(availableTimeSlots[index - 1])) {
                                if (today) {
                                    if (UserUtils.checktimings(
                                            monthsModel.month,
                                            SimpleDateFormat("HH:mm a").format(Date())
                                        )
                                    ) {
                                        afternoonTimings.add(availableTimeSlots[index - 1])
                                    }
                                } else {
                                    afternoonTimings.add(availableTimeSlots[index - 1])
                                }
                            }
                        }
                        if (today) {
                            if (UserUtils.checktimings(
                                    monthsModel.month,
                                    SimpleDateFormat("HH:mm a").format(Date())
                                )
                            ) {
                                afternoonTimings.add(monthsModel)
                            }
                        } else {
                            afternoonTimings.add(monthsModel)
                        }
                    }
                    UserUtils.isNowTimeBetween("16:00", "21:00", monthsModel.month) -> {
                        if (index >= 1) {
                            if (!eveningTimings.contains(availableTimeSlots[index - 1])) {
                                if (today) {
                                    if (UserUtils.checktimings(
                                            monthsModel.month,
                                            SimpleDateFormat("HH:mm a").format(Date())
                                        )
                                    ) {
                                        eveningTimings.add(availableTimeSlots[index - 1])
                                    }
                                } else {
                                    eveningTimings.add(availableTimeSlots[index - 1])
                                }
                            }
                        }
                        if (today) {
                            if (UserUtils.checktimings(
                                    monthsModel.month,
                                    SimpleDateFormat("HH:mm a").format(Date())
                                )
                            ) {
                                eveningTimings.add(monthsModel)
                            }
                        } else {
                            eveningTimings.add(monthsModel)
                        }

                    }
                    UserUtils.isNowTimeBetween("21:00", "07:00", monthsModel.month) -> {
                        if (index >= 1) {
                            if (!nightTimings.contains(availableTimeSlots[index - 1])) {
                                if (today) {
                                    if (UserUtils.checktimings(
                                            monthsModel.month,
                                            SimpleDateFormat("HH:mm a").format(Date())
                                        )
                                    ) {
                                        nightTimings.add(availableTimeSlots[index - 1])
                                    }
                                } else {
                                    nightTimings.add(availableTimeSlots[index - 1])
                                }
                            }
                        }
                        if (today) {
                            if (UserUtils.checktimings(
                                    monthsModel.month,
                                    SimpleDateFormat("HH:mm a").format(Date())
                                )
                            ) {
                                nightTimings.add(monthsModel)
                            }
                        } else {
                            nightTimings.add(monthsModel)
                        }

                    }
                }
            }
            morningTimings = filterTimeSlots(morningTimings)
            if (morningTimings.isEmpty()) {
                binding.morningText.visibility = View.GONE
                binding.morningTimeRv.visibility = View.GONE
            } else {
                if (today) {
                    morningTimings.removeAt(0)
                }
                binding.morningText.visibility = View.VISIBLE
                binding.morningTimeRv.visibility = View.VISIBLE
                binding.morningTimeRv.adapter = MonthsAdapter(morningTimings, this@BookingDateAndTimeScreen, "T")
            }
            afternoonTimings = filterTimeSlots(afternoonTimings)
            if (afternoonTimings.isEmpty()) {
                binding.afternoonText.visibility = View.GONE
                binding.afternoonTimeRv.visibility = View.GONE
            } else {
                if (today) {
                    if (binding.morningTimeRv.visibility != View.VISIBLE) {
                        afternoonTimings.removeAt(0)
                    }
                }
                binding.afternoonText.visibility = View.VISIBLE
                binding.afternoonTimeRv.visibility = View.VISIBLE
                binding.afternoonTimeRv.adapter = MonthsAdapter(afternoonTimings, this@BookingDateAndTimeScreen, "T")
            }
            eveningTimings = filterTimeSlots(eveningTimings)
            if (eveningTimings.isEmpty()) {
                binding.eveningText.visibility = View.GONE
                binding.eveningTimeRv.visibility = View.GONE
            } else {
                if (today) {
                    if (binding.morningTimeRv.visibility != View.VISIBLE && binding.afternoonTimeRv.visibility != View.VISIBLE) {
                        eveningTimings.removeAt(0)
                    }
                }
                binding.eveningText.visibility = View.VISIBLE
                binding.eveningTimeRv.visibility = View.VISIBLE
                binding.eveningTimeRv.adapter = MonthsAdapter(eveningTimings, this@BookingDateAndTimeScreen, "T")
            }
            nightTimings = filterTimeSlots(nightTimings)
            if (nightTimings.isEmpty()) {
                binding.nightText.visibility = View.GONE
                binding.nightTimeRv.visibility = View.GONE
            } else {
                if (binding.morningTimeRv.visibility != View.VISIBLE && binding.afternoonTimeRv.visibility != View.VISIBLE && binding.eveningTimeRv.visibility != View.VISIBLE) {
                    nightTimings.removeAt(0)
                }
                binding.nightText.visibility = View.VISIBLE
                binding.nightTimeRv.visibility = View.VISIBLE
                binding.nightTimeRv.adapter = MonthsAdapter(nightTimings, this@BookingDateAndTimeScreen, "T")
            }
        }
        validateFields()
    }

    override fun onBackPressed() {
        finish()
        if (isReschedule(this)) {
            super.onBackPressed()
        } else {
            val intent = Intent(this, UserSearchViewProfileScreen::class.java)
            startActivity(intent)
        }

    }

}