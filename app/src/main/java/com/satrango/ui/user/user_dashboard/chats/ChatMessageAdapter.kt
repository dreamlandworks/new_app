package com.satrango.ui.user.user_dashboard.chats

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.MessageChatRowBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.utils.UserUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ChatMessageAdapter(val list: List<ChatMessageModel>): RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    class ViewHolder(binding: MessageChatRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MessageChatRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (data.sentBy == UserUtils.getUserId(holder.binding.incomingMessage.context)) {
            holder.binding.incomingLayout.visibility = View.GONE
            holder.binding.outGoingMessage.text = data.message
            try {
                holder.binding.outGoingTime.text = SimpleDateFormat("hh:mm a").format(Date(data.datetime.toLong()))
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
        } else {
            holder.binding.outgoingLayout.visibility = View.GONE
            holder.binding.incomingMessage.text = data.message
            try {
                holder.binding.incomingTime.text = SimpleDateFormat("hh:mm a").format(Date(data.datetime.toLong()))
            } catch (e: Exception) {
                holder.binding.incomingTime.text = data.datetime
            }
            holder.binding.incomingLayout.visibility = View.VISIBLE
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