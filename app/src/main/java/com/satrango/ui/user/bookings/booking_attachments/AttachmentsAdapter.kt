package com.satrango.ui.user.bookings.booking_attachments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.AttachmentRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bids.place_bid.ProviderPlaceBidScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.Attachment
import com.satrango.utils.UserUtils.isProvider
import java.util.*
import kotlin.collections.ArrayList


class AttachmentsAdapter(private val list: ArrayList<Attachment>, private val attachmentsListener: AttachmentsListener): RecyclerView.Adapter<AttachmentsAdapter.ViewHolder>() {

    class ViewHolder(binding: AttachmentRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        private fun base64ToImage(base64String: String): Bitmap {
            val imageBytes: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

        fun bind(imagePath: Attachment) {
            when (imagePath.id.lowercase(Locale.getDefault())) {
                "doc" -> {
                    Glide.with(binding.image).load(R.drawable.doc).error(R.drawable.doc).into(binding.image)
                }
                "docx" -> {
                    Glide.with(binding.image).load(R.drawable.docx).error(R.drawable.docx).into(binding.image)
                }
                "html" -> {
                    Glide.with(binding.image).load(R.drawable.html).error(R.drawable.html).into(binding.image)
                }
                "htm" -> {
                    Glide.with(binding.image).load(R.drawable.htm).error(R.drawable.htm).into(binding.image)
                }
                "odx" -> {
                    Glide.with(binding.image).load(R.drawable.odx).error(R.drawable.odx).into(binding.image)
                }
                "pdf" -> {
                    Glide.with(binding.image).load(R.drawable.pdf).error(R.drawable.pdf).into(binding.image)
                }
                "xls" -> {
                    Glide.with(binding.image).load(R.drawable.xls).error(R.drawable.xls).into(binding.image)
                }
                "xlsx" -> {
                    Glide.with(binding.image).load(R.drawable.xlsx).error(R.drawable.xlsx).into(binding.image)
                }
                "ppt" -> {
                    Glide.with(binding.image).load(R.drawable.ppt).error(R.drawable.ppt).into(binding.image)
                }
                "pptx" -> {
                    Glide.with(binding.image).load(R.drawable.pptx).error(R.drawable.pptx).into(binding.image)
                }
                "txt" -> {
                    Glide.with(binding.image).load(R.drawable.txt).error(R.drawable.txt).into(binding.image)
                }
                "jpg" -> {
                    Glide.with(binding.image).load(base64ToImage(imagePath.file_name)).error(base64ToImage(imagePath.file_name)).into(binding.image)
                }
                "jpeg" -> {
                    Glide.with(binding.image).load(base64ToImage(imagePath.file_name)).error(base64ToImage(imagePath.file_name)).into(binding.image)
                }
                "png" -> {
                    Glide.with(binding.image).load(base64ToImage(imagePath.file_name)).error(base64ToImage(imagePath.file_name)).into(binding.image)
                }
            }
            when (imagePath.file_type.lowercase(Locale.getDefault())) {
                "doc" -> {
                    Glide.with(binding.image).load(R.drawable.doc).error(R.drawable.doc).into(binding.image)
                }
                "docx" -> {
                    Glide.with(binding.image).load(R.drawable.docx).error(R.drawable.docx).into(binding.image)
                }
                "html" -> {
                    Glide.with(binding.image).load(R.drawable.html).error(R.drawable.html).into(binding.image)
                }
                "htm" -> {
                    Glide.with(binding.image).load(R.drawable.htm).error(R.drawable.htm).into(binding.image)
                }
                "odx" -> {
                    Glide.with(binding.image).load(R.drawable.odx).error(R.drawable.odx).into(binding.image)
                }
                "pdf" -> {
                    Glide.with(binding.image).load(R.drawable.pdf).error(R.drawable.pdf).into(binding.image)
                }
                "xls" -> {
                    Glide.with(binding.image).load(R.drawable.xls).error(R.drawable.xls).into(binding.image)
                }
                "xlsx" -> {
                    Glide.with(binding.image).load(R.drawable.xlsx).error(R.drawable.xlsx).into(binding.image)
                }
                "ppt" -> {
                    Glide.with(binding.image).load(R.drawable.ppt).error(R.drawable.ppt).into(binding.image)
                }
                "pptx" -> {
                    Glide.with(binding.image).load(R.drawable.pptx).error(R.drawable.pptx).into(binding.image)
                }
                "txt" -> {
                    Glide.with(binding.image).load(R.drawable.txt).error(R.drawable.txt).into(binding.image)
                }
                "jpg" -> {
                    Glide.with(binding.image).load(imagePath.file_name).error(R.drawable.img).into(binding.image)
                }
                "jpeg" -> {
                    Glide.with(binding.image).load(imagePath.file_name).error(R.drawable.img).into(binding.image)
                }
                "png" -> {
                    Glide.with(binding.image).load(imagePath.file_name).error(R.drawable.img).into(binding.image)
                }
            }
            if (MyJobPostViewScreen.myJobPostViewScreen) {
                binding.closeBtn.visibility = View.GONE
            }

            if (isProvider(binding.closeBtn.context)) {
                if (ProviderPlaceBidScreen.FROM_EDIT_BID) {
                    binding.closeBtn.visibility = View.VISIBLE
                } else {
                    binding.closeBtn.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AttachmentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.binding.closeBtn.context, "Selected", Toast.LENGTH_SHORT).show()
        }
        holder.binding.closeBtn.setOnClickListener {
            attachmentsListener.deleteAttachment(position, list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}