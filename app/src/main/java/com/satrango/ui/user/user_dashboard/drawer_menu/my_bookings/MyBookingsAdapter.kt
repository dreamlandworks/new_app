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
import com.satrango.ui.user.bookings.view_booking_details.ViewUserBookingDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_bookings.models.BookingDetail
import com.satrango.utils.UserUtils

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
            binding.card.setOnClickListener {
                ViewUserBookingDetailsScreen.FROM_MY_BOOKINGS_SCREEN = true
                val intent = Intent(binding.root.context, ViewUserBookingDetailsScreen::class.java)
                intent.putExtra(binding.root.context.getString(R.string.booking_id), data.booking_id)
                intent.putExtra(binding.root.context.getString(R.string.category_id), data.category_id)
                intent.putExtra(binding.root.context.getString(R.string.user_id), UserUtils.getUserId(binding.root.context))
                binding.root.context.startActivity(intent)
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