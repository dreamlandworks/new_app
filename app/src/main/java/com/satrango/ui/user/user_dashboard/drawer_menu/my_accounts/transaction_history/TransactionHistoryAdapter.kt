package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.TransactionHistoryRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.transaction_history.models.Data

class TransactionHistoryAdapter(private val list: List<Data>): RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

    class ViewHolder(binding: TransactionHistoryRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {

            binding.date.text = data.date.split(" ")[0]
            binding.note.text = data.transaction_name
            binding.amount.text = "Rs ${data.amount}"

            if (data.payment_status == "Success") {
                binding.date.setTextColor(Color.parseColor("#00cf0b"))
                binding.note.setTextColor(Color.parseColor("#00cf0b"))
                binding.amount.setTextColor(Color.parseColor("#00cf0b"))
            } else {
                binding.date.setTextColor(Color.parseColor("#ff3300"))
                binding.note.setTextColor(Color.parseColor("#ff3300"))
                binding.amount.setTextColor(Color.parseColor("#ff3300"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TransactionHistoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}