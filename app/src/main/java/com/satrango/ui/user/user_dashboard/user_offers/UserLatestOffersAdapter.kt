package com.satrango.ui.user.user_dashboard.user_offers

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.UserLatestOfferRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOffersScreen
import com.satrango.ui.user.user_dashboard.search_service_providers.search_service_provider.SearchServiceProvidersScreen
import com.satrango.ui.user.user_dashboard.user_offers.models.Data
import com.satrango.utils.UserUtils.isProvider

class UserLatestOffersAdapter(private val list: List<Data>) :
    RecyclerView.Adapter<UserLatestOffersAdapter.ViewHolder>() {

    class ViewHolder(binding: UserLatestOfferRowBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {
            Glide.with(binding.image.context).load(data.offer_image)
                .into(binding.image)
            if (isProvider(binding.image.context)) {
                binding.title.setBackgroundColor(binding.title.context.resources.getColor(R.color.purple_500))
                binding.title.text = "Activate"
            } else {
                binding.image.setOnClickListener {
                    SearchServiceProvidersScreen.offerId = data.id.toInt()
                    binding.image.context.startActivity(
                        Intent(
                            binding.title.context,
                            SearchServiceProvidersScreen::class.java
                        )
                    )
                }
            }
        }

        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserLatestOfferRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}