package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.MyJobPostViewScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionDetail
import com.satrango.utils.UserUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DiscussionListAdapter(
    private val list: List<DiscussionDetail>,
    private val context: Context,
    private val discussionBoardInterface: DiscussionBoardInterface
) : RecyclerView.Adapter<DiscussionListAdapter.ViewHolder>() {

    private var USER_VIEW = 0
    private var PROVIDER_VIEW = 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage = view.findViewById<CircleImageView>(R.id.profilePic)
        val layout = view.findViewById<LinearLayout>(R.id.layout)
        val userName = view.findViewById<TextView>(R.id.userName)
        val likeBtn = view.findViewById<ImageView>(R.id.likeBtn)
        val message = view.findViewById<TextView>(R.id.message)
        val dateTime = view.findViewById<TextView>(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == USER_VIEW) {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.discussion_board_user_row,
                    parent,
                    false
                )
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.discussion_board_sp_row,
                    parent,
                    false
                )
            )
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (!data.profile_pic.isNullOrBlank()) {
            Glide.with(holder.profileImage).load(RetrofitBuilder.BASE_URL + data.profile_pic)
                .error(R.drawable.images).into(
                    holder.profileImage
                )
        }
        holder.userName.text = data.fname + " " + data.lname
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("hh:mm a")
        val formatedTime = formatter.format(parser.parse(data.created_dts))
        holder.dateTime.text = formatedTime
        holder.message.text = data.comment

        if (!data.likes_count.isNullOrBlank()) {
            if (data.likes_count.toInt() > 0) {
                holder.likeBtn.setImageResource(R.drawable.ic_blue_filled_thumb_up_24)
            } else {
                holder.likeBtn.setImageResource(R.drawable.ic_outline_thumb_up_24)
            }
        }

        holder.likeBtn.setOnClickListener {
            discussionBoardInterface.likeClicked(data.id, position)
        }

        if (MyJobPostViewScreen.FROM_PROVIDER) {
            holder.layout.setBackgroundResource(R.drawable.purple_out_line)
            holder.profileImage.borderColor = holder.profileImage.context.resources.getColor(R.color.purple_500)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].users_id == UserUtils.getUserId(context)) {
            return USER_VIEW
        } else {
            return PROVIDER_VIEW
        }
    }
}