package com.satrango.ui.user.bookings.view_booking_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.AttachmentRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment

class JobDetailsAttachmentsAdapter(private val list: List<Attachment>) :
    RecyclerView.Adapter<JobDetailsAttachmentsAdapter.ViewHolder>() {

    class ViewHolder(binding: AttachmentRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(attachment: Attachment) {
            val binding = binding
            binding.closeBtn.visibility = View.GONE
            Glide.with(binding.image).load(attachment.file_name).into(binding.image)
            binding.image.setOnClickListener {
                openFullScreenImageDialog(attachment.file_name)
            }
        }

        private fun openFullScreenImageDialog(imageUrl: String) {
            val dialog = Dialog(binding.image.context)
            val dialogView = LayoutInflater.from(binding.image.context).inflate(R.layout.full_screen_image, null)
            val image = dialogView.findViewById<ImageView>(R.id.fullScreenImage)
            val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
            Glide.with(binding.image.context).load(imageUrl).into(image)
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            closeBtn.setOnClickListener { dialog.dismiss() }
            dialog.setContentView(dialogView)
            dialog.show()
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