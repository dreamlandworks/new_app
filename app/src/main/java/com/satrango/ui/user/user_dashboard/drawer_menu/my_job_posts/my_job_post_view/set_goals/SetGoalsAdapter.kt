package com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.SetGoalsRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.DataX

class SetGoalsAdapter(private val list: List<DataX>, private val setGoalsListener: SetGoalsListener): RecyclerView.Adapter<SetGoalsAdapter.ViewHolder>() {
    class ViewHolder(binding: SetGoalsRowBinding): RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SetGoalsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.installmentText.text = (position + 1).toString() + " - Installment"
        val data = list[position]
        holder.binding.installmentAmount.text = "Rs. " + data.amount.toString() + "/-"
        holder.binding.amountPercentage.text = "${data.goal_id * 10}% Completion"
        holder.binding.closeBtn.setOnClickListener {  }
        holder.itemView.setOnClickListener { setGoalsListener.selectedInstallment(list[position]) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}