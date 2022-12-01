package com.satrango.ui.user.bookings.booking_attachments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.UserPopularServicesRowBinding
import com.satrango.databinding.ViewFilesRowBinding
import com.satrango.ui.user.bookings.booking_attachments.models.Attachments
import com.satrango.ui.user.bookings.booking_attachments.models.ViewFilesResModel
import java.text.SimpleDateFormat
import java.util.*

class ViewFilesAdapter(val options: List<ViewFilesResModel>) :
    RecyclerView.Adapter<ViewFilesAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewFilesRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewFilesRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = options[position]
        Glide.with(holder.binding.userProfilePic).load(model.profile_pic).error(R.drawable.images)
            .into(holder.binding.userProfilePic)
        holder.binding.userNameText.text = model.username
        holder.binding.userDate.text =
            SimpleDateFormat("dd/MM/yyyy hh:mm a").format(Date(model.datetime.toLong()))
        holder.binding.recyclerView.layoutManager = LinearLayoutManager(
            holder.binding.recyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.binding.recyclerView.adapter = FilesAdapter(model.attachments)
        holder.binding.downloadBtn.setOnClickListener {
            for (url in model.attachments) {
                downloadPdfOrImage(holder.binding.downloadBtn.context, url.url, url.type)
            }
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }

    private fun downloadPdfOrImage(context: Context, url: String, fileType: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val dateTime = Date().time.toString()
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setDescription("Downloading...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setVisibleInDownloadsUi(true)
        request.setTitle("Image_$dateTime")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS.toString(),
            "$dateTime.$fileType"
        )
        downloadManager!!.enqueue(request)
        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
    }
}

class FilesAdapter(val attachments: List<Attachments>) :
    RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    class ViewHolder(binding: UserPopularServicesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.rowTitle.text = attachments[position].type
        when (attachments[position].type.toLowerCase()) {
            "doc" -> {
                holder.binding.rowImage.setImageResource(R.drawable.doc)
            }
            "docx" -> {
                holder.binding.rowImage.setImageResource(R.drawable.docx)
            }
            "html" -> {
                holder.binding.rowImage.setImageResource(R.drawable.html)
            }
            "htm" -> {
                holder.binding.rowImage.setImageResource(R.drawable.htm)
            }
            "odx" -> {
                holder.binding.rowImage.setImageResource(R.drawable.odx)
            }
            "pdf" -> {
                holder.binding.rowImage.setImageResource(R.drawable.pdf)
            }
            "xls" -> {
                holder.binding.rowImage.setImageResource(R.drawable.xls)
            }
            "xlsx" -> {
                holder.binding.rowImage.setImageResource(R.drawable.xlsx)
            }
            "ppt" -> {
                holder.binding.rowImage.setImageResource(R.drawable.ppt)
            }
            "pptx" -> {
                holder.binding.rowImage.setImageResource(R.drawable.pptx)
            }
            "txt" -> {
                holder.binding.rowImage.setImageResource(R.drawable.txt)
            }
            "jpg" -> {
                holder.binding.rowImage.setImageResource(R.drawable.img)
            }
            "jpeg" -> {
                holder.binding.rowImage.setImageResource(R.drawable.img)
            }
            "png" -> {
                holder.binding.rowImage.setImageResource(R.drawable.img)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserPopularServicesRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

}
