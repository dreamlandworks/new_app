package com.satrango.ui.user.bookings.booking_date_time

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.MonthsRowBinding

class MonthsAdapter(
    private val list: ArrayList<MonthsModel>,
    private val monthsInterface: MonthsInterface,
    private val listType: String
): RecyclerView.Adapter<MonthsAdapter.ViewHolder>() {

    class ViewHolder(binding: MonthsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(model: MonthsModel) {
            binding.title.text = model.month
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MonthsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        if (list[position].isSelected) {
            holder.binding.rowLayout.setBackgroundResource(R.drawable.category_bg)
            holder.binding.addressText.setBackgroundResource(R.drawable.category_bg)
            holder.binding.addressText.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.binding.title.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            if (BookingDateAndTimeScreen.FROM_PROVIDER) {
                holder.binding.rowLayout.setBackgroundResource(R.drawable.provider_btn_bg)
            }
        } else {
            holder.binding.rowLayout.setBackgroundResource(R.drawable.blue_out_line)
            holder.binding.addressText.setBackgroundResource(R.drawable.blue_out_line)
            holder.binding.addressText.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
            holder.binding.title.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
            if (BookingDateAndTimeScreen.FROM_PROVIDER) {
                holder.binding.rowLayout.setBackgroundResource(R.drawable.purple_out_line)
            }
        }
        if (listType == "AA") {
            holder.binding.rowLayout.visibility = View.GONE
            holder.binding.addressText.visibility = View.VISIBLE
            holder.binding.addressText.text = list[position].month
            holder.itemView.setOnClickListener {
                monthsInterface.selectedMonth(position, listType)
                notifyItemChanged(position)
            }
        }
        holder.itemView.setOnClickListener {
            monthsInterface.selectedMonth(position, listType)
            notifyDataSetChanged()
        }
        if (listType == "T") {
            holder.binding.title.text = list[position].month
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}