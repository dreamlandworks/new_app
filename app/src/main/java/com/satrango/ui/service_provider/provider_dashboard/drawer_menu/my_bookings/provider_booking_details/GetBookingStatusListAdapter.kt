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
            if (ViewUserBookingDetailsScreen.FROM_PROVIDER) {
                binding.image.setImageResource(R.drawable.mark_purple)
                binding.view.setBackgroundColor(binding.view.resources.getColor(R.color.purple_500))
            } else {
                binding.image.setImageResource(R.drawable.mark_blue)
                binding.view.setBackgroundColor(binding.view.resources.getColor(R.color.blue))
            }
            when (details.status_id) {
                "13" -> {
                    binding.description.text = "Booking Started and OTP Verified"
                }
                "15" -> {
                    binding.description.text = "Booking Paused"
                }
                "16" -> {
                    binding.description.text = "Booking Resume"
                }
                "22" -> {
                    binding.description.text = "Mark As Completed"
                }
                "23" -> {
                    binding.description.text = "OTP Verified and Booking Completed"
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
            }
//            binding.description.text = details.name
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
