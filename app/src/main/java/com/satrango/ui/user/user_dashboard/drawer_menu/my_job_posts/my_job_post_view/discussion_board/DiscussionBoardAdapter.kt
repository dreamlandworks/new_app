package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R

class DiscussionBoardAdapter(private val list: List<String>) :
    RecyclerView.Adapter<DiscussionBoardAdapter.ViewHolder>() {
    var FROM_USER = 0
    var FROM_PROVIDERS = 1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == FROM_USER) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.discussion_board_user_row, parent, false))
        } else {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.discussion_board_sp_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

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