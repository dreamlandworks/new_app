package com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.LeaderboardRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.dashboard.leaderboard.models.Data
import com.satrango.utils.loadProfileImage

class LeaderboardAdapter(private val list: List<Data>): RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(binding: LeaderboardRowBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {
            binding.apply {
                rank.text = "#${data.rank}"
                if (data.profile_pic.isNullOrBlank()) {
                    Glide.with(root.context).load(R.drawable.images).into(profileImage)
                } else {
                    Glide.with(root.context).load(RetrofitBuilder.BASE_URL + data.profile_pic).into(profileImage)
                }
                spName.text = "${data.fname} ${data.lname}"
                professionName.text = data.profession
                pointsCount.text = "${data.points_count} Points"
                rating.text = data.rating
                audience.text = data.total_people

            }
        }

        val binding = binding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LeaderboardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}