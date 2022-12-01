package com.satrango.ui.user.user_dashboard.drawer_menu.my_accounts.fund_transfer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.AllBanksDetailsRowBinding
import com.satrango.utils.UserUtils.isProvider

class AllBankDetailsAdapter(
    private val list: List<UserBankAccount>,
    private val userBankDetailsInterface: AllBankDetailsInterface
) :
    RecyclerView.Adapter<AllBankDetailsAdapter.ViewHolder>() {

    class ViewHolder(binding: AllBanksDetailsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(userBankAccount: UserBankAccount) {
            binding.title.text = "A/c No: ${userBankAccount.account_no}"
            binding.description.text = "IFSC: ${userBankAccount.ifsc_code}"
            if (isProvider(binding.description.context)) {
                binding.rowLayout.setBackgroundResource(R.drawable.purple_out_line)
                binding.title.setTextColor(binding.title.context.resources.getColor(R.color.purple_500))
                binding.description.setTextColor(binding.title.context.resources.getColor(R.color.purple_500))
            }
            if (userBankAccount.isSelected) {
                if (isProvider(binding.description.context)) {
                    binding.rowLayout.setBackgroundResource(R.drawable.provider_btn_bg)
                } else {
                    binding.rowLayout.setBackgroundResource(R.drawable.category_bg)
                }
                binding.title.setTextColor(binding.title.context.resources.getColor(R.color.white))
                binding.description.setTextColor(binding.title.context.resources.getColor(R.color.white))
            }
        }

        val binding = binding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            AllBanksDetailsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.root.setOnClickListener {
            userBankDetailsInterface.selectedAccount(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}