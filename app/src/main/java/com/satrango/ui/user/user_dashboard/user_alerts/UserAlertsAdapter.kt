package com.satrango.ui.user.user_dashboard.user_alerts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.satrango.databinding.UserAlertRowBinding
import com.satrango.remote.RetrofitBuilder
import com.satrango.ui.user.user_dashboard.user_alerts.models.Action
import com.satrango.utils.UserUtils
import com.satrango.utils.UserUtils.isProvider
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserAlertsAdapter(
    private val list: List<Action>,
    private val alertsInterface: AlertsInterface
): RecyclerView.Adapter<UserAlertsAdapter.ViewHolder>() {

    class ViewHolder(binding: UserAlertRowBinding, alertsInterface: AlertsInterface) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        val alertsInterface = alertsInterface

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(data: Action) {
            binding.rowTitle.text = data.description.trim()
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val outputTime: String = formatter.format(parser.parse(data.created_on))
            binding.rowTime.text = outputTime
            binding.rowLayout.visibility = View.VISIBLE
            binding.rowReviewNowBtn.text = data.accept_text
            binding.rowReviewLaterBtn.text = data.reject_text
            binding.rowReviewLaterBtn.visibility = View.VISIBLE
            binding.rowReviewNowBtn.visibility = View.VISIBLE
            Glide.with(binding.profilePic).load(data.profile_pic)
                .error(UserUtils.getUserProfilePic(binding.profilePic.context))
                .into(binding.profilePic)
            binding.rowReviewNowBtn.setOnClickListener {
                onClickAction(data)
            }
            binding.rowReviewLaterBtn.setOnClickListener {
                onClickAction(data)
            }
            binding.root.setOnClickListener {
                onClickAction(data)
            }
        }

        @SuppressLint("SetTextI18n")
        fun onClickAction(data: Action) {
            if (data.type_id == "2") {
                alertsInterface.divertToInstallmentsScreen(data.booking_id, data.post_id.toInt())
            }
            if (data.type_id == "3") {
                alertsInterface.divertToOfferScreen()
            }
            if (data.type_id == "7") {
                alertsInterface.extraDemandDialog(data.booking_id.toInt(), data.category_id.toInt(), data.user_id.toInt())
            }
            if (data.type_id == "8") {
                alertsInterface.divertToViewBidDetailsScreen(data.booking_id, data.bid_user_id.toInt(), data.bid_id.toInt())
            }
            if (data.type_id == "9") {
                if (isProvider(binding.profilePic.context)) {
                    alertsInterface.rescheduleUserAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.req_raised_by_id.toInt(), data.reschedule_id.toInt(), data.description.trim())
                } else {
                    alertsInterface.rescheduleUserAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.user_id.toInt(), data.reschedule_id.toInt(), data.description.trim())
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            UserAlertRowBinding.inflate(LayoutInflater.from(parent.context), parent, false), alertsInterface)
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