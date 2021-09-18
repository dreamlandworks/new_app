package com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderReleaseGoalsRowBinding
import com.satrango.ui.service_provider.provider_dashboard.provider_dashboard.my_bookings.provider_booking_details.release_goals.models.GoalsInstallmentsDetail

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
            binding.totalCost.text = data.amount
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.requestBtn.setOnClickListener {
            providerReleaseGoalsInterface.sendRequest(list[position])
            holder.binding.requestBtn.text = "Requested"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}