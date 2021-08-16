package com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.MyBookingsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail

class MyBookingsAdapter(private val list: List<BookingDetail>): RecyclerView.Adapter<MyBookingsAdapter.ViewHolder>() {

    class ViewHolder(binding: MyBookingsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: BookingDetail) {
            binding.amount.text = "Rs ${data.amount}"
            binding.bookingId.text = "Booking Id: ${data.booking_id}"
            binding.scheduleDate.text = data.scheduled_date
            if (!!data.details[0].locality.isNullOrBlank()) {
                binding.myLocation.text = data.details[0].city
            } else {
                binding.myLocation.text = data.details[0].locality + ", " + data.details[0].city
            }
            binding.jobDescription.text = data.details[0].job_description
            if (!!data.fname.isNullOrBlank() && !!data.lname.isNullOrBlank()) {
                binding.spName.text = "${data.fname} ${data.lname}"
            } else {
                binding.spName.text = "Unknown"
            }
            if (!data.profile_pic.isNullOrBlank()) {
                Glide.with(binding.profilePic).load(RetrofitBuilder.BASE_URL + data.profile_pic).into(binding.profilePic)
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