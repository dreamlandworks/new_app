package com.satrango.ui.user.bookings.booking_address

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.UserBookingAddressRowBinding
import com.satrango.ui.user.bookings.booking_date_time.BookingDateAndTimeScreen
import com.satrango.ui.user.bookings.booking_date_time.MonthsInterface
import com.satrango.ui.user.bookings.booking_date_time.MonthsModel
import com.satrango.utils.UserUtils.isProvider
import java.util.*

class UserBookingAddressAdapter(
    private val list: ArrayList<MonthsModel>,
    private val monthsInterface: MonthsInterface,
    private val listType: String
): RecyclerView.Adapter<UserBookingAddressAdapter.ViewHolder>() {

    class ViewHolder(binding: UserBookingAddressRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserBookingAddressRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[holder.adapterPosition].isSelected) {
            holder.binding.rowLayout.setBackgroundResource(R.drawable.blue_bg_sm)
            holder.binding.locationText.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.binding.myLocation.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            if (isProvider(holder.binding.locationText.context)) {
                holder.binding.rowLayout.setBackgroundResource(R.drawable.purple_bg_sm)
            }
        }
        if (listType == "AA") {
            holder.binding.myLocation.text = list[holder.adapterPosition].month.split(", ")[0]
            holder.binding.locationText.text = list[holder.adapterPosition].month
            holder.itemView.setOnClickListener {
                monthsInterface.selectedMonth(holder.adapterPosition, list[holder.adapterPosition].month, listType)
                notifyItemChanged(holder.adapterPosition)
            }
        } else {
            holder.itemView.setOnClickListener {
                monthsInterface.selectedMonth(holder.adapterPosition, list[holder.adapterPosition].month, listType)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}