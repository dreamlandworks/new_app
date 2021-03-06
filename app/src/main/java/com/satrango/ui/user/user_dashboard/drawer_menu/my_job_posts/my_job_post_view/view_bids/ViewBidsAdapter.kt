package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.satrango.R
import com.satrango.databinding.ViewBidsRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.SetGoalsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.ViewBidDetailsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.RejectJobPostStatusReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.BidDetail
import com.satrango.utils.UserUtils.isProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class ViewBidsAdapter(private val list: List<BidDetail>): RecyclerView.Adapter<ViewBidsAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewBidsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(bidDetail: BidDetail) {
            if (!bidDetail.sp_profile.isNullOrBlank()) {
                Glide.with(binding.profilePic).load(bidDetail.sp_profile).into(binding.profilePic)
            }
            binding.title.text = bidDetail.proposal
            binding.bidPrice.text = bidDetail.amount
            binding.time.text = bidDetail.esimate_time + " " + bidDetail.estimate_type
            binding.jobDescription.text = bidDetail.about_me
            binding.jobsCount.text = bidDetail.jobs_completed.toString()

            if (isProvider(binding.awardBtn.context)) {
                binding.layout.setBackgroundResource(R.drawable.purple_out_line)
                binding.awardBtn.visibility = View.GONE
                binding.rejectBtn.visibility = View.GONE
            } else {
                binding.awardBtn.setOnClickListener {
                    ViewBidsScreen.bidPrice = bidDetail.amount.toInt()
                    ViewBidsScreen.bidId = bidDetail.bid_id.toInt()
                    ViewBidsScreen.spId = bidDetail.sp_id.toInt()
                    binding.rejectBtn.context.startActivity(Intent(binding.root.context, SetGoalsScreen::class.java))
                }
                binding.rejectBtn.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val requestBody = RejectJobPostStatusReqModel(
                                ViewBidsScreen.bookingId,
                                RetrofitBuilder.USER_KEY,
                                ViewBidsScreen.postJobId,
                                bidDetail.bid_id.toInt(),
                                29
                            )
                            val response = RetrofitBuilder.getUserRetrofitInstance().rejectJobPostStatus(requestBody)
                            val jsonResponse = JSONObject(response.string())
                            if (jsonResponse.getInt("status") == 200) {
                                binding.rejectBtn.text = "Rejected"
                            } else {
                                Toast.makeText(binding.awardBtn.context, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(binding.awardBtn.context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ViewBidDetailsScreen::class.java)
                ViewBidDetailsScreen.bidId = bidDetail.bid_id.toInt()
                ViewBidDetailsScreen.spId = bidDetail.sp_id.toInt()
                binding.root.context.startActivity(intent)
            }

            if (MyJobPostViewScreen.postStatus == "Awarded" || MyJobPostViewScreen.postStatus == "Expired") {
                binding.rejectBtn.setBackgroundResource(R.drawable.gray_corner)
                binding.awardBtn.setBackgroundResource(R.drawable.gray_corner)
                binding.rejectBtn.setTextColor(binding.awardBtn.context.resources.getColor(R.color.gray))
                binding.awardBtn.setTextColor(binding.awardBtn.context.resources.getColor(R.color.gray))
                binding.awardBtn.isEnabled = false
                binding.rejectBtn.isEnabled = false
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