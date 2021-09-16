package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ProviderMyBookingsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.ProviderBookingDetailsScreen
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.BookingDetail
import com.satrango.ui.user.bookings.cancel_booking.UserBookingCancelScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.utils.UserUtils

class ProviderMyBookingAdapter(
    private val list: List<com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.models.BookingDetail>,
    private val status: String,
    private val providerMyBookingInterface: ProviderMyBookingInterface
): RecyclerView.Adapter<ProviderMyBookingAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderMyBookingsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(
            data: BookingDetail,
            status: String,
            providerMyBookingInterface: ProviderMyBookingInterface
        ) {
            binding.amount.text = "Rs ${data.amount}"
            binding.bookingId.text = "Booking Id: ${data.booking_id}"
            binding.scheduleDate.text = data.scheduled_date
            if (!data.details[0].locality.isNullOrBlank()) {
                binding.myLocation.text = data.details[0].city
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
            when {
                status.equals("InProgress", ignoreCase = true) -> {
                    binding.startBtn.text = "Pause"
                    binding.reScheduleBtn.visibility = View.GONE
                    binding.cancelBookingBtn.text = "Mark Complete"

                    binding.startBtn.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        val intent = Intent(binding.root.context, ProviderBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                        UserUtils.spid = data.sp_id
                        ViewUserBookingDetailsScreen.FROM_PROVIDER = true
                        binding.root.context.startActivity(intent)
                    }

                    binding.card.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        val intent = Intent(binding.root.context, ProviderBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                        ViewUserBookingDetailsScreen.FROM_PROVIDER = true
                        UserUtils.spid = data.sp_id
                        binding.root.context.startActivity(intent)
                    }
                }
                status.equals("Pending", ignoreCase = true) -> {
                    binding.startBtn.text = "Start"
                    binding.reScheduleBtn.text = "Re-schedule"
                    binding.cancelBookingBtn.text = "Cancel Booking"

                    binding.cancelBookingBtn.setOnClickListener {
                        val intent = Intent(binding.root.context, UserBookingCancelScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                        UserBookingCancelScreen.FROM_PROVIDER = true
                        binding.root.context.startActivity(intent)
                    }

                    binding.reScheduleBtn.setOnClickListener {
                        ViewBidsScreen.bookingId = data.booking_id.toInt()
                        UserUtils.re_scheduled_date = data.scheduled_date
                        UserUtils.re_scheduled_time_slot_from = data.time_slot_id
                        ViewUserBookingDetailsScreen.RESCHEDULE = true
                        UserUtils.spid = data.sp_id
                        BookingDateAndTimeScreen.FROM_PROVIDER = true
                        binding.root.context.startActivity(Intent(binding.root.context, BookingDateAndTimeScreen::class.java))
                    }

                    binding.startBtn.setOnClickListener {
                        providerMyBookingInterface.requestOTP(data.booking_id.toInt())
                    }

                    binding.card.setOnClickListener {
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                        ViewUserBookingDetailsScreen.FROM_PROVIDER = true
                        UserUtils.spid = data.sp_id
                        ViewUserBookingDetailsScreen.FROM_PENDING = true
                        binding.root.context.startActivity(intent)
                    }
                }
                status.equals("Completed", ignoreCase = true) -> {
                    binding.startBtn.text = "Raise Ticket"
                    binding.reScheduleBtn.visibility = View.GONE
                    binding.cancelBookingBtn.visibility = View.GONE
                }
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ProviderMyBookingsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], status, providerMyBookingInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}