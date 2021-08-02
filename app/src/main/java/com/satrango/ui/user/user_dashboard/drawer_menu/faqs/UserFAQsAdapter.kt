package com.satrango.ui.user.user_dashboard.drawer_menu.faqs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satrango.R
import com.satrango.databinding.FaqRowBinding
import com.satrango.ui.user.user_dashboard.drawer_menu.faqs.models.Data

class UserFAQsAdapter(private val list: List<Data>) :
    RecyclerView.Adapter<UserFAQsAdapter.ViewHolder>() {

    class ViewHolder(binding: FaqRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

        fun bind(data: Data) {
            binding.question.text = data.question
            binding.answer.text = data.answer

            binding.root.setOnClickListener {
                if (binding.answer.visibility == View.VISIBLE) {
                    binding.answer.visibility = View.GONE
                    binding.image.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                } else {
                    binding.answer.visibility = View.VISIBLE
                    binding.image.setImageResource(R.drawable.arrow_forward_icon)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FaqRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}