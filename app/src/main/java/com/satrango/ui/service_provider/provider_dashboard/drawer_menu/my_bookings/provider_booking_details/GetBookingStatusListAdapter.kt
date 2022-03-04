package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.BookingStatusRowBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.models.BookingStatusDetail
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.utils.UserUtils.isProvider

class GetBookingStatusListAdapter(private val list: List<BookingStatusDetail>) :
    RecyclerView.Adapter<GetBookingStatusListAdapter.ViewHolder>() {

    class ViewHolder(binding: BookingStatusRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(details: BookingStatusDetail, position: Int) {

            if (position == 0) {
                binding.view.visibility = View.GONE
            } else {
                binding.view.visibility = View.VISIBLE
            }
            if (isProvider(binding.description.context)) {
                binding.image.setImageResource(R.drawable.mark_purple)
                binding.view.setBackgroundColor(binding.view.resources.getColor(R.color.purple_500))
            } else {
                binding.image.setImageResource(R.drawable.mark_blue)
                binding.view.setBackgroundColor(binding.view.resources.getColor(R.color.blue))
            }
            when (details.status_id) {
                "4" -> {
                    binding.description.text = "Service Provider Rejected"
                }
                "5" -> {
                    binding.description.text = "Service Provider Accepted"
                }
                "6" -> {
                    binding.description.text = "Service Provider Not Responded"
                }
                "7" -> {
                    binding.description.text = "Payment Pending"
                }
                "8" -> {
                    binding.description.text = "Payment Done"
                }
                "9" -> {
                    binding.description.text = "Pending"
                }
                "10" -> {
                    binding.description.text = "Reschedule Requested"
                }
                "11" -> {
                    binding.description.text = "Reschedule Rejected"
                }
                "12" -> {
                    binding.description.text = "Reschedule Approved"
                }
                "13" -> {
                    binding.description.text = "Booking Started and OTP Verified"
                }
                "14" -> {
                    binding.description.text = "Booking Started"
                }
                "15" -> {
                    binding.description.text = "Booking Paused"
                }
                "16" -> {
                    binding.description.text = "Booking Resumed"
                }
                "17" -> {
                    binding.description.text = "Sent for Review"
                }
                "18" -> {
                    binding.description.text = "Reviewed and not Accepted"
                }
                "19" -> {
                    binding.description.text = "Reviewed and found appropriate"
                }
                "20" -> {
                    binding.description.text = "Part PAyment requested"
                }
                "21" -> {
                    binding.description.text = "Part Payment Released"
                }
                "22" -> {
                    binding.description.text = "Marked as Completed"
                }
                "23" -> {
                    binding.description.text = "OTP Verified and Booking Completed"
                }
                "24" -> {
                    binding.description.text = "Cancelled by User"
                }
                "25" -> {
                    binding.description.text = "Cancelled by Service Provider"
                }
                "26" -> {
                    binding.description.text = "Open"
                }
                "27" -> {
                    binding.description.text = "Awarded"
                }
                "28" -> {
                    binding.description.text = "Expired"
                }
                "29" -> {
                    binding.description.text = "Rejected"
                }
                "37" -> {
                    binding.description.text = "Extra Demand Raised"
                }
                "38" -> {
                    binding.description.text = "Extra Demand Accepted"
                }
                "39" -> {
                    binding.description.text = "Extra Demand Rejected"
                }
                "40" -> {
                    binding.description.text = "Service Provider Placed Bid"
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BookingStatusRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}
