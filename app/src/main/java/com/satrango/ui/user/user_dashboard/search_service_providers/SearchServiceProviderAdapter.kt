package com.satrango.ui.user.user_dashboard.search_service_providers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.SearchServiceProviderRowBinding
import com.satrango.ui.user.user_dashboard.search_service_providers.models.Data

class SearchServiceProviderAdapter(private val list: List<Data>) :
    RecyclerView.Adapter<SearchServiceProviderAdapter.ViewHolder>() {

    class ViewHolder(binding: SearchServiceProviderRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        fun bindValues(data: Data) {
            Glide.with(binding.profilePic).load(data.profile_pic).into(binding.profilePic)
            binding.userRating.text = data.id
            binding.userName.text = data.fname
            binding.userOccupation.text = data.profession
            binding.userDescription.text = data.about_me
            binding.costPerHour.text = data.per_hour
            binding.userDistance.text = "${data.address}, ${data.city}, ${data.state}, ${data.country}, ${data.postcode}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(SearchServiceProviderRowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindValues(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}