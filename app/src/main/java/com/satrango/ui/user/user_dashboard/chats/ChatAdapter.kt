package com.satrango.ui.user.user_dashboard.chats

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.ObservableSnapshotArray
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.satrango.R
import com.satrango.databinding.ChatRowBinding
import com.satrango.ui.user.user_dashboard.chats.models.ChatModel
import com.satrango.ui.user.user_dashboard.chats.models.ChatsModel
import com.satrango.utils.UserUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(var options: FirebaseRecyclerOptions<ChatsModel>) :
    FirebaseRecyclerAdapter<ChatsModel, ChatAdapter.ViewHolder>(options) {

    class ViewHolder(binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatsModel) {
        Glide.with(holder.binding.profileImage).load(model.profile_image).error(R.drawable.images).into(holder.binding.profileImage)
        holder.binding.userName.text = model.username
        holder.binding.time.text = SimpleDateFormat("hh:mm a").format(Date(model.datetime))
        holder.binding.root.setOnClickListener {
            UserUtils.selectedChat(holder.binding.root.context, Gson().toJson(model))
            holder.binding.root.context.startActivity(Intent(holder.binding.root.context, ChatScreen::class.java))
        }
        when(model.type) {
            holder.binding.count.context.getString(R.string.text) -> {
                holder.binding.lastMessage.text = model.last_message
            }
            holder.binding.count.context.getString(R.string.pdf) -> {
                holder.binding.lastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clip_icon_24, 0, 0, 0)
                holder.binding.lastMessage.text = holder.binding.lastMessage.context.getString(R.string.pdf)
            }
            holder.binding.count.context.getString(R.string.image) -> {
                holder.binding.lastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_camera_alt_24, 0, 0, 0)
                holder.binding.lastMessage.text = holder.binding.lastMessage.context.getString(R.string.image)
            }
        }
        if (model.sent_by != UserUtils.getUserId(holder.binding.count.context)) {
            holder.binding.lastMessage.setTextColor(holder.binding.count.context.getColor(R.color.blue))
        } else {
            holder.binding.count.visibility = View.GONE
        }
    }

}