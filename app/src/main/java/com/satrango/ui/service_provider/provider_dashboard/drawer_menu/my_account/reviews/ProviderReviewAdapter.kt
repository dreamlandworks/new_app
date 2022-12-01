package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ProviderReviewRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.reviews.models.SpReview

class ProviderReviewAdapter(private val list: List<SpReview>): RecyclerView.Adapter<ProviderReviewAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderReviewRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: SpReview) {
            if (!data.profile_pic.isNullOrBlank()) {
                Glide.with(binding.root.context).load(data.profile_pic).placeholder(R.drawable.images).into(binding.profilePic)
                binding.userName.text = data.fname + " " + data.lname
                binding.rating.text = data.overall_rating
                binding.bookingId.text = "Booking ID: ${data.booking_id}"
                binding.date.text = data.created_dts
                binding.description.text = data.feedback
                binding.professionRating.text = data.professionalism
                binding.behaviourRating.text = data.behaviour
                binding.skillsRating.text = data.skill
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ProviderReviewRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}