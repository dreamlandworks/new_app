package com.satrango.ui.user.user_dashboard.chats

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ChatRowBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatMessageModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.utils.UserUtils
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private var chats: List<ChatModel>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(binding: ChatRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ChatRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = chats[position]
        Glide.with(holder.binding.profileImage).load(data.profileImage).error(R.drawable.images).into(holder.binding.profileImage)
        holder.binding.userName.text = data.userName
        holder.binding.time.text = SimpleDateFormat("hh:mm a").format(Date(data.chats[data.chats.size - 1].datetime.toLong()))
        if (data.sentBy != UserUtils.getUserId(holder.binding.count.context)) {
            holder.binding.lastMessage.text = data.chats[data.chats.size - 1].message
            holder.binding.lastMessage.setTextColor(holder.binding.count.context.getColor(R.color.blue))
//            val temp = ArrayList<ChatMessageModel>()
//            for (chat in data.chats) {
//                if (chat.unseen == 0) {
//                    temp.add(chat)
//                }
//            }
//            holder.binding.count.text = temp.size.toString()
            holder.binding.count.visibility = View.GONE
        } else {
            holder.binding.lastMessage.text = data.lastMessage
            holder.binding.count.visibility = View.GONE
        }
        holder.binding.root.setOnClickListener {
            UserUtils.selectedChat(holder.binding.root.context, Gson().toJson(data))
            holder.binding.root.context.startActivity(Intent(holder.binding.root.context, ChatScreen::class.java))
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateChats(list: List<ChatModel>) {
        this.chats = list
        this.notifyDataSetChanged()
    }

}