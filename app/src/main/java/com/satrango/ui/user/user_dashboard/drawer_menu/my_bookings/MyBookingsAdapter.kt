package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.MyBookingsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.cancel_booking.UserBookingCancelScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.view_booking_details.UserMyBookingDetailsScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.ComplaintScreen
import com.satrango.utils.UserUtils
import java.util.*

class MyBookingsAdapter(private val list: List<BookingDetail>): RecyclerView.Adapter<MyBookingsAdapter.ViewHolder>() {

    class ViewHolder(binding: MyBookingsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

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
                binding.spName.text = "${data.fname} ${data.lname}"
            } else {
                binding.spName.text = "Unknown"
            }
            if (!data.profile_pic.isNullOrBlank()) {
                Glide.with(binding.profilePic).load(RetrofitBuilder.BASE_URL + data.profile_pic).into(binding.profilePic)
            }

            when(data.booking_status.lowercase(Locale.getDefault())) {
                "InProgress".lowercase(Locale.getDefault()) -> {
                    binding.startBtn.text = "Service Provider started to your location"
                    binding.startBtn.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        ViewUserBookingDetailsScreen.FROM_PENDING = false
                        ViewUserBookingDetailsScreen.FROM_PROVIDER = false
                        val intent = Intent(binding.root.context, UserMyBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                        binding.root.context.startActivity(intent)
                    }
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.reScheduleBtn.visibility = View.GONE

                    binding.card.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        val intent = Intent(binding.root.context, UserMyBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                        ViewUserBookingDetailsScreen.FROM_PENDING = false
                        ViewUserBookingDetailsScreen.FROM_PROVIDER = false
                        binding.root.context.startActivity(intent)
                    }

                }
                "Pending".lowercase(Locale.getDefault()) -> {
                    binding.startBtn.visibility = View.GONE
                    binding.reScheduleBtn.background = binding.reScheduleBtn.resources.getDrawable(R.drawable.user_btn_bg)
                    binding.reScheduleBtn.setTextColor(binding.reScheduleBtn.resources.getColor(R.color.white))
                    binding.cancelBookingBtn.setOnClickListener {
                        val intent = Intent(binding.root.context, UserBookingCancelScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                        UserBookingCancelScreen.FROM_PROVIDER = false
                        binding.root.context.startActivity(intent)
                    }
                    binding.reScheduleBtn.setOnClickListener {
                        ViewBidsScreen.bookingId = data.booking_id.toInt()
                        UserUtils.re_scheduled_date = data.scheduled_date
                        UserUtils.re_scheduled_time_slot_from = data.time_slot_id
                        ViewUserBookingDetailsScreen.RESCHEDULE = true
                        UserUtils.spid = data.sp_id
                        BookingDateAndTimeScreen.FROM_PROVIDER = false
                        binding.root.context.startActivity(Intent(binding.root.context, BookingDateAndTimeScreen::class.java))
                    }
                    binding.card.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                        ViewUserBookingDetailsScreen.FROM_PENDING = true
                        ViewUserBookingDetailsScreen.FROM_PROVIDER = false
                        binding.root.context.startActivity(intent)
                    }

                }
                "Completed".lowercase(Locale.getDefault()) -> {
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.reScheduleBtn.text = "Raise Support Ticket"
                    binding.startBtn.text = "Book Again"
                    binding.reScheduleBtn.setOnClickListener {
                        binding.startBtn.setOnClickListener {
                            ComplaintScreen.bookingId = data.booking_id.toInt()
                            ComplaintScreen.FROM_PROVIDER = false
                            binding.startBtn.context.startActivity(Intent(binding.startBtn.context, ComplaintScreen::class.java))
                        }
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
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(MyBookingsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}