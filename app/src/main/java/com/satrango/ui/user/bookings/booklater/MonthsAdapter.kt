package com.satrango.ui.user.bookings.booklater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

        fun bind(title: String) {
            binding.title.text = title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MonthsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position].month)
        if (list[position].isSelected) {
            holder.binding.rowLayout.setBackgroundResource(R.drawable.category_bg)
            holder.binding.addressText.setBackgroundResource(R.drawable.category_bg)
            holder.binding.addressText.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.binding.note.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.binding.title.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
        } else {
            holder.binding.rowLayout.setBackgroundResource(R.drawable.blue_out_line)
            holder.binding.addressText.setBackgroundResource(R.drawable.blue_out_line)
            holder.binding.addressText.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
            holder.binding.note.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
            holder.binding.title.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
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
    }

    override fun getItemCount(): Int {
        return list.size
    }
}