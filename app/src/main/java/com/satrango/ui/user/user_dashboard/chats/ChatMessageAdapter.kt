package com.satrango.ui.user.user_dashboard.chats

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.MessageChatRowBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.utils.UserUtils
import java.text.SimpleDateFormat
import java.util.*


class ChatMessageAdapter(val list: List<ChatMessageModel>, val chatInterface: ChatInterface) :
    RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    class ViewHolder(binding: MessageChatRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MessageChatRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (data.sentBy == UserUtils.getUserId(holder.binding.incomingMessage.context)) {
            holder.binding.incomingLayout.visibility = View.GONE
            holder.binding.outGoingMessage.text = data.message
            try {
                holder.binding.outGoingTime.text =
                    SimpleDateFormat("hh:mm a").format(Date(data.datetime.toLong()))
            } catch (e: Exception) {
                holder.binding.outGoingTime.text = data.datetime
            }
            holder.binding.outgoingLayout.visibility = View.VISIBLE
            if (data.unseen == "0") {
                holder.binding.readIcon.setImageResource(R.drawable.un_read_icon)
            }
            if (data.unseen == "1") {
                holder.binding.readIcon.setImageResource(R.drawable.read_icon)
            }
            when (data.type) {
                holder.binding.incomingLayout.context.getString(R.string.text) -> {
                    holder.binding.outGoingMessage.text = data.message
                }
                holder.binding.incomingLayout.context.getString(R.string.image) -> {
                    holder.binding.outGoingMessage.visibility = View.GONE
                    holder.binding.outgoingImage.visibility = View.VISIBLE
                    Glide.with(holder.binding.outgoingImage).load(data.message)
                        .error(R.drawable.images).into(holder.binding.outgoingImage)
                }
                holder.binding.incomingLayout.context.getString(R.string.pdf) -> {
                    holder.binding.outGoingMessage.text = "Pdf File"
                    holder.binding.outGoingMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_baseline_picture_as_pdf_24,
                        0
                    )
                }
            }
        } else {
            holder.binding.outgoingLayout.visibility = View.GONE
            holder.binding.incomingMessage.text = data.message
            try {
                holder.binding.incomingTime.text =
                    SimpleDateFormat("hh:mm a").format(Date(data.datetime.toLong()))
            } catch (e: Exception) {
                holder.binding.incomingTime.text = data.datetime
            }
            holder.binding.incomingLayout.visibility = View.VISIBLE
            when (data.type) {
                holder.binding.outgoingLayout.context.getString(R.string.text) -> {
                    holder.binding.incomingMessage.text = data.message
                }
                holder.binding.incomingLayout.context.getString(R.string.image) -> {
                    holder.binding.outGoingMessage.visibility = View.GONE
                    holder.binding.incomingImage.visibility = View.VISIBLE
                    Glide.with(holder.binding.incomingImage).load(data.message)
                        .error(R.drawable.images).into(holder.binding.incomingImage)
                }
                holder.binding.incomingLayout.context.getString(R.string.pdf) -> {
                    holder.binding.incomingMessage.text = "Pdf File"
                    holder.binding.incomingMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_baseline_picture_as_pdf_24,
                        0
                    )

                }
            }
        }
        holder.binding.outgoingImage.setOnClickListener {
            chatInterface.showFile(data.message, data.type)
        }
        holder.binding.outGoingMessage.setOnClickListener {
            if (data.type == holder.binding.incomingMessage.context.getString(R.string.pdf)) {
                chatInterface.downloadFile(
                    data.message,
                    holder.binding.outgoingImage.context.getString(R.string.pdf)
                )
            }
        }
        holder.binding.incomingImage.setOnClickListener {
            chatInterface.showFile(data.message, data.type)
        }
        holder.binding.incomingMessage.setOnClickListener {
            if (data.type == holder.binding.incomingMessage.context.getString(R.string.pdf)) {
                chatInterface.downloadFile(
                    data.message,
                    holder.binding.outgoingImage.context.getString(R.string.pdf)
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}