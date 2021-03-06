package com.satrango.ui.user.user_dashboard.user_offers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.UserExpiryOfferRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOffersScreen
import com.satrango.ui.user.user_dashboard.user_offers.models.Data
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider

class UserExpiryOffersAdapter(private val list: List<Data>) :
    RecyclerView.Adapter<UserExpiryOffersAdapter.ViewHolder>() {

    class ViewHolder(binding: UserExpiryOfferRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            Glide.with(binding.image.context).load(data.offer_image).into(binding.image)
            if (isProvider(binding.claimNow.context)) {
                binding.yesDeo.setBackgroundResource(R.drawable.purple_sharp_border_out_line)
                binding.yesDeo.setTextColor(binding.yesDeo.context.resources.getColor(R.color.purple_500))
                binding.claimNow.setBackgroundColor(binding.claimNow.context.resources.getColor(R.color.purple_500))
            }
        }

        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserExpiryOfferRowBinding.inflate(
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