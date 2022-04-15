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

//    companion object {
//        private fun buildQuery() = FirebaseDatabase.getInstance()
//            .getReferenceFromUrl("https://satrango-37ac9-default-rtdb.firebaseio.com/")
//            .child("users")
//
//        private fun buildOptions(lifecycleOwner: LifecycleOwner) = FirebaseRecyclerOptions.Builder<ChatModel>()
//            .setQuery(buildQuery(), ChatModel::class.java)
//            .setLifecycleOwner(lifecycleOwner)
//            .build()
//
//    }

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
        Glide.with(holder.binding.profileImage).load(model.profile_image).error(R.drawable.images)
            .into(holder.binding.profileImage)
        holder.binding.userName.text = model.username
        holder.binding.time.text = SimpleDateFormat("hh:mm a").format(Date(model.datetime))
        if (model.sent_by != UserUtils.getUserId(holder.binding.count.context)) {
            holder.binding.lastMessage.text = model.last_message
            holder.binding.lastMessage.setTextColor(holder.binding.count.context.getColor(R.color.blue))
//            val temp = ArrayList<ChatMessageModel>()
////            for (chat in data.chats) {
////                if (chat.unseen == 0) {
////                    temp.add(chat)
////                }
////            }
////            holder.binding.count.text = temp.size.toString()
//            holder.binding.count.visibility = View.GONE
        } else {
            holder.binding.lastMessage.text = model.last_message
            holder.binding.count.visibility = View.GONE
        }
            holder.binding.root.setOnClickListener {
                UserUtils.selectedChat(holder.binding.root.context, Gson().toJson(model))
                holder.binding.root.context.startActivity(
                    Intent(
                        holder.binding.root.context,
                        ChatScreen::class.java
                    )
                )
            }
        }

}