package com.satrango.ui.user.user_dashboard.user_offers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.UserOffersRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_offers.models.Data

class UserOffersAdapter(private val list: List<Data>): RecyclerView.Adapter<UserOffersAdapter.ViewHolder>() {

    class ViewHolder(binding: UserOffersRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            Glide.with(binding.offerImage.context).load(RetrofitBuilder.BASE_URL + data.offer_image).into(binding.offerImage)
        }
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserOffersRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}