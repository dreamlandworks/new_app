package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.JobDetailsRowBinding
import com.satrango.ui.user.bookings.view_booking_details.models.JobDetail

class JobDetailsAdapter(private val list: List<JobDetail>, private val categoryId: String) :
    RecyclerView.Adapter<JobDetailsAdapter.ViewHolder>() {

    class ViewHolder(binding: JobDetailsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        @SuppressLint("SetTextI18n")
        fun bind(jobDetails: JobDetail) {
            binding.jobDescription.text = jobDetails.job_description
            binding.jobLocation.text = jobDetails.city + ", " + jobDetails.state + ", " + jobDetails.zipcode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(JobDetailsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        if (categoryId == "2") {
            holder.binding.locationText.visibility = View.GONE
            holder.binding.jobLocation.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}