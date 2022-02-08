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
class UserAlertsAdapter(
    private val list: List<Data>,
    private val alertType: String,
    private val alertsInterface: AlertsInterface
): RecyclerView.Adapter<UserAlertsAdapter.ViewHolder>() {

    class ViewHolder(binding: UserAlertRowBinding, alertsInterface: AlertsInterface) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        val alertsInterface = alertsInterface

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(data: Data, alertType: String) {
            binding.rowTitle.text = data.description.trim()
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
            if ((data.alert_id == "9" && data.action == "2") || (data.alert_id == "10" && data.action == "2") || (data.alert_id == "7" && data.action == "2")) {
                binding.rowReviewNowBtn.text = "Accept"
                binding.rowReviewLaterBtn.text = "Reject"
                binding.rowReviewLaterBtn.visibility = View.VISIBLE
                binding.rowReviewNowBtn.visibility = View.VISIBLE
            }
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
        fun onClickAction(data: Data) {
            if (data.alert_id == "2" && data.action == "2") {
                alertsInterface.divertToInstallmentsScreen(data.booking_id, data.post_job_id.toInt())
            }
            if (data.alert_id == "7" && data.action == "2") {
                alertsInterface.extraDemandDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt())
            }
            if (data.alert_id == "8" && data.action == "2") {
                alertsInterface.divertToViewBidDetailsScreen(data.booking_id, data.bid_sp_id.toInt(), data.bid_id.toInt())
            }
            if (data.alert_id == "9" && data.action == "2") {
                alertsInterface.rescheduleUserAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt(), data.reschedule_id.toInt())
//                if (UserAlertScreen.FROM_PROVIDER) {
//                    alertsInterface.rescheduleUserStatusCancelDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt(), data.reschedule_user_id.toInt())
//                } else {
//                    alertsInterface.rescheduleSPAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt(), data.reschedule_id.toInt())
//                }
//                if (!UserAlertScreen.FROM_PROVIDER) {
//                } else {
//                    alertsInterface.rescheduleSPStatusCancelDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt(), data.reschedule_id.toInt())
//                }
            }
            if (data.alert_id == "10" && data.action == "2") {
                alertsInterface.rescheduleUserAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt(), data.reschedule_id.toInt())
//                if (!UserAlertScreen.FROM_PROVIDER) {
//                } else {
//                    alertsInterface.rescheduleSPStatusCancelDialog(data.booking_id.toInt(), data.category_id.toInt(), data.reschedule_user_id.toInt(), data.reschedule_id.toInt())
//                }
            }
            if (data.alert_id == "3") {
                alertsInterface.divertToOfferScreen()
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
        holder.bind(data, alertType)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}