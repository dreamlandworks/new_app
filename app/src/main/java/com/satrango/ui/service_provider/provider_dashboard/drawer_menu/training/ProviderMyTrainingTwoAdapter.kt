package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderTrainingRowBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.RecentVideo
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.training.model.RecommendedVideo

class ProviderMyTrainingTwoAdapter(val list: List<RecommendedVideo>): RecyclerView.Adapter<ProviderMyTrainingTwoAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderTrainingRowBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: RecommendedVideo) {
            binding.pointText.text = "${data.points} Points"
        }
        val binding = binding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ProviderTrainingRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}