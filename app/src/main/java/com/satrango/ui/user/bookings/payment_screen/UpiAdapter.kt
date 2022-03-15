package com.satrango.ui.user.bookings.payment_screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.UpiRowBinding
import com.satrango.ui.user.bookings.payment_screen.models.Data

class UpiAdapter(private val list: List<Data>): RecyclerView.Adapter<UpiAdapter.ViewHolder>() {

    class ViewHolder(binding: UpiRowBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {
            binding.upiId.text = data.upi
            binding.upiRadioBtn.setOnCheckedChangeListener { compoundButton, checked ->
                if (checked) {
                    binding.amount.visibility = View.VISIBLE
                    if (PaymentScreen.walletBalanceChecked) {
                        val remainingBalance = PaymentScreen.finalWalletBalance.toInt() - PaymentScreen.finalAmount
                        if (remainingBalance > 0) {
                            binding.amount.text = "Rs. $remainingBalance"
                        }
                    }
                }
            }
        }
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UpiRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}