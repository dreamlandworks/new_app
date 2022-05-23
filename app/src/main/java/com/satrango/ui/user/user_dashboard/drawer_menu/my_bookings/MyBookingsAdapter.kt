package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.MyBookingsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.cancel_booking.UserBookingCancelScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.view_booking_details.UserMyBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.ComplaintScreen
import com.satrango.ui.user.user_dashboard.user_alerts.AlertsInterface
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isCompleted
import com.satrango.utils.UserUtils.isPending
import com.satrango.utils.UserUtils.isProvider
import com.satrango.utils.UserUtils.isReschedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MyBookingsAdapter(
    private val list: List<BookingDetail>,
    private val alertsInterface: AlertsInterface,
    private val bookingInterface: BookingInterface
): RecyclerView.Adapter<MyBookingsAdapter.ViewHolder>() {

    class ViewHolder(
        binding: MyBookingsRowBinding,
        alertsInterface: AlertsInterface,
        bookingInterface: BookingInterface
    ): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        val alertsInterface = alertsInterface
        val bookingInterface = bookingInterface
        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(data: BookingDetail) {
            binding.amount.text = "Rs ${data.amount}"
            binding.bookingId.text = "Booking Id: ${data.booking_id}"
            binding.scheduleDate.text = "${data.scheduled_date} ${data.from}"
            when (data.category_id) {
                "1" -> {
                    binding.bookingType.text = "Single Move"
                }
                "2" -> {
                    binding.bookingType.text = "Blue Collar"
                }
                "3" -> {
                    binding.bookingType.text = "Multi Move"
                }
            }
            if (data.details[0].locality.isNullOrBlank()) {
                if (data.details[0].locality.isNullOrBlank() && data.details[0].city.isNullOrBlank()) {
                    binding.myLocationText.visibility = View.GONE
                    binding.myLocation.visibility = View.GONE
                } else {
                    binding.myLocation.text = data.details[0].city
                }
            } else {
                binding.myLocation.text = data.details[0].locality + ", " + data.details[0].city
            }
            binding.jobDescription.text = data.details[0].job_description
            if (!data.fname.isNullOrBlank() && !data.lname.isNullOrBlank()) {
                binding.spName.text = "${data.sp_fname} ${data.sp_lname}"
            } else {
                binding.spName.text = "Unknown"
            }
            if (!data.sp_profile_pic.isNullOrBlank()) {
                Glide.with(binding.profilePic).load(data.sp_profile_pic).into(binding.profilePic)
            }

            when(data.booking_status.lowercase(Locale.getDefault())) {
                "InProgress".lowercase(Locale.getDefault()) -> {
                    binding.timeRemaining.text = "Started"
                    binding.startBtn.visibility = View.GONE
                    CoroutineScope(Dispatchers.Main).launch {
                        val response = RetrofitBuilder.getUserRetrofitInstance().getBookingStatusList(RetrofitBuilder.USER_KEY, data.booking_id.toInt())
                        if (response.status == 200) {
                            if (response.booking_status_details.isNotEmpty()) {
                                val details = response.booking_status_details[response.booking_status_details.size - 1]
                                binding.startBtn.text = details.description
                                binding.startBtn.visibility = View.VISIBLE
                            } else {
                                binding.startBtn.text = "Service Provider started to your location"
                                binding.startBtn.visibility = View.VISIBLE
                            }
                        }
                    }
                    binding.startBtn.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        isProvider(binding.startBtn.context, false)
                        isPending(binding.startBtn.context, false)
                        UserMyBookingDetailsScreen.bookingId = data.booking_id
                        UserMyBookingDetailsScreen.categoryId = data.category_id
                        UserMyBookingDetailsScreen.userId = UserUtils.getUserId(binding.root.context)
                        binding.root.context.startActivity(Intent(binding.root.context, UserMyBookingDetailsScreen::class.java))
                    }
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.reScheduleBtn.visibility = View.GONE

                    binding.card.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        ViewUserBookingDetailsScreen.FCM_TOKEN = data.sp_fcm_token
                        UserMyBookingDetailsScreen.bookingId = data.booking_id
                        UserMyBookingDetailsScreen.categoryId = data.category_id
                        UserMyBookingDetailsScreen.userId = UserUtils.getUserId(binding.root.context)
                        isProvider(binding.startBtn.context, false)
                        isPending(binding.startBtn.context, false)
                        binding.root.context.startActivity(Intent(binding.root.context, UserMyBookingDetailsScreen::class.java))
                    }

                }
                "Pending".lowercase(Locale.getDefault()) -> {
                    binding.reScheduleBtn.setTextColor(binding.reScheduleBtn.resources.getColor(R.color.white))
                    if (data.reschedule_status == "10") {
                        binding.startBtn.text = "Reschedule Request Raised"
                        binding.startBtn.visibility = View.VISIBLE
                        binding.cancelBookingBtn.visibility = View.GONE
                        binding.reScheduleBtn.visibility = View.GONE
                        if (data.req_raised_by != UserUtils.getUserId(binding.amount.context)) {
                            binding.startBtn.setOnClickListener {
                                alertsInterface.rescheduleUserAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), 0, data.reschedule_id.toInt(), data.reschedule_description)
                            }
                        }
                    } else {
                        binding.startBtn.visibility = View.GONE
                        binding.timeRemaining.text = "${data.remaining_days_to_start}d, ${data.remaining_hours_to_start}h, ${data.remaining_minutes_to_start}m to start"
                        binding.reScheduleBtn.background = binding.reScheduleBtn.resources.getDrawable(R.drawable.user_btn_bg)
                        binding.cancelBookingBtn.setOnClickListener {
                            val intent = Intent(binding.root.context, UserBookingCancelScreen::class.java)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                            isProvider(binding.startBtn.context, false)
                            binding.root.context.startActivity(intent)
                        }
                        binding.reScheduleBtn.setOnClickListener {
                            ViewBidsScreen.bookingId = data.booking_id.toInt()
                            UserUtils.re_scheduled_date = data.scheduled_date
                            UserUtils.re_scheduled_time_slot_from = data.time_slot_id
                            isReschedule(binding.amount.context, true)
                            UserUtils.spid = data.sp_id
                            isProvider(binding.startBtn.context, false)
                            binding.root.context.startActivity(Intent(binding.root.context, BookingDateAndTimeScreen::class.java))
                        }
                    }
                    binding.card.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                        isProvider(binding.startBtn.context, false)
                        isPending(binding.startBtn.context, true)
                        binding.root.context.startActivity(intent)
                    }
                }
                "Completed".lowercase(Locale.getDefault()) -> {
                    binding.timeRemaining.text = "Completed"
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.reScheduleBtn.text = "Raise Support Ticket"
                    binding.startBtn.text = "Book Again"
                    binding.reScheduleBtn.setOnClickListener {
                        binding.startBtn.setOnClickListener {
                            ComplaintScreen.bookingId = data.booking_id.toInt()
                            isProvider(binding.startBtn.context, false)
                            binding.startBtn.context.startActivity(Intent(binding.startBtn.context, ComplaintScreen::class.java))
                        }
                    }
                    binding.card.setOnClickListener {
                        val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                        isCompleted(binding.amount.context, true)
                        isProvider(binding.startBtn.context, false)
                        isPending(binding.startBtn.context, false)
                        binding.root.context.startActivity(intent)
                    }
                }
                "Expired".lowercase(Locale.getDefault()) -> {
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.reScheduleBtn.visibility = View.GONE
                    binding.startBtn.text = "Expired"
                    binding.startBtn.setBackgroundResource(R.drawable.user_red_btn_bg)
                    binding.startBtn.elevation = 0f
                }
                "Cancelled".lowercase(Locale.getDefault()) -> {
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.reScheduleBtn.visibility = View.GONE
                    binding.startBtn.text = "Cancelled"
                    binding.startBtn.setBackgroundResource(R.drawable.user_red_btn_bg)
                    binding.startBtn.elevation = 0f
                }
            }
            binding.messageBtn.setOnClickListener {
                bookingInterface.startMessaging(data)
            }

        }



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(MyBookingsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false), alertsInterface, bookingInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}