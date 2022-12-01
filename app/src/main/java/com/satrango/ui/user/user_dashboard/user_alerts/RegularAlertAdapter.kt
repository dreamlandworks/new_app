package com.satrango.ui.user.user_dashboard.user_alerts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.R
import com.satrango.databinding.UserAlertRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_alerts.models.Regular
import com.satrango.utils.UserUtils
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RegularAlertAdapter(
    private val list: List<Regular>
): RecyclerView.Adapter<RegularAlertAdapter.ViewHolder>() {

    class ViewHolder(binding: UserAlertRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(data: Regular) {
            binding.rowTitle.text = data.description.trim()
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter = SimpleDateFormat("hh:mm a dd/MM/yyyy")
            val outputTime: String = formatter.format(parser.parse(data.created_on))
            binding.rowTime.text = outputTime
            binding.rowLayout.visibility = View.GONE
            Glide.with(binding.profilePic).load(data.profile_pic)
                .error(UserUtils.getUserProfilePic(binding.profilePic.context)).into(binding.profilePic)
            if (data.status == "1") {
                binding.rootLayout.setBackgroundColor(binding.rootLayout.context.resources.getColor(R.color.grey_800))
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(UserAlertRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}