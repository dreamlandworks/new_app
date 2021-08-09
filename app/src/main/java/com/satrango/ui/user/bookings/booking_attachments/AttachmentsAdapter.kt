package com.satrango.ui.user.bookings.booking_attachments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.AttachmentRowBinding

class AttachmentsAdapter(private val list: ArrayList<String>, private val attachmentsListener: AttachmentsListener): RecyclerView.Adapter<AttachmentsAdapter.ViewHolder>() {

    class ViewHolder(binding: AttachmentRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        fun bind(imagePath: String) {
            Glide.with(binding.image).load(imagePath).into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AttachmentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.closeBtn.setOnClickListener {
            attachmentsListener.deleteAttachment(position, list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}