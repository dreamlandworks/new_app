package com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.RepliesRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.settings.requests.models.Reply

class ComplaintRepliesAdapter(private val list: List<Reply>) :
    RecyclerView.Adapter<ComplaintRepliesAdapter.ViewHolder>() {

    class ViewHolder(binding: RepliesRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        fun bind(reply: Reply) {
            Glide.with(binding.profilePic.context).load(R.drawable.circlelogo).into(binding.profilePic)
            binding.description.text = reply.action_taken
            binding.time.text = reply.created_on
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RepliesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}