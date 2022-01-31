package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderTrainingRowBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.RecentVideo


class ProviderMyTrainingOneAdapter(
    private val list: List<RecentVideo>
) : RecyclerView.Adapter<ProviderMyTrainingOneAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderTrainingRowBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: RecentVideo) {
            binding.pointText.text = "${data.points} Points"
            binding.root.setOnClickListener {
                YoutubePlayerScreen.recentVideoDetails = data
                YoutubePlayerScreen.recommendedVideoDetails = null
                YoutubePlayerScreen.watchedVideoDetails = null
                binding.root.context.startActivity(Intent(binding.root.context, YoutubePlayerScreen::class.java))
            }
        }

        val binding = binding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ProviderTrainingRowBinding.inflate(
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