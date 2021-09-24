package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderMybidsRowBinding
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bids.models.JobPostDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen

class ProviderMyBidsAdapter(private val list: List<JobPostDetail>): RecyclerView.Adapter<ProviderMyBidsAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderMybidsRowBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: JobPostDetail) {
            binding.title.text = data.title
            binding.id.text = data.post_job_id
            if (data.expires_in == "0") {
                binding.expiresOnText.visibility = View.GONE
                binding.expiresIn.visibility = View.GONE
            } else {
                binding.expiresIn.text = data.expires_in
            }
            binding.priceRange.text = "Rs ${data.amount}"
            binding.scheduleDate.text = data.scheduled_date
            if (data.job_post_description.isNotEmpty()) {
                binding.jobLocation.text = data.job_post_description[0].locality
                binding.description.text = data.job_post_description[0].job_description
            }
            binding.bidCount.text = data.total_bids.toString()
            binding.avgAmount.text = data.average_bids_amount
            binding.card.setOnClickListener {
                MyJobPostViewScreen.FROM_PROVIDER = true
                val intent = Intent(binding.root.context, MyJobPostViewScreen::class.java)
                intent.putExtra("booking_id", data.booking_id)
                intent.putExtra("category_id", data.category_id)
                intent.putExtra("post_job_id", data.post_job_id)
                binding.root.context.startActivity(intent)
            }

        }

        val binding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProviderMybidsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}