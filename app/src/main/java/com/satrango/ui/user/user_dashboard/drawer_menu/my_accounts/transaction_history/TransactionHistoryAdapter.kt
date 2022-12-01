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
            if (data.transaction_type == "Receipt/Credit") {
                binding.amount.text = "Rs ${data.amount} Cr"
                binding.date.setTextColor(Color.parseColor("#00AE48"))
                binding.note.setTextColor(Color.parseColor("#00AE48"))
                binding.amount.setTextColor(Color.parseColor("#00AE48"))
            }
            if (data.transaction_type == "Payment/Debit") {
                binding.amount.text = "Rs ${data.amount} Dr"
                binding.date.setTextColor(Color.parseColor("#D82B00"))
                binding.note.setTextColor(Color.parseColor("#D82B00"))
                binding.amount.setTextColor(Color.parseColor("#D82B00"))
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