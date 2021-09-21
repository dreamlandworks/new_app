package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.BookingStatusRowBinding
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.models.BookingStatusDetail
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen

class GetBookingStatusListAdapter(private val list: List<BookingStatusDetail>) : RecyclerView.Adapter<GetBookingStatusListAdapter.ViewHolder>() {

    class ViewHolder(binding: BookingStatusRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
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
            binding.description.text = details.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BookingStatusRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
