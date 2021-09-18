package com.satrango.ui.user.bookings.view_booking_details.installments_request

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.databinding.GoalInstallmentsRowBinding
import com.satrango.ui.user.bookings.view_booking_details.installments_request.models.GoalsInstallmentsDetail

class UserInstallmentsRequestAdapter(
    private val list: List<GoalsInstallmentsDetail>,
    private val userInstallmentsRequestInterface: UserInstallmentsRequestInterface
) :
    RecyclerView.Adapter<UserInstallmentsRequestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            GoalInstallmentsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class ViewHolder(binding: GoalInstallmentsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        @SuppressLint("SetTextI18n")
        fun bind(data: GoalsInstallmentsDetail) {
            binding.text.text = "Installment ${data.inst_no}"
        }

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
        holder.binding.acceptBtn.setOnClickListener {
            userInstallmentsRequestInterface.acceptInstallment(
                list[position]
            )
        }
        holder.binding.rejectBtn.setOnClickListener {
            userInstallmentsRequestInterface.rejectInstallment(
                list[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}