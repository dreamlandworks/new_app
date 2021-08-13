package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.AttachmentRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.bookings.view_booking_details.models.Attachment

class JobDetailsAttachmentsAdapter(private val list: List<Attachment>) :
    RecyclerView.Adapter<JobDetailsAttachmentsAdapter.ViewHolder>() {

    class ViewHolder(binding: AttachmentRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        @SuppressLint("SetTextI18n")
        fun bind(attachment: Attachment) {
            val binding = binding
            Glide.with(binding.image).load(RetrofitBuilder.BASE_URL + attachment.file_name).into(binding.image)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(AttachmentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}