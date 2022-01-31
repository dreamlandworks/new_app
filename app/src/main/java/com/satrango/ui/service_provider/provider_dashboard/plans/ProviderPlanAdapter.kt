package com.satrango.ui.service_provider.provider_dashboard.plans

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ProviderChoosePlanRowBinding
import com.satrango.ui.service_provider.provider_dashboard.drawer_menu.my_account.ProviderPaymentListener
import com.satrango.ui.service_provider.provider_dashboard.plans.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.UserPlanListener

class ProviderPlanAdapter(private val list: List<Data>, private val activatedPlan: Int, private val providerPaymentListener: ProviderPaymentListener): RecyclerView.Adapter<ProviderPlanAdapter.ViewHolder>() {

    class ViewHolder(binding: ProviderChoosePlanRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {
            if (data.premium_tag == "Yes") {
                binding.premiumName.text = data.name
                binding.premiumDesc.text = data.description
                binding.premiumPlatformFeePerBooking.text = data.platform_fee_per_booking
                binding.premiumNoOfBidsPerMonth.text = data.bids_per_month
                binding.premiumSealedBidsPerMonth.text = data.sealed_bids_per_month
                binding.premiumPremiumTag.text = data.premium_tag
                binding.premiumCustomerSupport.text = data.customer_support
                binding.premiumAmount.text = data.amount + "/"
                binding.premiumAmountPeriod.text = data.period
                binding.regularCard.visibility = View.GONE
            } else {
                binding.regularName.text = data.name
                binding.regularDesc.text = data.description
                binding.platformFeePerBooking.text = data.platform_fee_per_booking
                binding.sealedBids.text = data.sealed_bids_per_month
                binding.premiumTag.text = data.premium_tag
                binding.regularCustomerSupport.text = data.customer_support
                binding.regularAmount.text = data.amount
                binding.premiumCard.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ProviderChoosePlanRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.premiumCard.setOnClickListener {
            providerPaymentListener.loadPayment(list[position])
        }
        if (activatedPlan == 2) {
            holder.binding.premiumText.text = "Activated"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}