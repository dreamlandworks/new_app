package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ProviderMyBookingsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.ProviderBookingDetailsScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.invoice.ProviderInVoiceScreen
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.cancel_booking.UserBookingCancelScreen
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.BookingInterface
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.complaints.ComplaintScreen
import com.satrango.ui.user.user_dashboard.user_alerts.AlertsInterface
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isReschedule
import com.satrango.utils.toast

class ProviderMyBookingAdapter(
    private val list: List<BookingDetail>,
    private val status: String,
    private val providerMyBookingInterface: ProviderMyBookingInterface,
    private val alertsInterface: AlertsInterface,
    private val bookingInterface: BookingInterface
) : RecyclerView.Adapter<ProviderMyBookingAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderMyBookingsRowBinding, alertsInterface: AlertsInterface, bookingInterface: BookingInterface) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        val alertsInterface = alertsInterface
        val bookingInterface = bookingInterface

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n")
        fun bind(
            data: BookingDetail,
            status: String,
            providerMyBookingInterface: ProviderMyBookingInterface
        ) {
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
                Glide.with(binding.profilePic).load(data.profile_pic).error(R.drawable.images)
                    .into(binding.profilePic)
            }
            if (data.category_id == "2") {
                binding.locationLayout.visibility = View.GONE
            } else {
                binding.locationLayout.visibility = View.VISIBLE
            }
            binding.messageBtn.setOnClickListener {
                bookingInterface.startServiceProviderMessaging(data)
            }

            binding.phoneBtn.setOnClickListener {
                UserUtils.makePhoneCall(
                    binding.phoneBtn.context,
                    data.mobile.replace(" ", "").takeLast(10)
                )
            }
            when {
                status.equals("InProgress", ignoreCase = true) -> {
                    binding.timeRemaining.text = "Started"
                    binding.navigateBtn.visibility = View.GONE
                    if (data.pause_status.equals("Yes", true)) {
                        binding.startBtn.text = "Resume"
                        binding.cancelBookingBtn.isEnabled = false
                        binding.cancelBookingBtn.setBackgroundResource(R.drawable.gray_corner)
                        binding.cancelBookingBtn.setTextColor(binding.cancelBookingBtn.context.getColor(R.color.gray))
                    } else {
                        binding.startBtn.text = "Pause"
                    }

                    binding.reScheduleBtn.visibility = View.GONE
                    binding.cancelBookingBtn.text = "Mark Complete"
                    binding.cancelBookingBtn.setOnClickListener {
                        if (data.extra_demand_total_amount.isNotEmpty()) {
                            UserUtils.isExtraDemandRaised(binding.phoneBtn.context, "1")
                            providerMyBookingInterface.markComplete(data.material_advance, data.booking_id.toInt(), data.category_id, data.users_id)
                        } else {
                            UserUtils.isExtraDemandRaised(binding.phoneBtn.context, "0")
                            openExtraDemandNotRaisedDialog(binding.cancelBookingBtn.context, data, providerMyBookingInterface)
                        }
                    }

                    binding.startBtn.setOnClickListener {
                        if (data.pause_status.equals("Yes", true)) {
//                            binding.startBtn.text = "Pause"
                            providerMyBookingInterface.resumeBooking(data.booking_id.toInt())
                        } else {
//                            binding.startBtn.text = "Resume"
                            providerMyBookingInterface.pauseBooking(data.booking_id.toInt())
                        }
                    }

                    binding.card.setOnClickListener {
                        UserUtils.saveBookingPauseResumeStatus(binding.bookingId.context, data.pause_status)
                        ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                        ViewUserBookingDetailsScreen.FCM_TOKEN = data.user_fcm_token
                        val intent = Intent(binding.root.context, ProviderBookingDetailsScreen::class.java)
                        ProviderBookingDetailsScreen.bookingId = data.booking_id
                        ProviderBookingDetailsScreen.categoryId = data.category_id
                        ProviderBookingDetailsScreen.userId = data.users_id
                        UserUtils.saveSpId(binding.root.context, data.sp_id)
                        binding.root.context.startActivity(intent)
                    }
                }
                status.equals("Pending", ignoreCase = true) -> {
                    if (data.category_id == "1") {
                        binding.navigateBtn.setOnClickListener {
                            val context = binding.navigateBtn.context
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${data.details[0].latitude},${data.details[0].longitude}&mode=l"))
                            intent.setPackage("com.google.android.apps.maps")
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                        }
                        binding.navigateBtn.visibility = View.VISIBLE
                    } else {
                        binding.navigateBtn.visibility = View.GONE
                    }
                    if (data.reschedule_status == "10") {
                        binding.startBtn.text = "Reschedule Request Raised"
                        binding.reScheduleBtn.visibility = View.GONE
                        binding.cancelBookingBtn.visibility = View.GONE
                        if (data.req_raised_by != UserUtils.getUserId(binding.amount.context)) {
                            binding.startBtn.setOnClickListener {
                                alertsInterface.rescheduleSPAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.users_id.toInt(), data.reschedule_id.toInt(), data.reschedule_description)
                            }
                        }
                    } else {
                        binding.startBtn.text = "Start"
                        binding.reScheduleBtn.text = "Re-schedule"
                        binding.cancelBookingBtn.text = "Cancel Booking"
                        binding.timeRemaining.text = "${data.remaining_days_to_start}d, ${data.remaining_hours_to_start}h, ${data.remaining_minutes_to_start}m to start"
                        binding.cancelBookingBtn.setOnClickListener {
                            val intent = Intent(binding.root.context, UserBookingCancelScreen::class.java)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                            binding.root.context.startActivity(intent)
                        }
                        binding.reScheduleBtn.setOnClickListener {
                            ViewBidsScreen.bookingId = data.booking_id.toInt()
                            UserUtils.re_scheduled_date = data.scheduled_date
                            UserUtils.re_scheduled_time_slot_from = data.time_slot_id
                            isReschedule(binding.amount.context, true)
                            UserUtils.saveSpId(binding.root.context, data.sp_id)
                            UserUtils.saveSelectedSPDetails(binding.amount.context, Gson().toJson(data))
                            binding.root.context.startActivity(Intent(binding.root.context, BookingDateAndTimeScreen::class.java))
                        }
                        binding.startBtn.setOnClickListener {
                            providerMyBookingInterface.requestOTP(data.booking_id.toInt(), data.category_id, data.users_id, data.sp_id, data.user_fcm_token, data.sp_fcm_token)
                        }
                        binding.card.setOnClickListener {
                            val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen:: class.java)
                            UserUtils.saveSpId(binding.root.context, data.sp_id)
                            intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                            intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                            intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                            ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                            binding.root.context.startActivity(intent)
                        }
                    }
                }
                status.equals("Completed", ignoreCase = true) -> {
                    binding.navigateBtn.visibility = View.GONE
                    binding.timeRemaining.text = "Completed"
                    binding.startBtn.text = "Raise Ticket"
                    binding.reScheduleBtn.visibility = View.GONE
                    binding.cancelBookingBtn.visibility = View.GONE
                    binding.startBtn.setOnClickListener {
                        ComplaintScreen.bookingId = data.booking_id.toInt()
                        binding.startBtn.context.startActivity(Intent(binding.startBtn.context, ComplaintScreen::class.java))
                    }
                    binding.card.setOnClickListener {
                        val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                        intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                        intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                        intent.putExtra(binding.root.context.getString(R.string.user_id), data.users_id)
                        binding.root.context.startActivity(intent)
                    }
                }
            }

        }

        private fun openExtraDemandNotRaisedDialog(
            context: Context,
            data: BookingDetail,
            providerMyBookingInterface: ProviderMyBookingInterface
        ) {
            AlertDialog.Builder(context)
                .setMessage("Extra Demand Not Raised, Do you want to Continue?")
                .setPositiveButton("YES") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    providerMyBookingInterface.markComplete(data.extra_demand_total_amount, data.booking_id.toInt(), data.category_id, data.users_id)
                }.setNegativeButton("NO") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.show()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ProviderMyBookingsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            alertsInterface,
            bookingInterface
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[holder.layoutPosition], status, providerMyBookingInterface)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}