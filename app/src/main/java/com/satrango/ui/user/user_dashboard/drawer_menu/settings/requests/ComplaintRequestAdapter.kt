package com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.ComplaintRequestRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.models.Complaint

class ComplaintRequestAdapter(private val list: List<Complaint>) :
    RecyclerView.Adapter<ComplaintRequestAdapter.ViewHolder>() {

    class ViewHolder(binding: ComplaintRequestRowBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(complaint: Complaint) {
            binding.description.text = complaint.description
            binding.status.text = complaint.status
            binding.requestId.text = "Complaint ID: ${complaint.id}"
            binding.repliesRecyclerView.adapter = ComplaintRepliesAdapter(complaint.replies)
        }

        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ComplaintRequestRowBinding.inflate(
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