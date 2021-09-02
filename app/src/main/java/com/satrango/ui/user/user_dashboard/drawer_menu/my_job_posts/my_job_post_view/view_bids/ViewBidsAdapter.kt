package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.ViewBidsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.ViewBidDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.BidDetail

class ViewBidsAdapter(private val list: List<BidDetail>): RecyclerView.Adapter<ViewBidsAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewBidsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(bidDetail: BidDetail) {
            if (!bidDetail.sp_profile.isNullOrBlank()) {
                Glide.with(binding.profilePic).load(RetrofitBuilder.BASE_URL + bidDetail.sp_profile).into(binding.profilePic)
            }
            binding.title.text = bidDetail.proposal
            binding.bidPrice.text = bidDetail.amount
            binding.time.text = bidDetail.esimate_time + " " + bidDetail.estimate_type
            binding.jobDescription.text = bidDetail.about_me
            binding.jobsCount.text = bidDetail.jobs_completed.toString()
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ViewBidDetailsScreen::class.java)
                intent.putExtra("bidId", bidDetail.bid_id)
                intent.putExtra("spId", bidDetail.sp_id)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewBidsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}