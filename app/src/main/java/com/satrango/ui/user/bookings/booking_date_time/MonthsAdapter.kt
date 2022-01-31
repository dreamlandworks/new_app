package com.satrango.ui.user.bookings.booking_date_time

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.MonthsRowBinding
import com.satrango.databinding.UserBookingAddressRowBinding
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class MonthsAdapter(
    private val list: ArrayList<MonthsModel>,
    private val monthsInterface: MonthsInterface,
    private val listType: String
): RecyclerView.Adapter<MonthsAdapter.ViewHolder>() {

    class ViewHolder(binding: MonthsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(model: MonthsModel) {
            try {
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")
                val date: LocalDate = LocalDate.parse(model.month, formatter)
                val dow: DayOfWeek = date.dayOfWeek
                val output: String = dow.getDisplayName(TextStyle.SHORT, Locale.US)

                binding.title.text = output
                binding.description.text = model.month.split("-")[2]
            } catch (e: Exception) {
                val h_mm_a = SimpleDateFormat("h:mm a")
                val hh_mm_ss = SimpleDateFormat("HH:mm:ss")
                try {
                    val fromTime = hh_mm_ss.parse(model.month.split("to")[0])
                    val toTime = hh_mm_ss.parse(model.month.split("to")[1].trim())
                    binding.title.text = h_mm_a.format(fromTime!!) + " - " + h_mm_a.format(toTime!!)
                    binding.description.visibility = View.GONE
                    binding.card.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MonthsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(list[position])
        if (list[position].isSelected) {
            holder.binding.rowLayout.setBackgroundResource(R.drawable.blue_bg_sm)
            holder.binding.description.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.binding.title.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            if (BookingDateAndTimeScreen.FROM_PROVIDER) {
                holder.binding.rowLayout.setBackgroundResource(R.drawable.purple_bg_sm)
            }
        }
        if (listType == "AA") {
            holder.binding.description.visibility = View.GONE
            holder.binding.title.text = list[position].month
            holder.binding.card.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            holder.itemView.setOnClickListener {
                monthsInterface.selectedMonth(position, list[holder.adapterPosition].month, listType)
                notifyItemChanged(position)
            }
        }
        holder.itemView.setOnClickListener {
            monthsInterface.selectedMonth(position, list[position].month, listType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return getItemId(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}