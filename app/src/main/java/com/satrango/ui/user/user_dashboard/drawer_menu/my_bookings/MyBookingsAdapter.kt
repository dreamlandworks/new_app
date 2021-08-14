package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.MyBookingsRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail

class MyBookingsAdapter(private val list: List<BookingDetail>): RecyclerView.Adapter<MyBookingsAdapter.ViewHolder>() {

    class ViewHolder(binding: MyBookingsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: BookingDetail) {
            binding.amount.text = "Rs ${data.amount}"
            binding.bookingId.text = "Booking Id: ${data.booking_id}"
            binding.scheduleDate.text = data.scheduled_date
//            binding.myLocation.text = data.
//            binding.jobDescription.text = data.
            binding.spName.text = "${data.fname}, ${data.lname}"
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