package com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderReleaseGoalsRowBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_bookings.provider_booking_details.release_goals.models.GoalsInstallmentsDetail

class ProviderReleaseGoalsAdapter(
    private val list: List<GoalsInstallmentsDetail>,
    private val providerReleaseGoalsInterface: ProviderReleaseGoalsInterface
) : RecyclerView.Adapter<ProviderReleaseGoalsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ProviderReleaseGoalsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class ViewHolder(binding: ProviderReleaseGoalsRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: GoalsInstallmentsDetail) {
            binding.text.text = "Installment ${data.inst_no}"
            binding.totalCost.text = "Rs.${data.amount}/-"
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        if (list[position].inst_request_status_id == "33") {
            holder.binding.requestBtn.text = "Requested"
        }
        holder.binding.requestBtn.setOnClickListener {
            if (holder.binding.requestBtn.text.toString().trim() == "Requested") {
                Toast.makeText(
                    holder.binding.requestBtn.context,
                    "Already Requested",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                providerReleaseGoalsInterface.sendRequest(list[position])
                holder.binding.requestBtn.text = "Requested"
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}