package com.satrango.ui.user_dashboard.drawer_menu.my_profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.UserProfileAddressRowBinding
import com.satrango.ui.user_dashboard.drawer_menu.my_profile.models.Address
import com.satrango.ui.user_dashboard.drawer_menu.my_profile.models.UserProfileAddressInterface

class UserProfileAddressAdapter(
    private val list: List<Address>,
    private val userProfileAddressInterface: UserProfileAddressInterface
): RecyclerView.Adapter<UserProfileAddressAdapter.ViewHolder>() {

    class ViewHolder(binding: UserProfileAddressRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(address: Address) {
            binding.addressText.text = "${address.locality}, ${address.city}, ${address.state}, ${address.country}, ${address.zipcode}"
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(UserProfileAddressRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.closeBtn.setOnClickListener { userProfileAddressInterface.deleteAddress(list[position].id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}