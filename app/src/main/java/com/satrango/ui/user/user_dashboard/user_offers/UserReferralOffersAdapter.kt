package com.satrango.ui.user.user_dashboard.user_offers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.UserReferralOfferRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_offers.models.Data

class UserReferralOffersAdapter(private val list: List<Data>): RecyclerView.Adapter<UserReferralOffersAdapter.ViewHolder>() {

    class ViewHolder(binding: UserReferralOfferRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            Glide.with(binding.image.context).load(RetrofitBuilder.BASE_URL + data.offer_image).into(binding.image)
        }
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserReferralOfferRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}