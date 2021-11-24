package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.ChoosePlanRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.PostJobPlansResModel

class UserPlanAdapter(
    private val response: PostJobPlansResModel,
    private val list: List<Data>,
    private val activatedPlan: Int,
    private val userPlanListener: UserPlanListener
) :
    RecyclerView.Adapter<UserPlanAdapter.ViewHolder>() {

    class ViewHolder(binding: ChoosePlanRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: Data, userPlanListener: UserPlanListener) {
            if (data.premium_tag == "Yes") {
                binding.premiumCard.visibility = View.VISIBLE
                binding.regularCard.visibility = View.GONE

                binding.premiumName.text = data.name
                binding.premiumAmount.text = data.amount
                binding.premiumAmountPeriod.text = " / ${data.period}days"
                binding.premiumDesc.text = data.description
                binding.premiumPostsCount.text = data.posts_per_month
                binding.premiumProposals.text = data.proposals_per_post
                binding.premiumCustomerSupport.text = data.customer_support

                binding.premiumCard.setOnClickListener {
                    userPlanListener.loadPayment(data)
                }

            } else {
                binding.premiumCard.visibility = View.GONE
                binding.regularCard.visibility = View.VISIBLE

                binding.regularName.text = data.name
                binding.regularAmount.text = "Free"
                binding.regularDesc.text = data.description
                binding.regularPostsCount.text = data.posts_per_month
                binding.regularProposalsCount.text = data.proposals_per_post
                binding.regularCustomerSupport.text = data.customer_support

                binding.regularCard.setOnClickListener {
                    userPlanListener.loadPayment(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ChoosePlanRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], userPlanListener)
        if (activatedPlan == 2) {
            holder.binding.premiumText.text = "Activated"
            holder.binding.premiumAmount.visibility = View.GONE
            holder.binding.premiumAmountPeriod.text = "Valid till ${response.valid_till_date}"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}