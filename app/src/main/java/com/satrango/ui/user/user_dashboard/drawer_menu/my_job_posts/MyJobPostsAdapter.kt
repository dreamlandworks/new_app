package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.MyJobPostsRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.JobPostDetail
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.ViewBidsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.PostJobDateTimeScreen
import com.satrango.utils.UserUtils

class MyJobPostsAdapter(private val list: List<JobPostDetail>, private val status: String) :
    RecyclerView.Adapter<MyJobPostsAdapter.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    class ViewHolder(binding: MyJobPostsRowBinding) : RecyclerView.ViewHolder(binding.root) {

        val binding = binding

        fun bind(jobPostDetail: JobPostDetail, status: String) {
            binding.title.text = jobPostDetail.title
            binding.id.text = jobPostDetail.post_job_ref_id
            binding.expiresOn.text = jobPostDetail.expires_in
            binding.costPerHour.text = jobPostDetail.range_slots
            binding.scheduleDate.text = jobPostDetail.scheduled_date
            binding.jobDescription.text = jobPostDetail.details[0].job_description
            binding.bidCount.text = jobPostDetail.total_bids.toString()
            binding.avgAmount.text = jobPostDetail.average_bids_amount
            if (!jobPostDetail.details[0].locality.isNullOrBlank()) {
                binding.jobLocation.text = jobPostDetail.details[0].locality
            } else if (!jobPostDetail.details[0].city.isNullOrBlank()) {
                binding.jobLocation.text = jobPostDetail.details[0].city
            } else {
                binding.locationLayout.visibility = View.GONE
            }
            if (status == "Pending") {
                binding.editBtn.visibility = View.VISIBLE
                binding.awardedBtn.visibility = View.GONE
//                binding.awardedBtn.text = "Award"
                binding.editBtn.setOnClickListener {
                    when (jobPostDetail.category_id) {
                        "1" -> {
                            UserUtils.setFromJobPostSingleMove(binding.editBtn.context, true)
                            UserUtils.setFromJobPostBlueCollar(binding.editBtn.context, false)
                            UserUtils.setFromJobPostMultiMove(binding.editBtn.context, false)
                        }
                        "2" -> {
                            UserUtils.setFromJobPostSingleMove(binding.editBtn.context, false)
                            UserUtils.setFromJobPostBlueCollar(binding.editBtn.context, true)
                            UserUtils.setFromJobPostMultiMove(binding.editBtn.context, false)
                        }
                        "3" -> {
                            UserUtils.setFromJobPostSingleMove(binding.editBtn.context, false)
                            UserUtils.setFromJobPostBlueCollar(binding.editBtn.context, false)
                            UserUtils.setFromJobPostMultiMove(binding.editBtn.context, true)
                        }
                    }
                    UserUtils.EDIT_MY_JOB_POST = true
                    ViewBidsScreen.bookingId = jobPostDetail.booking_id.toInt()
                    ViewBidsScreen.categoryId = jobPostDetail.category_id.toInt()
                    ViewBidsScreen.postJobId = jobPostDetail.post_job_id.toInt()
                    binding.editBtn.context.startActivity(
                        Intent(
                            binding.editBtn.context,
                            PostJobDateTimeScreen::class.java
                        )
                    )
                }
            } else if (status == "Awarded") {
                binding.editBtn.visibility = View.GONE
                binding.awardedBtn.visibility = View.VISIBLE
                binding.awardedBtn.text = "My Bookings"
            } else if (status == "Expired") {
                binding.editBtn.visibility = View.GONE
                binding.awardedBtn.visibility = View.VISIBLE
                binding.awardedBtn.text = "Post Again"
            }
            binding.root.setOnClickListener {
                when (jobPostDetail.category_id) {
                    "1" -> {
                        UserUtils.setFromJobPostSingleMove(binding.editBtn.context, true)
                        UserUtils.setFromJobPostBlueCollar(binding.editBtn.context, false)
                        UserUtils.setFromJobPostMultiMove(binding.editBtn.context, false)
                    }
                    "2" -> {
                        UserUtils.setFromJobPostSingleMove(binding.editBtn.context, false)
                        UserUtils.setFromJobPostBlueCollar(binding.editBtn.context, true)
                        UserUtils.setFromJobPostMultiMove(binding.editBtn.context, false)
                    }
                    "3" -> {
                        UserUtils.setFromJobPostSingleMove(binding.editBtn.context, false)
                        UserUtils.setFromJobPostBlueCollar(binding.editBtn.context, false)
                        UserUtils.setFromJobPostMultiMove(binding.editBtn.context, true)
                    }
                }
                val intent = Intent(binding.root.context, MyJobPostViewScreen::class.java)
                intent.putExtra("booking_id", jobPostDetail.booking_id)
                intent.putExtra("category_id", jobPostDetail.category_id)
                intent.putExtra("post_job_id", jobPostDetail.post_job_id)
                binding.root.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MyJobPostsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], status)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}