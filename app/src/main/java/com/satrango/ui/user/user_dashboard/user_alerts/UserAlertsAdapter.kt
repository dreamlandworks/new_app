package com.satrango.ui.user.user_dashboard.user_alerts

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.UserAlertRowBinding
import com.satrango.ui.service_provider.provider_dashboard.alerts.ProviderAlertsScreen
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.ViewBidDetailsScreen
import com.satrango.ui.user.user_dashboard.user_alerts.models.Data
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserAlertsAdapter(
    private val list: List<Data>,
    private val alertType: String,
    private val alertsInterface: AlertsInterface
) : RecyclerView.Adapter<UserAlertsAdapter.ViewHolder>() {

    class ViewHolder(binding: UserAlertRowBinding) : RecyclerView.ViewHolder(binding.root) {
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
        return ViewHolder(
            UserAlertRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data, alertType)
        holder.binding.root.setOnClickListener {
            if (data.alert_id == "2" && data.action == "2") {
                alertsInterface.divertToInstallmentsScreen(data.booking_id, data.post_job_id.toInt())
            }
            if (data.alert_id == "7" && data.action == "2") {
                alertsInterface.extraDemandDialog(data.booking_id.toInt(), data.category_id.toInt(), data.booking_id.toInt())
            }
            if (data.alert_id == "8" && data.action == "2") {
                alertsInterface.divertToViewBidDetailsScreen(data.booking_id, data.bid_sp_id.toInt(), data.bid_id.toInt())
            }
            if (data.alert_id == "9" && data.action == "2") {
                if (UserAlertScreen.FROM_PROVIDER) {
                    alertsInterface.rescheduleUserStatusCancelDialog(data.booking_id.toInt(), data.category_id.toInt(), data.booking_id.toInt(), data.booking_id.toInt())
                } else {
                    alertsInterface.rescheduleSPAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.booking_id.toInt(), data.booking_id.toInt())
                }
            }
            if (data.alert_id == "10" && data.action == "2") {
                if (UserAlertScreen.FROM_PROVIDER) {
                    alertsInterface.rescheduleUserAcceptRejectDialog(data.booking_id.toInt(), data.category_id.toInt(), data.booking_id.toInt(), data.booking_id.toInt())
                } else {
                    alertsInterface.rescheduleSPStatusCancelDialog(data.booking_id.toInt(), data.category_id.toInt(), data.booking_id.toInt(), data.booking_id.toInt())
                }
            }
            if (data.alert_id == "3") {
                alertsInterface.divertToOfferScreen()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}