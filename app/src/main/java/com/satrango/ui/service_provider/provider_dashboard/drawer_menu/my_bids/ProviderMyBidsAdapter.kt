package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderMybidsRowBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.models.JobPostDetail
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.ProviderPlaceBidScreen
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.ProviderViewPlacedBidScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider

class ProviderMyBidsAdapter(private val list: List<JobPostDetail>) :
    RecyclerView.Adapter<ProviderMyBidsAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderMybidsRowBinding) : RecyclerView.ViewHolder(binding.root) {

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
            if (data.category_id == null) {

            } else {
                when (data.category_id) {
                    "1" -> {
                        binding.bookingType.text = "Single Move"
                    }
                    "2" -> {
                        binding.bookingType.text = "Blue Collar"
                    }
                    "3" -> {
                        binding.bookingType.text = "Multi Move"
                    }
                }
            }
            binding.priceRange.text = "Rs ${data.range_slots}"
            binding.scheduleDate.text = data.scheduled_date
            if (data.category_id.toInt() != 2) {
                if (data.job_post_description.isNotEmpty()) {
                    binding.jobLocation.text =
                        "${data.job_post_description[0].locality}, ${data.job_post_description[0].city}"
                    binding.description.text = data.job_post_description[0].job_description
                }
            }
            binding.bidCount.text = data.total_bids.toString()
            binding.avgAmount.text = data.average_bids_amount

            when (data.bid_type) {
                "Open" -> {
                    binding.card.setOnClickListener {
                        ProviderPlaceBidScreen.FROM_EDIT_BID = false
//                        ProviderPlaceBidScreen.EDIT_BID_ID = data.bid_id
                        val intent = Intent(binding.root.context, ProviderViewPlacedBidScreen::class.java)
                        intent.putExtra("booking_id", data.booking_id)
                        intent.putExtra("category_id", data.category_id)
                        intent.putExtra("post_job_id", data.post_job_id)
                        intent.putExtra("user_id", data.booking_user_id)
                        intent.putExtra("title", data.title)
                        intent.putExtra("expiresIn", data.expires_in)
                        intent.putExtra("bidRange", data.bid_range_name)
                        UserUtils.savePostJobId(binding.atText.context, data.post_job_id.toInt())
                        binding.root.context.startActivity(intent)
                    }

                    binding.editBidBtn.text = "Edit Bid"
                    binding.editBidBtn.setOnClickListener {
                        ProviderPlaceBidScreen.FROM_EDIT_BID = true
                        ProviderPlaceBidScreen.EDIT_BID_ID = data.bid_id
                        val intent = Intent(binding.root.context, ProviderPlaceBidScreen::class.java)
                        intent.putExtra("expiresIn", data.expires_in)
                        intent.putExtra("bidRanges", data.range_slots)
                        intent.putExtra("title", data.title)
                        UserUtils.savePostJobId(binding.atText.context, data.post_job_id.toInt())
                        ProviderPlaceBidScreen.bookingId = data.booking_id.toInt()
                        binding.root.context.startActivity(intent)

                    }
                }
                "Awarded", "Expired" -> {
                    binding.editBidBtn.visibility = View.GONE
                    binding.card.setOnClickListener {
                        ProviderPlaceBidScreen.FROM_AWARDED = true
                        ProviderPlaceBidScreen.FROM_EDIT_BID = false
                        MyJobPostViewScreen.bookingId = data.booking_id.toInt()
                        MyJobPostViewScreen.categoryId = data.category_id.toInt()
                        MyJobPostViewScreen.userId = data.booking_user_id.toInt()
                        UserUtils.savePostJobId(binding.atText.context, data.post_job_id.toInt())
                        val intent = Intent(binding.root.context, MyJobPostViewScreen::class.java)
                        binding.root.context.startActivity(intent)
                    }
                }
                else -> {
//                    binding.editBidBtn.text = "Place Bid"
                    binding.editBidBtn.text = "Bid Placed"
//                    binding.editBidBtn.setOnClickListener {
//                        ProviderPlaceBidScreen.FROM_AWARDED = false
//                        ProviderPlaceBidScreen.FROM_EDIT_BID = false
//                        val intent = Intent(binding.root.context, ProviderPlaceBidScreen::class.java)
//                        intent.putExtra("expiresIn", data.expires_in)
//                        intent.putExtra("bidRanges", data.range_slots)
//                        intent.putExtra("title", data.title)
//                        UserUtils.savePostJobId(
//                            binding.atText.context,
//                            data.post_job_id.toInt()
//                        )
//                        ProviderPlaceBidScreen.bookingId = data.booking_id.toInt()
//                        binding.root.context.startActivity(intent)
//                    }
                    binding.card.setOnClickListener {
                        isProvider(binding.atText.context, true)
                        ProviderPlaceBidScreen.FROM_AWARDED = false
                        ProviderPlaceBidScreen.FROM_EDIT_BID = false
                        MyJobPostViewScreen.categoryId = data.category_id.toInt()
                        MyJobPostViewScreen.bookingId = data.booking_id.toInt()
                        MyJobPostViewScreen.userId = data.booking_user_id.toInt()
                        UserUtils.savePostJobId(
                            binding.atText.context,
                            data.post_job_id.toInt()
                        )
                        val intent = Intent(binding.root.context, MyJobPostViewScreen::class.java)
                        binding.root.context.startActivity(intent)
                    }
                }
            }

        }

        val binding = binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ProviderMybidsRowBinding.inflate(
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