package com.satrango.ui.user.user_dashboard.user_offers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.UserReferralOfferRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.offers.ProviderOffersScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.refer_earn.UserReferAndEarn
import com.satrango.ui.user.user_dashboard.user_offers.models.Data

class UserReferralOffersAdapter(private val list: List<Data>): RecyclerView.Adapter<UserReferralOffersAdapter.ViewHolder>() {

    class ViewHolder(binding: UserReferralOfferRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            Glide.with(binding.image.context).load(RetrofitBuilder.BASE_URL + data.offer_image).into(binding.image)
            if (ProviderOffersScreen.FROM_PROVIDER) {
                binding.knowMore.setBackgroundResource(R.drawable.purple_sharp_border_out_line)
                binding.knowMore.setTextColor(binding.knowMore.context.resources.getColor(R.color.purple_500))
                binding.share.setBackgroundColor(binding.share.context.resources.getColor(R.color.purple_500))
            } else {
                binding.knowMore.setOnClickListener {
                    binding.root.context.startActivity(Intent(binding.root.context, UserReferAndEarn::class.java))
                }
                binding.share.setOnClickListener {
                    binding.root.context.startActivity(Intent(binding.root.context, UserReferAndEarn::class.java))
                }
            }
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