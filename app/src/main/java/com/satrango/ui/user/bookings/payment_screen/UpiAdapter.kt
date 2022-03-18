package com.satrango.ui.user.bookings.payment_screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.UpiRowBinding
import com.satrango.ui.user.bookings.payment_screen.models.Data

class UpiAdapter(private val list: List<Data>, private val upiInterface: UpiInterface): RecyclerView.Adapter<UpiAdapter.ViewHolder>() {

    class ViewHolder(binding: UpiRowBinding, upiInterface: UpiInterface): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {
            binding.upiId.text = data.upi
            binding.upiRadioBtn.isChecked = data.isChecked
            if (data.isChecked) {
                binding.amount.visibility = View.VISIBLE
                if (PaymentScreen.walletBalanceChecked) {
                    val remainingBalance = PaymentScreen.finalAmount - PaymentScreen.finalWalletBalance.toInt()
                    if (remainingBalance > 0) {
                        binding.amount.text = "Rs. $remainingBalance"

                    }
                } else {
                    binding.amount.text = "Rs. ${PaymentScreen.finalAmount}"
                }
            }
            binding.upiRadioBtn.setOnCheckedChangeListener { compoundButton, checked ->
                if (checked) {
                    upiInterface.updateList(adapterPosition)
                }
            }
        }
        val binding = binding
        val upiInterface = upiInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UpiRowBinding.inflate(LayoutInflater.from(parent.context), parent, false), upiInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}