package com.satrango.ui.user.user_dashboard.user_alerts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.UserAlertRowBinding
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserAlertsAdapter(private val list: List<Data>, private val alertType: String): RecyclerView.Adapter<UserAlertsAdapter.ViewHolder>() {

    class ViewHolder(binding: UserAlertRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        @SuppressLint("SimpleDateFormat")
        fun bind(data: Data, alertType: String) {
            binding.rowTitle.text = data.description
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val outputTime: String = formatter.format(parser.parse(data.created_on))
            binding.rowTime.text = outputTime

            if (alertType == "1") {
                binding.rowLayout.visibility = View.GONE
            }
            if (alertType == "2") {
                binding.rowLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(UserAlertRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], alertType)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}